package com.zbkj.common.result;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.exception.BusinessExceptionAssert;

/**
 * 订单响应码
 *
 * @author Hzw
 * @version 1.0.0
 * @Date 2023/11/14
 */
public enum OrderResultCode implements BusinessExceptionAssert {

    ORDER_NOT_EXIST(5001, "订单不存在"),
    ORDER_DELETE(5002, "订单已删除"),
    ORDER_LEVEL_ABNORMAL(5003, "订单等级异常"),
    ORDER_REFUND_ED(5004, "订单已退款"),
    ORDER_STATUS_ABNORMAL(5005, "订单状态异常"),
    ORDER_SHIPPING_TYPE_PICK_UP(5006, "订单配送方式门店自提"),
    ORDER_USER_NOT_DELETE(5007, "订单用户未删除"),
    ORDER_VERIFICATION(5008, "订单已核销"),
    PRE_ORDER_NOT_EXIST(5009, "预下单订单不存在"),
    ORDER_CANCEL(5009, "订单已经取消"),
    ORDER_PAID(5009, "订单已经支付"),
    ORDER_NOT_PAID(5010, "订单未支付"),

    ORDER_INVOICE_NOT_EXIST(5101, "订单发货单不存在"),
    ORDER_INVOICE_LOGISTICS_NOT_EXIST(5102, "订单发货单物流不存在"),

    ORDER_DETAIL_NOT_EXIST(5201, "订单详情不存在"),
    ORDER_DETAIL_RECEIPT(5202, "订单详情已收货"),
    ORDER_DETAIL_REPLY(5203, "订单详情已评价"),

    REFUND_ORDER_NOT_EXIST(5501, "退款单不存在"),
    REFUND_ORDER_STATUS_ABNORMAL(5502, "退款单状态异常"),
    ;

    OrderResultCode(Integer code, String message) {
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
    public OrderResultCode setCode(Integer code) {
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
    public OrderResultCode setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public OrderResultCode setMsgParams(Object... msgParams) {
        this.msgParams = msgParams;
        return this;
    }

    @Override
    public Object[] getMsgParams() {
        return msgParams;
    }
}
