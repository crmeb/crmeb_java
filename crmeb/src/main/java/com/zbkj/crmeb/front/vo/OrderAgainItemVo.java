package com.zbkj.crmeb.front.vo;

import lombok.Data;

/**
 * @author stivepeim
 * @title: OrderAgainItemVo
 * @projectName crmeb
 * @description: 再次下单status Item
 * @date 2020/7/1517:50
 */
@Data
public class OrderAgainItemVo {

    public OrderAgainItemVo() {
    }

    public OrderAgainItemVo(Integer type, String title, String msg) {
        this.type = type;
        this.title = title;
        this.msg = msg;
    }

    private Integer type;
    private String title;
    private String msg;
    private String payType;
    private String deliveryType;

}
