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
public enum CouponResultCode implements BusinessExceptionAssert {

    COUPON_NOT_EXIST(10001, "优惠券不存在"),
    COUPON_STATUS_ABNORMAL(10002, "订单状态异常"),

    COUPON_PRODUCT_NOT_EXIST(10101, "优惠券对应商品不存在"),
    ;

    CouponResultCode(Integer code, String message) {
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
    public CouponResultCode setCode(Integer code) {
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
    public CouponResultCode setMessage(String message) {
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
