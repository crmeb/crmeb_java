package com.zbkj.common.result;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.exception.BusinessExceptionAssert;

/**
 * @ClassName ResultCodeEnum
 * @Description 登录、注册、认证模块响应码枚举
 * @Author HZW
 * @Date 2023/2/21 16:10
 * @Version 1.0
 */
public enum LoginResultCode implements BusinessExceptionAssert {

    LOGIN_PASSWORD_ERROR(1001, "登录密码不正确"),
    CODE_IS_NOT_EXIST(1002, "code码不存在"),
    REQUEST_IS_FAIL(1003, "请求微信失败，请稍后再试"),
    USER_IS_NOT_ONLY(1004, "用户唯一标识获取失败"),
    USERNAME_NOT_EXIST(1005, "用户名不存在"),
    ACCOUNT_IS_DELETE(1006, "账号已被删除"),
    ACCOUNT_NOT_ENABLE(1007, "账号已被禁用"),
    LOGIN_INFO_IS_EMPTY(1008, "登录信息不能为空"),
    LOGIN_USERNAME_IS_EMPTY(1009, "账号不能为空"),
    LOGIN_PASSWORD_IS_EMPTY(1010, "密码不能为空"),
    LOGIN_CAPTCHA_ERROR(1011, "验证码不正确"),
    CANT_GET_NICKNAME(1012, "未获取到您的昵称信息~"),
    CANT_GET_AVATAR_URL(1013, "未获取到您的头像信息~"),
    OPENID_IS_EMPTY(1014, "openId不能为空"),
    USER_NOT_FOUND(1015, "用户不存在"),
    LOGIN_EXPIRE(1016, "登录状态已过期"),
    ;

    LoginResultCode(Integer code, String message) {
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
    public LoginResultCode setCode(Integer code) {
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
    public LoginResultCode setMessage(String message) {
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
