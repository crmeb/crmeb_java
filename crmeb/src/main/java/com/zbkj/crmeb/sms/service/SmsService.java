package com.zbkj.crmeb.sms.service;

import com.alibaba.fastjson.JSONObject;
import com.common.PageParamRequest;
import com.zbkj.crmeb.sms.request.RegisterRequest;
import com.zbkj.crmeb.sms.request.SendSmsVo;
import com.zbkj.crmeb.sms.request.SmsLoginRequest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * @author Mr.Zhang
 * @Description SmsService 接口
 * @since 2020-04-16
 */
public interface SmsService{

    JSONObject register(RegisterRequest registerRequest);

    JSONObject getPublicTempList(String isHave, PageParamRequest pageParamRequest);

    JSONObject save(String id, String tempId);

    JSONObject account(SmsLoginRequest smsLoginRequest);

    JSONObject info();

    JSONObject isLogin();

    void logOut();

    JSONObject payTempList(PageParamRequest pageParamRequest);

    JSONObject getPayQrCode(String payType, Integer mealId, BigDecimal price, Integer num);

    JSONObject applyTempMessage(String title, String content, Integer type);

    JSONObject tempList(String title, Integer status, String type, PageParamRequest pageParamRequest);

    boolean pushCodeToList(String phone, Integer tag,HashMap<String, Object> pram);

    void push(String phone,String tempKey,Integer msgTempId,boolean valid, HashMap<String,Object> mapPram);
    /**
     * 根据发送id同步发送短信结果
     * @param recordIds 短信发送id
     */
    void pushByAsyncStatus(String recordIds);

    void consume();

    boolean sendCode(SendSmsVo sendSmsVo);

    /**
     * 注册短信发送
     * @param phone 手机号码
     * @return 发送结果
     */
    JSONObject sendCodeForRegister(String phone);
}
