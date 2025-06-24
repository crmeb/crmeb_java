package com.zbkj.common.result;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.exception.BusinessExceptionAssert;

/**
 * @ClassName WechatResultCode
 * @Description 微信模块响应状态码
 * @Author HZW
 * @Date 2023/3/7 12:20
 * @Version 1.0
 */
public enum WechatResultCode implements BusinessExceptionAssert {

    ADMIN_EXIST(13001, "管理员已存在"),
    ADMIN_NOT_EXIST(13002, "管理员不存在"),

    PUBLIC_APPID_NOT_CONFIG(13101, "微信公众号appId未设置"),
    PUBLIC_SECRET_NOT_CONFIG(13101, "微信公众号secret未设置"),

    ROUTINE_APPID_NOT_CONFIG(13201, "微信小程序appId未设置"),
    ROUTINE_SECRET_NOT_CONFIG(13201, "微信小程序secret未设置"),
    ;

    WechatResultCode(Integer code, String message) {
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
    public WechatResultCode setCode(Integer code) {
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
    public WechatResultCode setMessage(String message) {
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
