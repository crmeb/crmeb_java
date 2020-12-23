package com.zbkj.crmeb.sms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.OnePassConstants;
import com.constants.SmsConstants;
import com.exception.CrmebException;
import com.utils.*;
import com.zbkj.crmeb.pass.vo.OnePassLoginVo;
import com.zbkj.crmeb.sms.model.SmsRecord;
import com.zbkj.crmeb.sms.request.SendSmsVo;
import com.zbkj.crmeb.sms.request.SmsApplyTempRequest;
import com.zbkj.crmeb.sms.request.SmsModifySignRequest;
import com.zbkj.crmeb.sms.service.SmsRecordService;
import com.zbkj.crmeb.sms.service.SmsService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.service.UserService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
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

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    /**
     * 添加到短信队列
     * 验证码特殊处理其他的参数自行根据要求处理
     * 参数处理逻辑 {code:value,code1:value1}
     * @param phone String 手机号码
     * @return boolean
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    @Override
    public Boolean pushCodeToList(String phone, Integer tag, HashMap<String, Object> pram) {
        //发送手机验证码， 记录到redis  sms_validate_code_手机号
        switch (tag) {
            case SmsConstants.SMS_CONFIG_TYPE_VERIFICATION_CODE: // 验证码 特殊处理 code
                //获取短信验证码过期时间
                String codeExpireStr = systemConfigService.getValueByKey(Constants.CONFIG_KEY_SMS_CODE_EXPIRE);
                if (StringUtils.isBlank(codeExpireStr) || Integer.parseInt(codeExpireStr) == 0) {
                    codeExpireStr = Constants.NUM_FIVE + "";
                }
                Integer code = CrmebUtil.randomCount(111111, 999999);
                HashMap<String, Object> justPram = new HashMap<>();
                justPram.put("code", code);
                push(phone, SmsConstants.SMS_CONFIG_VERIFICATION_CODE,
                        SmsConstants.SMS_CONFIG_VERIFICATION_CODE_TEMP_ID, false, justPram);

                redisUtil.set(userService.getValidateCodeRedisKey(phone), code, Long.valueOf(codeExpireStr), TimeUnit.MINUTES);//5分钟过期

                break;
            case SmsConstants.SMS_CONFIG_TYPE_LOWER_ORDER_SWITCH: // 支付成功短信提醒 pay_price order_id
                push(phone, SmsConstants.SMS_CONFIG_LOWER_ORDER_SWITCH,
                        SmsConstants.SMS_CONFIG_LOWER_ORDER_SWITCH_TEMP_ID, true, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_DELIVER_GOODS_SWITCH: // 发货短信提醒 nickname store_name
                push(phone, SmsConstants.SMS_CONFIG_DELIVER_GOODS_SWITCH,
                        SmsConstants.SMS_CONFIG_DELIVER_GOODS_SWITCH_TEMP_ID, true, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_CONFIRM_TAKE_OVER_SWITCH: // 确认收货短信提醒 order_id store_name
                push(phone, SmsConstants.SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH,
                        SmsConstants.SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID, true, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_LOWER_ORDER_SWITCH: // 用户下单管理员短信提醒 admin_name order_id
                push(phone, SmsConstants.SMS_CONFIG_ADMIN_LOWER_ORDER_SWITCH,
                        SmsConstants.SMS_CONFIG_ADMIN_LOWER_ORDER_SWITCH_TEMP_ID, true, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_PAY_SUCCESS_SWITCH: // 支付成功管理员短信提醒 admin_name order_id
                push(phone, SmsConstants.SMS_CONFIG_ADMIN_PAY_SUCCESS_SWITCH,
                        SmsConstants.SMS_CONFIG_ADMIN_PAY_SUCCESS_SWITCH_TEMP_ID, true, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_REFUND_SWITCH: // 用户确认收货管理员短信提醒 admin_name order_id
                push(phone, SmsConstants.SMS_CONFIG_ADMIN_REFUND_SWITCH,
                        SmsConstants.SMS_CONFIG_ADMIN_REFUND_SWITCH_TEMP_ID, true, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_CONFIRM_TAKE_OVER_SWITCH: // 用户发起退款管理员短信提醒 admin_name order_id
                push(phone, SmsConstants.SMS_CONFIG_ADMIN_CONFIRM_TAKE_OVER_SWITCH,
                        SmsConstants.SMS_CONFIG_ADMIN_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID, true, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_PRICE_REVISION_SWITCH: // 改价短信提醒 order_id pay_price
                push(phone, SmsConstants.SMS_CONFIG_PRICE_REVISION_SWITCH,
                        SmsConstants.SMS_CONFIG_PRICE_REVISION_SWITCH_TEMP_ID, true, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ORDER_PAY_FALSE: // 订单未支付 order_id
                push(phone, SmsConstants.SMS_CONFIG_ORDER_PAY_FALSE,
                        SmsConstants.SMS_CONFIG_ORDER_PAY_FALSE_TEMP_ID, false, pram);
                break;
        }
        return true;
    }

    @Override
    public boolean sendCode(SendSmsVo sendSmsVo) {
        String result;
        try {
            String token = onePassUtil.getToken();
            HashMap<String, String> header = onePassUtil.getCommonHeader(token);

            Map<String, Object> map = (Map<String, Object>) JSONObject.parseObject(sendSmsVo.getParam());
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            param.add("phone", sendSmsVo.getMobile());
            param.add("temp_id", sendSmsVo.getTemplate());
            map.entrySet().stream().forEach(entry -> param.add(StrUtil.format(SmsConstants.SMS_COMMON_PARAM_FORMAT, entry.getKey()), entry.getValue()));

            result = restTemplateUtil.postFromUrlencoded(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_SEND_URI, param, header);
        } catch (Exception e) {
            //接口请求异常，需要重新发送
            e.printStackTrace();
            return false;
        }

        if (StringUtils.isBlank(result)) {
            //没有拿到数据 需要重新发送
            return false;
        }

        JSONObject joResult;
        try {
            joResult = checkResult(result);
        } catch (Exception e1) {
            joResult = JSONObject.parseObject(result);
        }

        int resultCode = joResult.getInteger("status");
        String message = joResult.getString("msg");
        JSONObject data = joResult.getJSONObject("data");
        String smsRecodeId = (data.containsKey("id") ? data.getString("id") : "0");
        sendSmsVo.setContent(data.getString("content"));

        if (resultCode == Constants.HTTPSTATUS_CODE_SUCCESS) {
            try {
                // 这里只保存本地记录，之后的短信列表从平台api处查询
                SmsRecord smsRecord = new SmsRecord(sendSmsVo.getUid(), sendSmsVo.getMobile(), sendSmsVo.getContent(),
                        "", sendSmsVo.getTemplate().toString(),
                        resultCode, Integer.parseInt(smsRecodeId), message);
                smsRecordService.save(smsRecord);
            } catch (Exception e) {
                return true;
            }
        }
        return true;
    }

    /**
     * 短信队列消费者
     */
    @Async
    @Transactional
    public void consume() {
        Long size = redisUtil.getListSize(SmsConstants.SMS_SEND_KEY);
        logger.info("SmsServiceImpl.consume | size:" + size);
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                //如果10秒钟拿不到一个数据，那么退出循环
                Object data = redisUtil.getRightPop(SmsConstants.SMS_SEND_KEY, 10L);
                if (null == data) {
                    continue;
                }
                try {
                    JSONObject jsonObject = JSONObject.parseObject(data.toString());
                    SendSmsVo smsVo = JSONObject.toJavaObject(jsonObject, SendSmsVo.class);
//                    SendSmsVo smsVo = JSONObject.toJavaObject(JSONObject.parseObject(data.toString()), SendSmsVo.class);
                    boolean result = sendCode(smsVo);
                    // 捕捉异常或者发送失败存起来下次继续
                    if (!result) {
                        redisUtil.lPush(SmsConstants.SMS_SEND_KEY, data);
                    }
                } catch (Exception e) {
                    redisUtil.lPush(SmsConstants.SMS_SEND_KEY, data);
                }
            }
        }
    }

    /**
     * 添加待发送消息到redis队列
     */
    @Override
    public void push(String phone, String tempKey, Integer msgTempId, boolean valid, HashMap<String, Object> mapPram) {
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(tempKey) || msgTempId <= 0) {
            return;
        }
        OnePassLoginVo loginVo = onePassUtil.getLoginVo();

        HashMap<String, Object> mParam = new HashMap<>();
        mParam.put("uid", loginVo.getAccount());
        mParam.put("token", loginVo.getSecret());
        mParam.put("mobile", phone);
        mParam.put("template", msgTempId);
        mParam.put("param", JSONObject.toJSONString(mapPram));

        if (!valid) {
            redisUtil.lPush(SmsConstants.SMS_SEND_KEY, JSONObject.toJSONString(mParam));
            return;
        }
        String value = systemConfigService.getValueByKey(tempKey);
        if (value.equals("1")) {
            redisUtil.lPush(SmsConstants.SMS_SEND_KEY, JSONObject.toJSONString(mParam));
        }
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
     * @param type  (1=验证码 2=通知 3=推广)
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
        if (StringUtils.isBlank(result)) {
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
}
