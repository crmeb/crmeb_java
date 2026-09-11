package com.zbkj.common.result;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.exception.BusinessExceptionAssert;

/**
 * @ClassName UserResultCode
 * @Description 用户模块响应状态码
 * @Author HZW
 * @Date 2023/3/7 12:20
 * @Version 1.0
 */
public enum UserResultCode implements BusinessExceptionAssert {

    USER_EXIST(8101, "用户已存在"),
    USER_NOT_EXIST(8102, "用户不存在"),
    USER_ID_NULL(8103, "请选择用户"),
    USER_STATUS_EXCEPTION(8104, "用户状态异常"),
    USER_LOGOFF(8105, "用户已经注销"),
    USER_REGISTER_FAILED(8106, "用户注册失败"),
    USER_PHONE_EXIST(8107, "用户手机号已存在"),
    USER_LOGOFF_FAILED(8108, "用户注销失败"),
    USER_NOT_COLLECT_MERCHANT(8109, "用户未收藏商户"),
    USER_BALANCE_INSUFFICIENT(8110, "用户余额不足"),

    USER_ADDRESS_NOT_EXIST(8201, "用户地址不存在"),
    USER_ADDRESS_CREATE_FAILED(8202, "用户地址创建失败"),

    USER_CLOSING_PROCESSING(8301, "用户结算申请正常处理中"),
    USER_CLOSING_NULL(8302, "用户结算单不存在"),
    USER_CLOSING_AUDIT_STATUS_EXCEPTION(8303, "用户结算单审核状态异常"),


    USER_BROKERAGE_INSUFFICIENT(8401, "用户佣金不足"),

    USER_TAG_USING(8501, "用户标签正在使用中"),

    ;

    UserResultCode(Integer code, String message) {
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
    public UserResultCode setCode(Integer code) {
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
    public UserResultCode setMessage(String message) {
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
