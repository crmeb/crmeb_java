package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.request.SmsApplyTempRequest;
import com.zbkj.common.request.SmsModifySignRequest;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.RestTemplateUtil;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.OnePassConstants;
import com.zbkj.common.constants.SmsConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.vo.OnePassLoginVo;
import com.zbkj.common.vo.SendSmsVo;
import com.zbkj.service.service.*;
import com.zbkj.service.util.OnePassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * SmsServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class SmsServiceImpl implements SmsService {

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

    @Autowired
    private OnePassUtil onePassUtil;

    @Autowired
    private OnePassService onePassService;

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    /**
     * 发送短信
     * @param phone 手机号
     * @param tag 短信标识
     * @param msgTempId 短信模板id
     * @param pram 参数
     * @return Boolean
     */
    private Boolean sendMessages(String phone, Integer tag, Integer msgTempId, HashMap<String, Object> pram) {
        //发送手机验证码， 记录到redis  sms_validate_code_手机号
        switch (tag) {
            case SmsConstants.SMS_CONFIG_TYPE_VERIFICATION_CODE: // 验证码 特殊处理 code
                //获取短信验证码过期时间
                String codeExpireStr = systemConfigService.getValueByKey(Constants.CONFIG_KEY_SMS_CODE_EXPIRE);
                if (StrUtil.isBlank(codeExpireStr) || Integer.parseInt(codeExpireStr) == 0) {
                    codeExpireStr = Constants.NUM_FIVE + "";// 默认5分钟过期
                }
                Integer code = CrmebUtil.randomCount(111111, 999999);
                HashMap<String, Object> justPram = new HashMap<>();
                justPram.put("code", code);
                justPram.put("time", codeExpireStr);
                push(phone, SmsConstants.SMS_CONFIG_VERIFICATION_CODE,
                        SmsConstants.SMS_CONFIG_VERIFICATION_CODE_TEMP_ID, justPram);

                // 将验证码存入redis
                redisUtil.set(userService.getValidateCodeRedisKey(phone), code, Long.valueOf(codeExpireStr), TimeUnit.MINUTES);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_LOWER_ORDER_SWITCH: // 支付成功短信提醒 pay_price order_id
                push(phone, msgTempId, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_DELIVER_GOODS_SWITCH: // 发货短信提醒 nickname store_name
                push(phone, msgTempId, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_CONFIRM_TAKE_OVER_SWITCH: // 确认收货短信提醒 order_id store_name
                push(phone, SmsConstants.SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH,
                        SmsConstants.SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_LOWER_ORDER_SWITCH: // 用户下单管理员短信提醒 admin_name order_id
                push(phone, msgTempId, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_PAY_SUCCESS_SWITCH: // 支付成功管理员短信提醒 admin_name order_id
                push(phone, msgTempId, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_REFUND_SWITCH: // 用户确认收货管理员短信提醒 admin_name order_id
                push(phone, msgTempId, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_CONFIRM_TAKE_OVER_SWITCH: // 用户发起退款管理员短信提醒 admin_name order_id
                push(phone, msgTempId, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_PRICE_REVISION_SWITCH: // 改价短信提醒 order_id pay_price
                push(phone, SmsConstants.SMS_CONFIG_PRICE_REVISION_SWITCH,
                        SmsConstants.SMS_CONFIG_PRICE_REVISION_SWITCH_TEMP_ID, pram);
                break;
        }
        return true;
    }

    /**
     * 发送短信
     * @param sendSmsVo 短信参数
     */
    private Boolean sendCode(SendSmsVo sendSmsVo) {
        String result;
        try {
            String token = onePassUtil.getToken();
            HashMap<String, String> header = onePassUtil.getCommonHeader(token);

            Map<String, Object> map = (Map<String, Object>) JSONObject.parseObject(sendSmsVo.getParam());
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            param.add("phone", sendSmsVo.getMobile());
            param.add("temp_id", sendSmsVo.getTemplate());
            map.entrySet().stream().forEach(entry -> param.add(StrUtil.format(SmsConstants.SMS_COMMON_PARAM_FORMAT, entry.getKey()), entry.getValue()));
            System.out.println("============发送短信=========header = " + header);
            result = restTemplateUtil.postFromUrlencoded(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_SEND_URI, param, header);
            checkResult(result);
        } catch (Exception e) {
            //接口请求异常，需要重新发送
            e.printStackTrace();
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 组装发送对象
     *
     * @param phone     手机号
     * @param msgTempId 模板id
     * @param mapPram   参数map
     */
    private Boolean push(String phone, Integer msgTempId, HashMap<String, Object> mapPram) {
        if (StrUtil.isBlank(phone) || msgTempId <= 0) {
            return false;
        }
        OnePassLoginVo loginVo = onePassUtil.getLoginVo();
        SendSmsVo smsVo = new SendSmsVo();
        smsVo.setUid(loginVo.getAccount());
        smsVo.setToken(loginVo.getSecret());
        smsVo.setMobile(phone);
        smsVo.setTemplate(msgTempId);
        smsVo.setParam(JSONObject.toJSONString(mapPram));
        return sendCode(smsVo);
    }

    /**
     * 添加待发送消息到redis队列
     *
     * @param phone     手机号
     * @param tempKey   模板key
     * @param msgTempId 模板id
     * @param mapPram   参数map
     */
    private Boolean push(String phone, String tempKey, Integer msgTempId, HashMap<String, Object> mapPram) {
        if (StrUtil.isBlank(phone) || StrUtil.isBlank(tempKey) || msgTempId <= 0) {
            return false;
        }
        OnePassLoginVo loginVo = onePassUtil.getLoginVo();
        SendSmsVo smsVo = new SendSmsVo();
        smsVo.setUid(loginVo.getAccount());
        smsVo.setToken(loginVo.getSecret());
        smsVo.setMobile(phone);
        smsVo.setTemplate(msgTempId);
        smsVo.setParam(JSONObject.toJSONString(mapPram));
        return sendCode(smsVo);
    }

    /**
     * 修改签名
     */
    @Override
    public Boolean modifySign(SmsModifySignRequest request) {
        ValidateFormUtil.isPhoneException(request.getPhone());
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("sign", request.getSign());
        map.add("phone", request.getPhone());
        map.add("verify_code", request.getCode());

        onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_SMS_MODIFY_URI, map, header);
        return Boolean.TRUE;
    }

    /**
     * 短信模板
     */
    @Override
    public MyRecord temps(PageParamRequest pageParamRequest) {
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("page", pageParamRequest.getPage());
        param.add("limit", pageParamRequest.getLimit());
        param.add("temp_type", 0);// 查询所有类型模板
        JSONObject post = postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_TEMP_LIST_URI, param, header);
        JSONObject jsonObject = post.getJSONObject("data");
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        MyRecord myRecord = new MyRecord();
        if (CollUtil.isEmpty(jsonArray)) {
            return myRecord.set("count", 0);
        }
        List<MyRecord> recordList = jsonArray.stream().map(i -> {
            MyRecord record = new MyRecord();
            record.setColums((JSONObject) i);
            switch (record.getInt("temp_type")) {
                case 1:
                    record.set("type", "验证码");
                    break;
                case 2:
                    record.set("type", "通知");
                    break;
                case 3:
                    record.set("type", "营销短信");
                    break;
            }
            return record;
        }).collect(Collectors.toList());

        myRecord.set("count", recordList.size());
        myRecord.set("data", recordList);
        return myRecord;
    }

    /**
     * 申请模板消息
     */
    @Override
    public Boolean applyTempMessage(SmsApplyTempRequest request) {
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("title", request.getTitle());
        param.add("content", request.getContent());
        param.add("type", request.getType());

        onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_TEMP_APPLY_URI, param, header);
        return Boolean.TRUE;
    }

    /**
     * 模板申请记录
     *
     * @param type (1=验证码 2=通知 3=推广)
     */
    @Override
    public MyRecord applys(Integer type, PageParamRequest pageParamRequest) {
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("page", pageParamRequest.getPage());
        param.add("limit", pageParamRequest.getLimit());
        if (ObjectUtil.isNotNull(type)) {
            param.add("temp_type", type);
        }

        JSONObject post = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_APPLYS_LIST_URI, param, header);
        JSONObject jsonObject = post.getJSONObject("data");
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        MyRecord myRecord = new MyRecord();
        if (CollUtil.isEmpty(jsonArray)) {
            return myRecord.set("count", 0);
        }
        List<MyRecord> recordList = jsonArray.stream().map(i -> {
            MyRecord record = new MyRecord();
            record.setColums((JSONObject) i);
            switch (record.getInt("temp_type")) {
                case 1:
                    record.set("type", "验证码");
                    break;
                case 2:
                    record.set("type", "通知");
                    break;
                case 3:
                    record.set("type", "营销短信");
                    break;
            }
            return record;
        }).collect(Collectors.toList());

        myRecord.set("count", recordList.size());
        myRecord.set("data", recordList);
        return myRecord;
    }

    /**
     * 发送公共验证码
     *
     * @param phone 手机号
     * @return Boolean
     * 1.校验后台是否配置一号通
     * 2.一号通是否剩余短信条数
     * 3.发送短信
     */
    @Override
    public Boolean sendCommonCode(String phone) {
        ValidateFormUtil.isPhone(phone,"手机号码错误");
        Boolean checkAccount = onePassService.checkAccount();
        if (!checkAccount) {
            throw new CrmebException("发送短信请先登录一号通账号");
        }
        JSONObject info = onePassService.info();
        JSONObject smsObject = info.getJSONObject("sms");
        Integer open = smsObject.getInteger("open");
        if (!open.equals(1)) {
            throw new CrmebException("发送短信请先开通一号通账号服务");
        }
        if (smsObject.getInteger("num") <= 0) {
            throw new CrmebException("一号通账号服务余量不足");
        }

        return sendSms(phone, SmsConstants.SMS_CONFIG_TYPE_VERIFICATION_CODE, null);
    }

    /**
     * 发送支付成功短信
     *
     * @param phone    手机号
     * @param orderNo  订单编号
     * @param payPrice 支付金额
     * @param msgTempId 短信模板id
     * @return Boolean
     */
    @Override
    public Boolean sendPaySuccess(String phone, String orderNo, BigDecimal payPrice, Integer msgTempId) {
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("pay_price", payPrice);
        map.put("order_id", orderNo);
        return sendMessages(phone, SmsConstants.SMS_CONFIG_TYPE_LOWER_ORDER_SWITCH, msgTempId, map);
    }

    /**
     * 发送管理员下单短信提醒
     *
     * @param phone    手机号
     * @param orderNo  订单编号
     * @param realName 管理员名称
     * @param msgTempId 短信模板id
     * @return Boolean
     */
    @Override
    public Boolean sendCreateOrderNotice(String phone, String orderNo, String realName, Integer msgTempId) {
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("admin_name", realName);
        map.put("order_id", orderNo);
        return sendMessages(phone, SmsConstants.SMS_CONFIG_TYPE_ADMIN_LOWER_ORDER_SWITCH, msgTempId, map);
    }

    /**
     * 发送订单支付成功管理员提醒短信
     *
     * @param phone    手机号
     * @param orderNo  订单编号
     * @param realName 管理员名称
     * @param msgTempId 短信模板id
     * @return Boolean
     */
    @Override
    public Boolean sendOrderPaySuccessNotice(String phone, String orderNo, String realName, Integer msgTempId) {
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("admin_name", realName);
        map.put("order_id", orderNo);
        return sendMessages(phone, SmsConstants.SMS_CONFIG_TYPE_ADMIN_PAY_SUCCESS_SWITCH, msgTempId, map);
    }

    /**
     * 发送用户退款管理员提醒短信
     *
     * @param phone    手机号
     * @param orderNo  订单编号
     * @param realName 管理员名称
     * @param msgTempId 短信模板id
     * @return Boolean
     */
    @Override
    public Boolean sendOrderRefundApplyNotice(String phone, String orderNo, String realName, Integer msgTempId) {
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("admin_name", realName);
        map.put("order_id", orderNo);
        return sendMessages(phone, SmsConstants.SMS_CONFIG_TYPE_ADMIN_CONFIRM_TAKE_OVER_SWITCH, msgTempId, map);
    }

    /**
     * 发送用户确认收货管理员提醒短信
     * @param phone 手机号
     * @param orderNo 订单编号
     * @param realName 管理员名称
     * @param msgTempId 短信模板id
     */
    @Override
    public Boolean sendOrderReceiptNotice(String phone, String orderNo, String realName, Integer msgTempId) {
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("admin_name", realName);
        map.put("order_id", orderNo);
        return sendMessages(phone, SmsConstants.SMS_CONFIG_TYPE_ADMIN_REFUND_SWITCH, msgTempId, map);
    }

    /**
     * 发送订单改价提醒短信
     *
     * @param phone   手机号
     * @param orderNo 订单编号
     * @param price   修改后的支付金额
     * @param msgTempId 短信模板id
     * @return Boolean
     */
    @Override
    public Boolean sendOrderEditPriceNotice(String phone, String orderNo, BigDecimal price, Integer msgTempId) {
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("order_id", orderNo);
        map.put("pay_price", price);
        return sendMessages(phone, SmsConstants.SMS_CONFIG_TYPE_PRICE_REVISION_SWITCH, msgTempId, map);
    }

    /**
     * 发送订单发货提醒短信
     *
     * @param phone     手机号
     * @param nickName  用户昵称
     * @param storeName 商品名称
     * @param orderNo   订单编号
     * @param msgTempId 短信模板id
     */
    @Override
    public Boolean sendOrderDeliverNotice(String phone, String nickName, String storeName, String orderNo, Integer msgTempId) {
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("nickname", nickName);
        map.put("store_name", storeName);
        map.put("order_id", orderNo);
        return sendMessages(phone, SmsConstants.SMS_CONFIG_TYPE_DELIVER_GOODS_SWITCH, msgTempId, map);
    }

    /**
     * post请求from表单模式提交
     */
    private JSONObject postFrom(String url, MultiValueMap<String, Object> param, Map<String, String> header) {
        String result = restTemplateUtil.postFromUrlencoded(url, param, header);
        return checkResult(result);
    }

    /**
     * 检测结构请求返回的数据
     *
     * @param result 接口返回的结果
     * @return JSONObject
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    private JSONObject checkResult(String result) {
        if (StrUtil.isBlank(result)) {
            throw new CrmebException("短信平台接口异常，没任何数据返回！");
        }

        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.parseObject(result);
        } catch (Exception e) {
            throw new CrmebException("短信平台接口异常！");
        }
        if (SmsConstants.SMS_ERROR_CODE.equals(jsonObject.getInteger("status"))) {
            throw new CrmebException("短信平台接口" + jsonObject.getString("msg"));
        }
        return jsonObject;
    }

    /**
     * 发送短信
     * 验证码特殊处理其他的参数自行根据要求处理
     * 参数处理逻辑 {code:value,code1:value1}
     *
     * @param phone String 手机号码
     * @return boolean
     */
    private Boolean sendSms(String phone, Integer tag, HashMap<String, Object> pram) {
        SendSmsVo sendSmsVo = new SendSmsVo();
        sendSmsVo.setMobile(phone);
        if (tag.equals(SmsConstants.SMS_CONFIG_TYPE_VERIFICATION_CODE)) {// 验证码 特殊处理 code
            //获取短信验证码过期时间
            String codeExpireStr = systemConfigService.getValueByKey(Constants.CONFIG_KEY_SMS_CODE_EXPIRE);
            if (StrUtil.isBlank(codeExpireStr) || Integer.parseInt(codeExpireStr) == 0) {
                codeExpireStr = Constants.NUM_FIVE + "";// 默认5分钟过期
            }
            Integer code = CrmebUtil.randomCount(111111, 999999);
            HashMap<String, Object> justPram = new HashMap<>();
            justPram.put("code", code);
            justPram.put("time", codeExpireStr);

            sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_VERIFICATION_CODE_TEMP_ID);
            sendSmsVo.setContent(JSONObject.toJSONString(justPram));
            Boolean aBoolean = commonSendSms(sendSmsVo);
            if (!aBoolean) {
                throw new CrmebException("发送短信失败，请联系后台管理员");
            }
            // 将验证码存入redis
            redisUtil.set(userService.getValidateCodeRedisKey(phone), code, Long.valueOf(codeExpireStr), TimeUnit.MINUTES);
            return aBoolean;
        }
        // 以下部分实时性不高暂时还是使用队列发送
        sendSmsVo.setContent(JSONObject.toJSONString(pram));
        switch (tag) {
            case SmsConstants.SMS_CONFIG_TYPE_LOWER_ORDER_SWITCH: // 支付成功短信提醒 pay_price order_id
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_LOWER_ORDER_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_DELIVER_GOODS_SWITCH: // 发货短信提醒 nickname store_name
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_DELIVER_GOODS_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_CONFIRM_TAKE_OVER_SWITCH: // 确认收货短信提醒 order_id store_name
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_LOWER_ORDER_SWITCH: // 用户下单管理员短信提醒 admin_name order_id
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_ADMIN_LOWER_ORDER_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_PAY_SUCCESS_SWITCH: // 支付成功管理员短信提醒 admin_name order_id
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_ADMIN_PAY_SUCCESS_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_REFUND_SWITCH: // 用户确认收货管理员短信提醒 admin_name order_id
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_ADMIN_REFUND_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_CONFIRM_TAKE_OVER_SWITCH: // 用户发起退款管理员短信提醒 admin_name order_id
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_ADMIN_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_PRICE_REVISION_SWITCH: // 改价短信提醒 order_id pay_price
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_PRICE_REVISION_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ORDER_PAY_FALSE: // 订单未支付 order_id
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_ORDER_PAY_FALSE_TEMP_ID);
                break;
        }
        return commonSendSms(sendSmsVo);
    }

    /**
     * 公共发送短信
     *
     * @param sendSmsVo 发送短信对象
     * @return 是否发送成功
     */
    private Boolean commonSendSms(SendSmsVo sendSmsVo) {
        try {
            String result;
            String token = onePassUtil.getToken();
            HashMap<String, String> header = onePassUtil.getCommonHeader(token);

            Map<String, Object> map = (Map<String, Object>) JSONObject.parseObject(sendSmsVo.getContent());
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            param.add("phone", sendSmsVo.getMobile());
            param.add("temp_id", sendSmsVo.getTemplate());

            map.forEach((key, value) -> param.add(StrUtil.format(SmsConstants.SMS_COMMON_PARAM_FORMAT, key), value));
            logger.info("============发送短信=========header = " + header);
            result = restTemplateUtil.postFromUrlencoded(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_SEND_URI, param, header);
            checkResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发送短信失败：" + e.getMessage());
            return false;
        }
        return true;
    }
}
