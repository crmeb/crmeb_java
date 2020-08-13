package com.zbkj.crmeb.front.response;

import lombok.Data;

/**
 * @author stivepeim
 * @title: OrderPayResponse
 * @projectName crmeb
 * @description: 支付订单 Response
 * @date 2020/7/1418:06
 */
@Data
public class OrderPayResponse {

    private String status;

    private OrderPayItemResponse result; // 非线上支付对象

    private Object jsConfig; // 线上支付对象 todo 后面抽象对象

    private String message;

    public OrderPayResponse() {
    }

    public OrderPayResponse(String status, OrderPayItemResponse result) {
        this.status = status;
        this.result = result;
    }
}
