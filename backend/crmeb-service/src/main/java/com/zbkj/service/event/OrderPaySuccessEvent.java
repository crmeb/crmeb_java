package com.zbkj.service.event;

/**
 * 订单支付成功后置处理事件
 */
public class OrderPaySuccessEvent {

    private final String orderNo;

    public OrderPaySuccessEvent(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }
}
