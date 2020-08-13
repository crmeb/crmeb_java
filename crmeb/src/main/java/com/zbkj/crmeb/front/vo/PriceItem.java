package com.zbkj.crmeb.front.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单计算Item属性对象
 */
@Data
public class PriceItem {
    private BigDecimal num;
    private BigDecimal price;
    private BigDecimal first;
    private BigDecimal firstPrice;
    private BigDecimal renewal;
    private BigDecimal renewalPrice;
    private Integer tempId;
    private Integer cityId;

    public PriceItem() {

    }

    public PriceItem(BigDecimal num, BigDecimal price, BigDecimal first, BigDecimal firstPrice, BigDecimal renewal, BigDecimal renewalPrice, Integer tempId, Integer cityId) {
        this.num = num;
        this.price = price;
        this.first = first;
        this.firstPrice = firstPrice;
        this.renewal = renewal;
        this.renewalPrice = renewalPrice;
        this.tempId = tempId;
        this.cityId = cityId;
    }
}
