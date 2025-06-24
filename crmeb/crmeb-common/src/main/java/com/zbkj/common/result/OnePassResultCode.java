package com.zbkj.common.result;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.exception.BusinessExceptionAssert;

/**
 * @ClassName CouponResultCode
 * @Description 优惠券模块响应码
 * @Author HZW
 * @Date 2023/5/17 9:17
 * @Version 1.0
 */
public enum OnePassResultCode implements BusinessExceptionAssert {

    ACCESS_KEY_NOT_CONFIG(12001, "请配置一号通access_key一号通后台应用管理获得APPID！"),
    SECRET_KEY_NOT_CONFIG(12002, "请配置一号通secret_key一号通后台应用管理获得AppSecret"),

    SMS_NOT_OPEN(12101, "短信业务未开通"),
    SMS_SHORTAGE_IN_NUMBER(12102, "短信业务数量不足"),
    ;

    OnePassResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 响应状态码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 响应信息补充
     */
    private Object[] msgParams;


    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public OnePassResultCode setCode(Integer code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        if (ArrayUtil.isNotEmpty(msgParams)) {
            return StrUtil.format(message, msgParams);
        }
        return message;
    }

    @Override
    public OnePassResultCode setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public IResultEnum setMsgParams(Object... msgParams) {
        this.msgParams = msgParams;
        return this;
    }

    @Override
    public Object[] getMsgParams() {
        return msgParams;
    }
}
