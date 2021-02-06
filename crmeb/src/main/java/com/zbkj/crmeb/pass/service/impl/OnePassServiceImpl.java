package com.zbkj.crmeb.pass.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.MyRecord;
import com.constants.OnePassConstants;
import com.exception.CrmebException;
import com.qiniu.util.Json;
import com.utils.DateUtil;
import com.utils.OnePassUtil;
import com.utils.RedisUtil;
import com.utils.ValidateFormUtil;
import com.zbkj.crmeb.express.model.Express;
import com.zbkj.crmeb.express.service.ExpressService;
import com.zbkj.crmeb.pass.request.*;
import com.zbkj.crmeb.pass.service.OnePassService;
import com.zbkj.crmeb.pass.vo.OnePassLoginVo;
import com.zbkj.crmeb.pass.vo.OnePassLogisticsQueryVo;
import com.zbkj.crmeb.system.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OnePassService 接口实现
 * 一号通
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class OnePassServiceImpl implements OnePassService {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OnePassUtil onePassUtil;

    @Autowired
    private ExpressService expressService;

    /**
     * 获取用户验证码
     *
     * @param phone 手机号
     */
    @Override
    public Object sendUserCode(String phone, Integer types) {
        ValidateFormUtil.isPhoneException(phone);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("phone", phone);
        if (ObjectUtil.isNotNull(types)) {
            map.add("types", types);
        }
        return onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.REGISTER_CAPTCHA_URI, map, null);
    }

    /**
     * 注册用户
     *
     * @param registerRequest 注册参数
     * @return String
     */
    @Override
    public String register(OnePassRegisterRequest registerRequest) {
        ValidateFormUtil.isPhoneException(registerRequest.getPhone());
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("account", registerRequest.getAccount());
        map.add("password", SecureUtil.md5(registerRequest.getPassword()));
        map.add("phone", registerRequest.getPhone());
        map.add("verify_code", registerRequest.getCode());
        map.add("url", registerRequest.getDomain());
        JSONObject post = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.USER_REGISTER_URI, map, null);

        // 获取token存入Redis
        String accessToken = OnePassConstants.ONE_PASS_USER_TOKEN_PREFIX.concat(post.getJSONObject("data").getString("access_token"));
        Long expiresIn = post.getJSONObject("data").getLong("expires_in");
        expiresIn = expiresIn - DateUtil.getTime();
        String secret = SecureUtil.md5(registerRequest.getPhone() + SecureUtil.md5(registerRequest.getPassword()));
        redisUtil.set(StrUtil.format(OnePassConstants.ONE_PASS_TOKEN_KEY_PREFIX, secret), accessToken, expiresIn, TimeUnit.SECONDS);

        //更新配置
        setConfigSmsInfo(registerRequest.getPhone(), registerRequest.getPassword());
        return post.getString("msg");
    }

    /**
     * 用户登录
     */
    @Override
    public MyRecord login(OnePassLoginRequest request) {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("account", request.getAccount());
        String secret = SecureUtil.md5(request.getAccount() + SecureUtil.md5(request.getPassword()));
        param.add("secret", secret);
        JSONObject jsonObject = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.USER_LOGIN_URI, param, null);
        setConfigSmsInfo(request.getAccount(), request.getPassword());

        String accessToken = OnePassConstants.ONE_PASS_USER_TOKEN_PREFIX.concat(jsonObject.getJSONObject("data").getString("access_token"));
        Long expiresIn = jsonObject.getJSONObject("data").getLong("expires_in");
        expiresIn = expiresIn - DateUtil.getTime();
        redisUtil.set(StrUtil.format(OnePassConstants.ONE_PASS_TOKEN_KEY_PREFIX, secret), accessToken, expiresIn, TimeUnit.SECONDS);
        MyRecord record = new MyRecord();
        record.set("account", request.getAccount());
        return record;
    }

    /**
     * 判断是否登录
     */
    @Override
    public MyRecord isLogin() {
        OnePassLoginVo loginVo;
        MyRecord record = new MyRecord();
        try {
            loginVo = onePassUtil.getLoginVo();
        } catch (Exception e) {
            return record.set("status", false);
        }
        onePassUtil.getToken(loginVo);
        record.set("status", true);
        record.set("info", loginVo.getAccount());
        return record;
    }

    /**
     * 一号通用户信息
     */
    @Override
    public JSONObject info() {
        JSONObject info = getInfo();
        // 判断是否开通电子面单
        JSONObject dump = info.getJSONObject("dump");
        Integer open = dump.getInteger("open");
        if (open.equals(1)) {
            String exportSiid = systemConfigService.getValueByKey("config_export_siid");// 打印机编号
            if (StrUtil.isBlank(exportSiid)) {
                dump.put("open", 0);
                info.put("dump", dump);
            }
        }
        return info;
    }

    private JSONObject getInfo() {
        OnePassLoginVo loginVo = onePassUtil.getLoginVo();
        String accessToken = onePassUtil.getToken(loginVo);
        HashMap<String, String> header = onePassUtil.getCommonHeader(accessToken);
        JSONObject jsonObject = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.USER_INFO_URI, null, header);
        JSONObject data = jsonObject.getJSONObject("data");
        data.put("account", loginVo.getAccount());
        return data;
    }

    /**
     * 用户注销
     */
    @Override
    public Boolean logOut() {
        OnePassLoginVo loginVo = onePassUtil.getLoginVo();
        boolean exists = redisUtil.exists(StrUtil.format(OnePassConstants.ONE_PASS_TOKEN_KEY_PREFIX, loginVo.getSecret()));
        if (exists) {
            redisUtil.remove(StrUtil.format(OnePassConstants.ONE_PASS_TOKEN_KEY_PREFIX, loginVo.getSecret()));
        }
        setConfigSmsInfo("", "");
        return true;
    }

    /**
     * 修改密码
     *
     * @param request 修改密码参数
     * @return Boolean
     */
    @Override
    public Boolean updatePassword(OnePassUpdateRequest request) {
        ValidateFormUtil.isPhoneException(request.getPhone());
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("account", request.getAccount());
        map.add("password", SecureUtil.md5(request.getPassword()));
        map.add("phone", request.getPhone());
        map.add("verify_code", request.getCode());
        onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.USER_UPDATE_PASSWORD_URI, map, null);

        setConfigSmsInfo("", "");
        return true;
    }

    /**
     * 修改用户手机
     *
     * @param request 修改手机参数
     * @return Boolean
     */
    @Override
    public Boolean updatePhone(OnePassUpdateRequest request) {
        ValidateFormUtil.isPhoneException(request.getPhone());
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("account", request.getAccount());
        map.add("phone", request.getPhone());
        map.add("verify_code", request.getCode());

        OnePassLoginVo loginVo = onePassUtil.getLoginVo();
        String token = onePassUtil.getToken(loginVo);
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.USER_UPDATE_PHONE_URI, map, header);
        onePassUtil.removeToken(loginVo);
        setConfigSmsInfo("", "");
        return true;
    }

    /**
     * 套餐列表
     *
     * @param type 套餐类型：sms,短信；expr_query,物流查询；expr_dump,电子面单；copy,产品复制
     * @return JSONObject
     */
    @Override
    public JSONObject mealList(String type) {
        if (!validateMealType(type)) throw new CrmebException("请选择正确的套餐类型");

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("type", type);
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);

        JSONObject jsonObject = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_MEAL_LIST_URI, params, header);
        return jsonObject.getJSONObject("data");
    }

    /**
     * 套餐购买
     *
     * @param request 购买参数
     */
    @Override
    public JSONObject mealCode(MealCodeRequest request) {
        if (!validateMealType(request.getType())) throw new CrmebException("请选择正确的套餐类型");
        // 获取用户详情
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        JSONObject jsonObject = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.USER_INFO_URI, null, header);
        JSONObject data = jsonObject.getJSONObject("data");

        boolean isGrant = false;
        switch (request.getType()) {
            case OnePassConstants.ONE_PASS_MEAL_TYPE_SMS:
                isGrant = data.getJSONObject("sms").getInteger("open").equals(1);
                break;
            case OnePassConstants.ONE_PASS_MEAL_TYPE_EXPR:
                isGrant = data.getJSONObject("query").getInteger("open").equals(1);
                break;
            case OnePassConstants.ONE_PASS_MEAL_TYPE_DUMP:
                isGrant = data.getJSONObject("dump").getInteger("open").equals(1);
                break;
            case OnePassConstants.ONE_PASS_MEAL_TYPE_COPY:
                isGrant = data.getJSONObject("copy").getInteger("open").equals(1);
                break;
        }
        if (!isGrant) throw new CrmebException("请先开通对应的服务");

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("meal_id", request.getMealId());
        params.add("price", request.getPrice());
        params.add("num", request.getNum());
        params.add("type", request.getType());
        params.add("pay_type", request.getPayType());

        JSONObject post = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_MEAL_CODE_URI, params, header);
        return post.getJSONObject("data");
    }

    /**
     * 服务开通
     *
     * @param request 服务开通参数
     */
    @Override
    public Boolean serviceOpen(ServiceOpenRequest request) {
        if (!validateMealType(request.getType())) throw new CrmebException("请选择正确的服务类型");
        Boolean open = false;
        switch (request.getType()) {
            case OnePassConstants.ONE_PASS_MEAL_TYPE_SMS:// 短信开通
                if (StrUtil.isBlank(request.getSign())) throw new CrmebException("签名不能为空");
                open = smsOpen(request);
                break;
            case OnePassConstants.ONE_PASS_MEAL_TYPE_EXPR:// 物流开通
                open = expressOpen(request);
                break;
            case OnePassConstants.ONE_PASS_MEAL_TYPE_DUMP:// 电子面单开通
                expressOpenValidate(request);
                open = expressOpen(request);
                break;
            case OnePassConstants.ONE_PASS_MEAL_TYPE_COPY:// 产品复制开通
                open = copyOpen();
                break;
        }
        return open;
    }

    /**
     * 用量记录
     * @param request 用量记录查询参数
     */
    @Override
    public JSONObject userRecord(OnePassUserRecordRequest request) {
        if (!validateMealType(request.getType())) throw new CrmebException("请选择正确的记录类型");
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("type", request.getType());
        params.add("page", request.getPage());
        params.add("limit", request.getLimit());
        if (OnePassConstants.ONE_PASS_MEAL_TYPE_SMS.equals(request.getType())) {
//            int status = 3;// 查询全部状态
            if (ObjectUtil.isNotNull(request.getStatus()) && request.getStatus() != 3) {
                params.add("status", request.getStatus());
            }
        }
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        JSONObject jsonObject = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_USER_RECORD_URI, params, header);
        return jsonObject.getJSONObject("data");
    }

    /**
     * 复制平台商品
     * @param url 商品链接
     * 平台复制商品示例
     * {"msg":"ok","data":{"image":"http://img.alicdn.com/imgextra/i2/2201504973406/O1CN01C7wCJe1b1zdtDz50j_!!2201504973406.jpg","store_info":"豹纹衣身,潮酷有型","give_integral":0,"cost":0,"temp_id":"","description_images":["http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01tGSA611b1zaDd3sOk_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01zrSHcP1b1zaPZB9IR_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i2/2201504973406/O1CN01X8KO5g1b1zaJcCOaN_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i2/2201504973406/O1CN01cS03ht1b1zaNO9kKJ_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01cCfVkZ1b1zaLXTr3v_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01zqsHfb1b1zaONUmg7_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01pD7WGr1b1zaLujJVA_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01hB0cth1b1zaONVO6K_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01aAWvO51b1zaLNWxzL_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01DweEqg1b1zaLXSBBL_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01bk0dqy1b1zaMlYKrE_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01PwOQL41b1zaJFfOVL_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01Npc2Nx1b1zaLXSmZ2_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01CYZLU51b1zaDd2fZG_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01tXnKqb1b1zaJcAqxs_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i3/2201504973406/O1CN0145DNqP1b1zaLuhZRf_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01QBjDyr1b1zaPZ9wTi_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01120POL1b1zaNOAHdT_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01C0YYiq1b1zaLNXdZZ_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01txpuLk1b1zaNO9LQZ_!!2201504973406.jpg"],"description":"<img src=\"http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01tGSA611b1zaDd3sOk_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01zrSHcP1b1zaPZB9IR_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i2/2201504973406/O1CN01X8KO5g1b1zaJcCOaN_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i2/2201504973406/O1CN01cS03ht1b1zaNO9kKJ_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01cCfVkZ1b1zaLXTr3v_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01zqsHfb1b1zaONUmg7_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01pD7WGr1b1zaLujJVA_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01hB0cth1b1zaONVO6K_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01aAWvO51b1zaLNWxzL_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01DweEqg1b1zaLXSBBL_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01bk0dqy1b1zaMlYKrE_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01PwOQL41b1zaJFfOVL_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01Npc2Nx1b1zaLXSmZ2_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01CYZLU51b1zaDd2fZG_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01tXnKqb1b1zaJcAqxs_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i3/2201504973406/O1CN0145DNqP1b1zaLuhZRf_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01QBjDyr1b1zaPZ9wTi_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01120POL1b1zaNOAHdT_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01C0YYiq1b1zaLNXdZZ_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01txpuLk1b1zaNO9LQZ_!!2201504973406.jpg\">","cate_id":"","slider_image":["http://img.alicdn.com/imgextra/i2/2201504973406/O1CN01C7wCJe1b1zdtDz50j_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01y07EFV1b1zaLru5es_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01dvxZXQ1b1zaJFgGWj_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i2/2201504973406/O1CN0178H0r51b1zaLrvZ56_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01Rw9wYQ1b1zaPznzFQ_!!2201504973406.jpg"],"soure_link":"","attrs":[],"unit_name":"件","postage":0,"video_link":"","price":0,"store_name":"CacheCache短袖t恤女装2019新款潮豹纹牛油果绿抹茶绿古着感少女","ficti":0,"keyword":"","stock":0,"add_time":0,"items":[{"detail":["160/84A/S","165/88A/M","170/92A/L","175/96A/XL"],"value":"尺码"},{"detail":["蜡黄色/757"],"value":"主要颜色"}],"ot_price":0,"info":{"header":[{"minWidth":120,"title":"尺码","align":"center","key":"value1"},{"minWidth":120,"title":"主要颜色","align":"center","key":"value2"},{"minWidth":80,"slot":"pic","title":"图片","align":"center"},{"minWidth":95,"slot":"price","title":"售价","align":"center"},{"minWidth":95,"slot":"cost","title":"成本价","align":"center"},{"minWidth":95,"slot":"ot_price","title":"原价","align":"center"},{"minWidth":95,"slot":"stock","title":"库存","align":"center"},{"minWidth":120,"slot":"bar_code","title":"商品编号","align":"center"},{"minWidth":95,"slot":"weight","title":"重量(KG)","align":"center"},{"minWidth":95,"slot":"volume","title":"体积(m³)","align":"center"},{"minWidth":70,"slot":"action","title":"操作","align":"center"}],"attr":[{"detail":["160/84A/S","165/88A/M","170/92A/L","175/96A/XL"],"value":"尺码"},{"detail":["蜡黄色/757"],"value":"主要颜色"}],"value":[{"brokerage":0,"cost":0,"value2":"蜡黄色/757","value1":"160/84A/S","weight":0,"pic":"","volume":0,"brokerage_two":0,"price":0,"bar_code":"","detail":{"主要颜色":"蜡黄色/757","尺码":"160/84A/S"},"stock":0,"ot_price":0},{"brokerage":0,"cost":0,"value2":"蜡黄色/757","value1":"165/88A/M","weight":0,"pic":"","volume":0,"brokerage_two":0,"price":0,"bar_code":"","detail":{"主要颜色":"蜡黄色/757","尺码":"165/88A/M"},"stock":0,"ot_price":0},{"brokerage":0,"cost":0,"value2":"蜡黄色/757","value1":"170/92A/L","weight":0,"pic":"","volume":0,"brokerage_two":0,"price":0,"bar_code":"","detail":{"主要颜色":"蜡黄色/757","尺码":"170/92A/L"},"stock":0,"ot_price":0},{"brokerage":0,"cost":0,"value2":"蜡黄色/757","value1":"175/96A/XL","weight":0,"pic":"","volume":0,"brokerage_two":0,"price":0,"bar_code":"","detail":{"主要颜色":"蜡黄色/757","尺码":"175/96A/XL"},"stock":0,"ot_price":0}]}},"status":200}
     */
    @Override
    public JSONObject copyGoods(String url) {
        HashMap<String, String> header = onePassUtil.getCommonHeader(onePassUtil.getToken());
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("url", url);
        JSONObject jsonObject = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_COPY_GOODS_URI, params, header);
        System.out.println("one pass copy product = " + jsonObject);
        return jsonObject.getJSONObject("data");
    }

    /**
     * 电子面单
     * @param record 电子面单参数
     * @return
     */
    @Override
    public MyRecord expressDump(MyRecord record) {
        HashMap<String, String> header = onePassUtil.getCommonHeader(onePassUtil.getToken());
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        Map<String, Object> columns = record.getColumns();
        columns.forEach(params::add);
        JSONObject post = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_EXPRESS_DUMP_URI, params, header);
        MyRecord myRecord = new MyRecord();
        JSONObject jsonObject = post.getJSONObject("data");
        return myRecord.setColums(jsonObject);
    }

    /**
     * 物流追踪
     * @param expressNo 快递单号
     * @param com   快递公司简写
     * @return OnePassLogisticsQueryVo
     */
    @Override
    public OnePassLogisticsQueryVo exprQuery(String expressNo, String com) {
        HashMap<String, String> header = onePassUtil.getCommonHeader(onePassUtil.getToken());
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("com", com);
        params.add("num", expressNo);
        JSONObject post = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_EXPRESS_QUEARY_URI, params, header);
        String dataStr = post.getString("data");
        if (StrUtil.isBlank(dataStr) || dataStr.equals("[]")) {
            return null;
        }
        JSONObject jsonObject = post.getJSONObject("data");
        return JSONObject.toJavaObject(jsonObject, OnePassLogisticsQueryVo.class);
    }

    /**
     * 修改手机号——验证账号密码
     * @return Boolean
     */
    @Override
    public Boolean beforeUpdatePhoneValidator(OnePassLoginRequest request) {
        OnePassLoginVo loginVo = onePassUtil.getLoginVo();
        if (!loginVo.getAccount().equals(request.getAccount())) {
            throw new CrmebException("账号不匹配");
        }
        String secret = SecureUtil.md5(request.getAccount() + SecureUtil.md5(request.getPassword()));
        if (!loginVo.getSecret().equals(secret)) {
            throw new CrmebException("密码不匹配");
        }
        return Boolean.TRUE;
    }

    /**
     * 物流、电子面单开通参数校验
     */
    private void expressOpenValidate(ServiceOpenRequest request) {
        if (StrUtil.isBlank(request.getCom())) {
            throw new CrmebException("请选择快递公司");
        }
        if (StrUtil.isBlank(request.getTempId())) {
            throw new CrmebException("请选择快递模板");
        }
        if (StrUtil.isBlank(request.getToName())) {
            throw new CrmebException("请填写寄件人姓名");
        }
        if (StrUtil.isBlank(request.getToTel())) {
            throw new CrmebException("请输入寄件人手机号码");
        }
        if (StrUtil.isBlank(request.getToAddress())) {
            throw new CrmebException("请填写寄件人详细地址");
        }
        if (StrUtil.isBlank(request.getSiid())) {
            throw new CrmebException("请填写云打印机编号");
        }
        ValidateFormUtil.isPhoneException(request.getToTel());
    }

    /**
     * 商品复制开通
     */
    private Boolean copyOpen() {
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_COPY_OPEN_URI, null, header);
        return Boolean.TRUE;
    }

    /**
     * 物流开通
     * 保存平台电子面单打印信息
     */
    private Boolean expressOpen(ServiceOpenRequest request) {
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        if (request.getType().equals(OnePassConstants.ONE_PASS_MEAL_TYPE_EXPR)) {// 物流
            onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_EXPRESS_OPEN_URI, null, header);
            return Boolean.TRUE;
        }

        // 电子面单
        Express express = expressService.getByCode(request.getCom());
        if (ObjectUtil.isNull(express)) throw new CrmebException("没有找到对应的快递公司");

        // 保存平台电子面单打印信息
        Boolean execute = transactionTemplate.execute(e -> {
            // 快递公司id
            systemConfigService.updateOrSaveValueByName("config_export_id", express.getId().toString());
            // 快递公司模板id
            systemConfigService.updateOrSaveValueByName("config_export_temp_id", request.getTempId());
            // 快递公司编码
            systemConfigService.updateOrSaveValueByName("config_export_com", request.getCom());
            // 发货人姓名
            systemConfigService.updateOrSaveValueByName("config_export_to_name", request.getToName());
            // 发货人电话
            systemConfigService.updateOrSaveValueByName("config_export_to_tel", request.getToTel());
            // 发货人详细地址
            systemConfigService.updateOrSaveValueByName("config_export_to_address", request.getToAddress());
            // 电子面单打印机编号
            systemConfigService.updateOrSaveValueByName("config_export_siid", request.getSiid());
            return Boolean.TRUE;
        });
        if (Boolean.FALSE.equals(execute)) throw new CrmebException("保存平台电子面单打印信息失败");

        // 查询是否已经开通
        JSONObject info = info();
        JSONObject dump = info.getJSONObject("dump");
        if (dump.getInteger("open").equals(1)) {
            return Boolean.TRUE;
        }
        onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_EXPRESS_OPEN_URI, null, header);
        return Boolean.TRUE;
    }

    /**
     * 短信开通
     */
    private Boolean smsOpen(ServiceOpenRequest request) {
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("sign", request.getSign());
        onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_SMS_OPEN_URI, params, header);
        return Boolean.TRUE;
    }

    /**
     * 校验套餐类型
     */
    private Boolean validateMealType(String type) {
        List<String> list = CollUtil.newArrayList();
        list.add(OnePassConstants.ONE_PASS_MEAL_TYPE_SMS);
        list.add(OnePassConstants.ONE_PASS_MEAL_TYPE_EXPR);
        list.add(OnePassConstants.ONE_PASS_MEAL_TYPE_DUMP);
        list.add(OnePassConstants.ONE_PASS_MEAL_TYPE_COPY);
        return list.contains(type);
    }

    /**
     * 更新sms配置信息
     *
     * @param account  账号
     * @param password 密码
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    private void setConfigSmsInfo(String account, String password) {
        boolean accountResult = systemConfigService.updateOrSaveValueByName("sms_account", account);
        boolean tokenResult = systemConfigService.updateOrSaveValueByName("sms_token", password);

        if (!accountResult || !tokenResult) {
            throw new CrmebException("数据更新失败！");
        }
    }
}
