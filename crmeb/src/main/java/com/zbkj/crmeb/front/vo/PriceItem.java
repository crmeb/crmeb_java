package com.zbkj.crmeb.front.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单计算Item属性对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
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
