package com.zbkj.crmeb.sms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.SmsConstants;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.RedisUtil;
import com.utils.RestTemplateUtil;
import com.zbkj.crmeb.sms.model.SmsRecord;
import com.zbkj.crmeb.sms.request.RegisterRequest;
import com.zbkj.crmeb.sms.request.SendSmsVo;
import com.zbkj.crmeb.sms.request.SmsLoginRequest;
import com.zbkj.crmeb.sms.service.SmsRecordService;
import com.zbkj.crmeb.sms.service.SmsService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.service.UserService;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务
 * @author Mr.Zhang
 * @Description SmsRecordServiceImpl 接口实现
 * @since 2020-04-16
 */
@Data
@Service
public class SmsServiceImpl implements SmsService {

    //域名
    private String domain;

    //账号
    private String account;

    //短信token
    private String md5Token;

    //状态
    private Integer status;

    //异步回调地址
    private String payNotifyUrl;

    //异步回调地址uri
    private String payNotifyUri = SmsConstants.SMS_API_PAY_NOTIFY_URI;

    //sms用户token
    private String smsUserToken;


    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private SmsRecordService smsRecordService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    //初始化
//    private void init(){
//        setAccount(systemConfigService.getValueByKey("sms_account")); //获取配置账号
//        String token = systemConfigService.getValueByKey("sms_token"); //获取配置token
//        if(StringUtils.isBlank(token)){
//            throw new CrmebException("请配置短信密码！");
//        }
//        setDomain(systemConfigService.getValueByKey("site_url")); //获取配置域名
//
//        setMd5Token(DigestUtils.md5Hex(getAccount() + DigestUtils.md5Hex(token))); //MD5 token
//        setPayNotifyUrl(getDomain() + getPayNotifyUri()); //支付成功回调地址
//    }

    // 初始化
    private void init() {
        String account = systemConfigService.getValueByKey("sms_account");// 获取配置账号
        if(StringUtils.isBlank(account)){
            throw new CrmebException("请配置短信账号！");
        }
        setAccount(account);

        String token = systemConfigService.getValueByKey("sms_token"); //获取配置token
        if(StringUtils.isBlank(token)){
            throw new CrmebException("请配置短信密码！");
        }
        setDomain(systemConfigService.getValueByKey("site_url")); //获取配置域名

        setMd5Token(DigestUtils.md5Hex(getAccount() + DigestUtils.md5Hex(token))); //MD5 token
        setPayNotifyUrl(getDomain() + getPayNotifyUri()); //支付成功回调地址

        setSmsUserToken(getSmsUserTokenByRedis());
    }


    public String getDomain() {
        if(StringUtils.isBlank(domain)){
            throw new CrmebException("请配置域名！");
        }
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAccount() {
        if(StringUtils.isBlank(account)){
            throw new CrmebException("请配置短信账号！");
        }
        return account;
    }

//    public void setAccount(String account) {
//        this.account = account;
//    }
//
//    public String getMd5Token() { return md5Token; }
//
//    public void setMd5Token(String md5Token) {
//        this.md5Token = md5Token;
//    }
//
//    public Integer getStatus() {
//        return status;
//    }
//
//    public void setStatus(Integer status) {
//        this.status = status;
//    }
//
//    public String getPayNotifyUrl() {
//        return payNotifyUrl;
//    }
//
//    public void setPayNotifyUrl(String payNotifyUrl) {
//        this.payNotifyUrl = payNotifyUrl;
//    }
//
//    public String getPayNotifyUri() {
//        return payNotifyUri;
//    }

    /**
     * 短信注册
     * @param registerRequest 注册参数
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return JSONObject
     */
    @Override
    public JSONObject register(RegisterRequest registerRequest) {
        registerRequest.setPassword(DigestUtils.md5Hex(registerRequest.getPassword()));
        registerRequest.setVerify_code(registerRequest.getCode());
        registerRequest.setUrl(registerRequest.getDomain());
        Map<String, Object> map = CrmebUtil.objectToMap(registerRequest);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.setAll(map);
        JSONObject post = postFrom(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_REGISTER_URI, param, null);
        // token存入Redis
        String accessToken = SmsConstants.SMS_USER_TOKEN_PREFIX.concat(post.getJSONObject("data").getString("access_token"));
        Long expiresIn = post.getJSONObject("data").getLong("expires_in");
        expiresIn = expiresIn - System.currentTimeMillis()/1000;
        redisUtil.set(StrUtil.format(SmsConstants.SMS_USER_TOKEN_REDIS_PREFIX, getMd5Token()), accessToken, expiresIn, TimeUnit.SECONDS);

        // 开通短信服务
        HashMap<String, String> header = getCommonHeader(accessToken);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("sign", registerRequest.getSign());
        postFrom(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_USER_OPEN, params, header);
        //更新配置
        setConfigSmsInfo(registerRequest.getAccount(), registerRequest.getPassword());
        return post;
    }

    /**
     * 判断是否已经登录
     * @return 当前登录者信息
     */
    @Override
    public JSONObject isLogin() {
        init();
        JSONObject jsonObject = new JSONObject();
        if(StringUtils.isBlank(systemConfigService.getValueByKey("sms_account"))){
            jsonObject.put("status", false);
        }else{
            jsonObject.put("status", true);
            jsonObject.put("info",getAccount());
            // 判断登录后刷新本地剩余数据
//            SmsLoginRequest request = new SmsLoginRequest();
//            request.setAccount(getAccount());
//            request.setToken(getMd5Token());
//            account(request);
        }
        return jsonObject;
    }

    /**
     * 注销登录
     * @return 注销登录结果
     */
    @Override
    public void logOut() {
        setConfigSmsInfo("","");
    }

    /**
     * 公共模板
     * @param isHave 是否拥有
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return JSONObject
     */
    @Override
    public JSONObject getPublicTempList(String isHave, PageParamRequest pageParamRequest) {
        init();
        Map<String, Object> map = null != pageParamRequest ? CrmebUtil.objectToMap(pageParamRequest): new HashMap<>();
        if(StringUtils.isNotBlank(isHave)) map.put("is_have", isHave);
        map.put("account", getAccount());
        map.put("token", getMd5Token());
        return post(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_PUBLIC_TEMP_URI, map);
    }

    /**
     * 公共模板
     * @param id id
     * @param tempId 模板id
     * @author Mr.Zhang
     * @since 2020-04-17
     * @return JSONObject
     */
    @Override
    public JSONObject save(String id, String tempId) {
        init();
        Map<String, Object> map = getTokenMap();
        map.put("id", id);
        map.put("tempId", tempId);
        map.put("account", getAccount());
        map.put("token", getMd5Token());
        return post(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_PUBLIC_TEMP_USE_URI, map);
    }

//    /**
//     * 获取账号信息
//     * @author Mr.Zhang
//     * @since 2020-04-17
//     * @return JSONObject
//     */
//    @Override
//    public JSONObject account(SmsLoginRequest smsLoginRequest) {
//        MultiValueMap<String,String> m = new LinkedMultiValueMap<String, String>();
//        String encodeMD5Token = DigestUtils.md5Hex(smsLoginRequest.getAccount()+DigestUtils.md5Hex(smsLoginRequest.getToken()));
//        m.add("account", smsLoginRequest.getAccount());
//        m.add("token", encodeMD5Token);
//        JSONObject jsonObject = postForm(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_USER_INFO_URI,m);
//        setConfigSmsInfo(smsLoginRequest.getAccount(), smsLoginRequest.getToken());
//        if(null != jsonObject.getJSONObject("data")){
//            JSONObject jsonResultTemple = getPublicTempList(null, null);
//            JSONObject data = jsonResultTemple.getJSONObject("data");
//            JSONArray dataArray = data.getJSONArray("data");
//            for (int i = 0; i < dataArray.size(); i++) {
//                JSONObject item = dataArray.getJSONObject(i);
//                if(item.getInteger("is_have") == 0){
//                    JSONObject useResult = save(item.getInteger("id").toString(), item.getInteger("templateid").toString());
//                    checkResult(useResult);
//                }
//            }
//        }
//        jsonObject.put("account",smsLoginRequest.getAccount());
//        return jsonObject;
//    }

    /**
     * 短信用户登录
     * @return
     */
    @Override
    public JSONObject account(SmsLoginRequest smsLoginRequest) {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<String, Object>();
        param.add("account", smsLoginRequest.getAccount());
        String secret = SecureUtil.md5(smsLoginRequest.getAccount() + SecureUtil.md5(smsLoginRequest.getToken()));
        param.add("secret", secret);
        JSONObject jsonObject = postFrom(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_USER_LOGIN, param, null);
        checkResult(jsonObject);
        setConfigSmsInfo(smsLoginRequest.getAccount(), smsLoginRequest.getToken());

        String accessToken = SmsConstants.SMS_USER_TOKEN_PREFIX.concat(jsonObject.getJSONObject("data").getString("access_token"));
        Long expiresIn = jsonObject.getJSONObject("data").getLong("expires_in");
        expiresIn = expiresIn - System.currentTimeMillis()/1000;
        redisUtil.set(StrUtil.format(SmsConstants.SMS_USER_TOKEN_REDIS_PREFIX, secret), accessToken, expiresIn, TimeUnit.SECONDS);
        jsonObject.remove("data");
        jsonObject.put("account",smsLoginRequest.getAccount());
        return jsonObject;
    }

    /**
     * 获取用户详情
     * @return
     */
    @Override
    public JSONObject info() {
//        MultiValueMap<String,String> m = new LinkedMultiValueMap<String, String>();
//        m.add("account", getAccount());
//        m.add("token", getMd5Token());
//        JSONObject jsonObject = postForm(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_USER_INFO_URI,m);
        String accessToken = getSmsUserTokenByRedis();
        HashMap<String, String> header = getCommonHeader(accessToken);
        JSONObject jsonObject = postFrom(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_USER_INFO_URI, null, header);
        JSONObject msg = jsonObject.getJSONObject("msg");
        msg.put("account", getAccount());
        jsonObject.put("data", msg);
//        jsonObject.getJSONObject("data").put("account", getAccount());
        return jsonObject;
    }

    /**
     * 获取价格套餐
     * @param pageParamRequest 分页信息
     * @author Mr.Zhang
     * @since 2020-04-17
     * @return JSONObject
     */
    @Override
    public JSONObject payTempList(PageParamRequest pageParamRequest) {
        init();
//        Map<String, Object> map = mergeToken(CrmebUtil.objectToMap(pageParamRequest));
//        map.put("type", "sms");
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("type", "sms");
//        params.setAll(map);
        String accessToken = getSmsUserTokenByRedis();
        HashMap<String, String> header = getCommonHeader(accessToken);
//        return get(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_PAY_TEMP_LIST_URI, map);
        return postFrom(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_PAY_TEMP_LIST_URI, params, header);
    }

    /**
     * 获取支付二维码
     * @param payType 支付类型
     * @param mealId 套餐id
     * @param price 价格
     * @author Mr.Zhang
     * @since 2020-04-17
     * @return JSONObject
     */
    @Override
    public JSONObject getPayQrCode(String payType, Integer mealId, BigDecimal price, Integer num) {
        init();
//        Map<String, Object> map = getTokenMap();
////        map.put("payType", payType);
//        map.put("pay_type", payType);
////        map.put("mealId", mealId);
//        map.put("meal_id", mealId);
//        map.put("price", price);
////        map.put("uid", getAccount());
////        map.put("token", getMd5Token());
//        map.put("type", "sms");
//        map.put("notify", getPayNotifyUrl());
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("meal_id", mealId);
        params.add("price", price);
        params.add("num", num);
        params.add("type", "sms");
        params.add("pay_type", payType);
        String accessToken = getSmsUserTokenByRedis();
        HashMap<String, String> header = getCommonHeader(accessToken);
//        return post(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_PAY_QR_CODE_URI, map);
        return postFrom(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_PAY_QR_CODE_URI, params, header);
    }

    /**
     * 申请模板消息
     * @param content 内容
     * @param title 主题
     * @param type 类型
     * @author Mr.Zhang
     * @since 2020-04-17
     * @return JSONObject
     */
    @Override
    public JSONObject applyTempMessage(String title, String content, Integer type) {
        init();
//        Map<String, Object> map = getTokenMap();
//        map.put("title", title);
//        map.put("content", content);
//        map.put("type", type);
        String accessToken = getSmsUserTokenByRedis();
        HashMap<String, String> header = getCommonHeader(accessToken);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("title", title);
        params.add("content", content);
        params.add("type", type);
//        return post(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_APPLY_TEMP_MESSAGE_URI, map);
        return postFrom(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_APPLY_TEMP_MESSAGE_URI, params, header);
    }

    /**
     * 短信模板列表
     * @param title 主题
     * @param status 内容
     * @param pageParamRequest 分页信息
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return JSONObject
     */
    @Override
    public JSONObject tempList(String title, Integer status, String type, PageParamRequest pageParamRequest) {
        init();
        Map<String, Object> map = mergeToken(CrmebUtil.objectToMap(pageParamRequest));
        map.put("title", title);
        map.put("status", status);
        map.put("temp_type", type);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.setAll(map);
        String accessToken = getSmsUserTokenByRedis();
        HashMap<String, String> header = getCommonHeader(accessToken);
//        return post(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_TEMP_LIST_URI, map);
        return postFrom(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_TEMP_LIST_URI, param, header);
    }

    /**
     * 添加到短信队列
     * 验证码特殊处理其他的参数自行根据要求处理
     * 参数处理逻辑 {code:value,code1:value1}
     * @param phone String 手机号码
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return boolean
     */
    @Override
    public boolean pushCodeToList(String phone, Integer tag, HashMap<String, Object> pram) {
        //发送手机验证码， 记录到redis  sms_validate_code_手机号
        switch (tag){
            case SmsConstants.SMS_CONFIG_TYPE_VERIFICATION_CODE : // 验证码 特殊处理 code
                //获取短信验证码过期时间
                String codeExpireStr = systemConfigService.getValueByKey(Constants.CONFIG_KEY_SMS_CODE_EXPIRE);
                if(StringUtils.isBlank(codeExpireStr) || Integer.parseInt(codeExpireStr) == 0){
                    codeExpireStr = Constants.NUM_FIVE + "";
                }
                Integer code = CrmebUtil.randomCount(111111, 999999);
                HashMap<String, Object> justPram = new HashMap<>();
                justPram.put("code", code);
                push(phone,SmsConstants.SMS_CONFIG_VERIFICATION_CODE,
                        SmsConstants.SMS_CONFIG_VERIFICATION_CODE_TEMP_ID,false,justPram);

                redisUtil.set(userService.getValidateCodeRedisKey(phone), code, Long.valueOf(codeExpireStr), TimeUnit.MINUTES);//5分钟过期

                break;
            case SmsConstants.SMS_CONFIG_TYPE_LOWER_ORDER_SWITCH : // 支付成功短信提醒 pay_price order_id
                push(phone,SmsConstants.SMS_CONFIG_LOWER_ORDER_SWITCH,
                        SmsConstants.SMS_CONFIG_LOWER_ORDER_SWITCH_TEMP_ID,true,pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_DELIVER_GOODS_SWITCH : // 发货短信提醒 nickname store_name
                push(phone,SmsConstants.SMS_CONFIG_DELIVER_GOODS_SWITCH,
                        SmsConstants.SMS_CONFIG_DELIVER_GOODS_SWITCH_TEMP_ID,true,pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_CONFIRM_TAKE_OVER_SWITCH : // 确认收货短信提醒 order_id store_name
                push(phone,SmsConstants.SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH,
                        SmsConstants.SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID,true,pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_LOWER_ORDER_SWITCH : // 用户下单管理员短信提醒 admin_name order_id
                push(phone,SmsConstants.SMS_CONFIG_ADMIN_LOWER_ORDER_SWITCH,
                        SmsConstants.SMS_CONFIG_ADMIN_LOWER_ORDER_SWITCH_TEMP_ID,true,pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_PAY_SUCCESS_SWITCH : // 支付成功管理员短信提醒 admin_name order_id
                push(phone,SmsConstants.SMS_CONFIG_ADMIN_PAY_SUCCESS_SWITCH,
                        SmsConstants.SMS_CONFIG_ADMIN_PAY_SUCCESS_SWITCH_TEMP_ID,true, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_REFUND_SWITCH : // 用户确认收货管理员短信提醒 admin_name order_id
                push(phone,SmsConstants.SMS_CONFIG_ADMIN_REFUND_SWITCH,
                        SmsConstants.SMS_CONFIG_ADMIN_REFUND_SWITCH_TEMP_ID,true, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_CONFIRM_TAKE_OVER_SWITCH : // 用户发起退款管理员短信提醒 admin_name order_id
                push(phone,SmsConstants.SMS_CONFIG_ADMIN_CONFIRM_TAKE_OVER_SWITCH,
                        SmsConstants.SMS_CONFIG_ADMIN_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID,true, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_PRICE_REVISION_SWITCH : // 改价短信提醒 order_id pay_price
                push(phone,SmsConstants.SMS_CONFIG_PRICE_REVISION_SWITCH,
                        SmsConstants.SMS_CONFIG_PRICE_REVISION_SWITCH_TEMP_ID,true, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ORDER_PAY_FALSE : // 订单未支付 order_id
                push(phone,SmsConstants.SMS_CONFIG_ORDER_PAY_FALSE,
                        SmsConstants.SMS_CONFIG_ORDER_PAY_FALSE_TEMP_ID,false, pram);
                break;
        }
        return true;
    }

    @Override
    public boolean sendCode(SendSmsVo sendSmsVo) {
        String result;
        try{
            if(StringUtils.isBlank(account)){
                init();
            }
//        sendSmsVo.setParam(JSONObject.toJSONString(sendSmsVo.getParam()));
//            result = restTemplateUtil.postJsonData(
//                    SmsConstants.SMS_API_URL + SmsConstants.SMS_API_SEND_URI,
//                    JSONObject.parseObject(JSONObject.toJSONString(sendSmsVo)));
            Map<String, Object> map = (Map<String, Object>) JSONObject.parseObject(sendSmsVo.getParam());
            String accessToken = getSmsUserTokenByRedis();
            HashMap<String, String> header = getCommonHeader(accessToken);

            MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            param.add("phone", sendSmsVo.getMobile());
            param.add("temp_id", sendSmsVo.getTemplate());
            map.entrySet().stream().forEach(entry -> param.add(StrUtil.format(SmsConstants.SMS_COMMON_PARAM_FORMAT, entry.getKey()), entry.getValue()));

            result = restTemplateUtil.postFromUrlencoded(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_SEND_URI, param, header);
        }catch (Exception e){
            //接口请求异常，需要重新发送
            return false;
        }

        if(StringUtils.isBlank(result)){
            //没有拿到数据 需要重新发送
            return false;
        }

        JSONObject joResult;
        try{
            joResult = checkResult(result);
        }catch (Exception e1){
            joResult = JSONObject.parseObject(result);
        }

        int resultCode = joResult.getInteger("status");
        String message = joResult.getString("msg");
        JSONObject data = joResult.getJSONObject("data");
        String smsRecodeId = (data.containsKey("id") ? data.getString("id") : "0");
        sendSmsVo.setContent(data.getString("content"));

        if(resultCode == Constants.HTTPSTATUS_CODE_SUCCESS){
            try{
                // 注意这里的状态仅仅是调用是否成功的状态 需要等待5分钟一周另外一个任务去查询发送状态后再更新status数据
                SmsRecord smsRecord = new SmsRecord(sendSmsVo.getUid(), sendSmsVo.getMobile(),sendSmsVo.getContent(),
                        "", sendSmsVo.getTemplate().toString(),
                        resultCode,Integer.parseInt(smsRecodeId), message);
                smsRecordService.save(smsRecord);
            }catch (Exception e){
                return true;
            }
            // 添加到短信实际发送状态队列
            if(smsRecodeId.length() > 0){
                List<Integer> recordsIds = new ArrayList<>();
                recordsIds.add(Integer.parseInt(smsRecodeId));
                pushByAsyncStatus(StringUtils.join(recordsIds,","));
            }
        }
        return true;
    }



    /**
     * 短信队列消费者
     */
    @Async
    public void consume() {
        Long size = redisUtil.getListSize(SmsConstants.SMS_SEND_KEY);
        logger.info("SmsServiceImpl.consume | size:" + size);
        if(size > 0){
            for (int i = 0; i < size; i++) {
                //如果10秒钟拿不到一个数据，那么退出循环
                Object data = redisUtil.getRightPop(SmsConstants.SMS_SEND_KEY, 10L);
                if(null == data){
                    continue;
                }
                try{
                    JSONObject jsonObject = JSONObject.parseObject(data.toString());
                    SendSmsVo smsVo = JSONObject.toJavaObject(jsonObject, SendSmsVo.class);
//                    SendSmsVo smsVo = JSONObject.toJavaObject(JSONObject.parseObject(data.toString()), SendSmsVo.class);
                    boolean result = sendCode(smsVo);
                    // 捕捉异常或者发送失败存起来下次继续
                    if(!result){
                        redisUtil.lPush(SmsConstants.SMS_SEND_KEY, data);
                    }
                }catch (Exception e){
                    redisUtil.lPush(SmsConstants.SMS_SEND_KEY, data);
                }
            }
        }
    }

    /**
     * 添加待发送消息到redis队列
     * @param phone
     */
    @Override
    public void push(String phone,String tempKey,Integer msgTempId, boolean valid, HashMap<String,Object> mapPram) {
        if(StringUtils.isBlank(phone) || StringUtils.isBlank(tempKey) || msgTempId <= 0){
            return;
        }
        if(StringUtils.isBlank(account)){
            init();
        }
        HashMap<String, Object> mParam = new HashMap<>();
        mParam.put("uid",getAccount());
        mParam.put("token", getMd5Token());
        mParam.put("mobile", phone);
        mParam.put("template", msgTempId);
        mParam.put("param", JSONObject.toJSONString(mapPram));

        if(!valid){
            redisUtil.lPush(SmsConstants.SMS_SEND_KEY, JSONObject.toJSONString(mParam));
            return;
        }
        String value = systemConfigService.getValueByKey(tempKey);
        if(value.equals("1")){
            redisUtil.lPush(SmsConstants.SMS_SEND_KEY, JSONObject.toJSONString(mParam));
        }
    }

    /**
     * 添加短信发送状态同步队列
     * @param recordIds 短信发送id
     */
    @Override
    public void pushByAsyncStatus(String recordIds) {
        if(null == recordIds) return;
        redisUtil.lPush(SmsConstants.SMS_SEND_RESULT_KEY, JSONObject.toJSONString(recordIds));
    }

    /**
     * 注册手机号码
     * @param phone 手机号码
     * @return 发送结果
     */
    @Override
    public JSONObject sendCodeForRegister(String phone) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("phone", phone);
        return postFrom(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_CAPTCHA_URI, map, null);
    }

    /**
     * post请求from表单模式提交
     */
    private JSONObject postFrom(String url, MultiValueMap<String, Object> param, Map<String, String> header) {
        String result = restTemplateUtil.postFromUrlencoded(url, param, header);
        return checkResult(result);
    }


    /**
     * post 请求接口
     * @param url 请求url
     * @param map 参数
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return JSONObject
     */
    private JSONObject post(String url, Map<String, Object> map){
        String result = restTemplateUtil.postMapData(url, map);
        return checkResult(result);
    }

    /**
     * post 请求 json数据
     * @param url url
     * @param jsonPram JSON 参数
     * @return json结果
     */
    private JSONObject postJson(String url, JSONObject jsonPram){
        JSONObject result = restTemplateUtil.postJsonDataAndReturnJson(url, jsonPram);
        return checkResult(result);
    }

    /**
     * get 请求接口
     * @param url 请求url
     * @param map 参数
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return JSONObject
     */
    private JSONObject get(String url, Map map){
        String result = restTemplateUtil.getData(url, map);
        return checkResult(result);
    }

    /**
     * get 请求 没有参数的或者可拼接url参数的
     * @param url url
     * @return 结果
     */
    private JSONObject getData(String url){
        JSONObject result = restTemplateUtil.getData(url);
        return checkResult(result);
    }

    /**
     * post 表单提交
     * @param url
     * @param m
     * @return
     */
    private JSONObject postForm(String url, MultiValueMap<String,String> m){
        String result = restTemplateUtil.postFormData(url,m);
        return checkResult(result);
    }

    /**
     * 检测结构请求返回的数据
     * @param result 接口返回的结果
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return JSONObject
     */
    private JSONObject checkResult(String result){
        if(StringUtils.isBlank(result)){
            throw new CrmebException("短信平台接口异常，没任何数据返回！");
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(result);
        } catch (Exception e) {
            throw new CrmebException("短信平台接口异常！");
        }
        if(SmsConstants.SMS_ERROR_CODE.equals(jsonObject.getInteger("status"))){
            throw new CrmebException("短信平台接口" + jsonObject.getString("msg"));
        }

        return jsonObject;
    }

    /**
     * 检测JSON返回结果
     * @param result
     * @return
     */
    private JSONObject checkResult(JSONObject result){
        if(null == result){
            throw new CrmebException("短信平台接口异常，没任何数据返回！");
        }
        if(SmsConstants.SMS_ERROR_CODE.equals(result.getInteger("status"))){
            throw new CrmebException("短信平台接口" + result.getString("msg"));
        }

        return result;
    }


    /**
     * map token信息
     * @param map 请求url
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return Map<String, Object>
     */
    private Map<String, Object> mergeToken(Map<String, Object> map){
        return CrmebUtil.mergeMap(getTokenMap(), map);
    }

    /**
     * 获取 token account map信息
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return Map<String, Object>
     */
    private Map<String, Object> getTokenMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("account", getAccount());
        map.put("token", getMd5Token());
        return map;
    }

    /**
     * 更新sms配置信息
     * @param account 账号
     * @param password 密码
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    private void setConfigSmsInfo(String account, String password) {
        boolean accountResult = systemConfigService.updateOrSaveValueByName("sms_account", account);
        boolean tokenResult = systemConfigService.updateOrSaveValueByName("sms_token", password);

        if(!accountResult || !tokenResult){
            throw new CrmebException("数据更新失败！");
        }
    }

    /**
     * 获取sms用户token
     */
    private String getSmsUserTokenByRedis() {
        boolean exists = redisUtil.exists(StrUtil.format(SmsConstants.SMS_USER_TOKEN_REDIS_PREFIX, getMd5Token()));
        if (exists) {
            Object token = redisUtil.get(StrUtil.format(SmsConstants.SMS_USER_TOKEN_REDIS_PREFIX, getMd5Token()));
            return token.toString();
        }
        // 缓存中不存在token，重新获取，存入缓存
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("account", getAccount());
        map.add("secret", getMd5Token());
        JSONObject jsonObject = postFrom(SmsConstants.SMS_API_URL + SmsConstants.SMS_API_USER_LOGIN, map, null);
        checkResult(jsonObject);
        String accessToken = "";
        Long expiresIn = 0L;
        accessToken = SmsConstants.SMS_USER_TOKEN_PREFIX.concat(jsonObject.getJSONObject("data").getString("access_token"));
        expiresIn = jsonObject.getJSONObject("data").getLong("expires_in");
        expiresIn = expiresIn - System.currentTimeMillis()/1000;
        redisUtil.set(StrUtil.format(SmsConstants.SMS_USER_TOKEN_REDIS_PREFIX, getMd5Token()), accessToken, expiresIn, TimeUnit.SECONDS);
        return accessToken;
    }

    /**
     * 获取请求的headerMap
     * @param accessToken
     * @return
     */
    private HashMap<String, String> getCommonHeader(String accessToken) {
        HashMap<String, String> header = CollUtil.newHashMap();
        header.put("Authorization", accessToken);
        return header;
    }
}
