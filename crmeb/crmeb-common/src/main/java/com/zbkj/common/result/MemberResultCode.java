package com.zbkj.common.result;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.exception.BusinessExceptionAssert;

/**
 * 会员响应码
 *
 * @author Hzw
 * @version 1.0.0
 * @Date 2023/11/14
 */
public enum MemberResultCode implements BusinessExceptionAssert {

    PAID_MEMBER_CARD_NOT_EXIST(14001, "付费会员卡不存在"),
    PAID_MEMBER_CARD_CLOSE(14002, "付费会员卡已关闭"),
    PAID_MEMBER_CARD_RESELECT(14003, "请重新选择会员卡"),
    PAID_MEMBER_ORDER_NOT_EXIST(14101, "付费会员订单不存在"),

    ;

    MemberResultCode(Integer code, String message) {
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
    public MemberResultCode setCode(Integer code) {
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
    public MemberResultCode setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public MemberResultCode setMsgParams(Object... msgParams) {
        this.msgParams = msgParams;
        return this;
    }

    @Override
    public Object[] getMsgParams() {
        return msgParams;
    }
}
