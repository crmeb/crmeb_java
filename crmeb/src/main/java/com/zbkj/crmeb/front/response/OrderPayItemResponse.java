package com.zbkj.crmeb.front.response;

import lombok.Data;

/**
 * @author stivepeim
 * @title: OrderPayItemResponse
 * @projectName crmeb
 * @description: 支付订单 Response item
 * @date 2020/7/1418:13
 */
@Data
public class OrderPayItemResponse {
    private String key;
    private Object orderId;

    public OrderPayItemResponse() {
    }

    public OrderPayItemResponse(String key, Object orderId) {
        this.key = key;
        this.orderId = orderId;
    }
}
