package com.zbkj.crmeb.store.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Classname RetailShopOrderDataResponse
 * @Description 分销用户订单数据
 * @Date 2020/6/22 3:39 下午
 * @Created by stivepeim
 */
@Data
public class RetailShopOrderDataResponse {
    // 总订单价
    @ApiModelProperty(value = "订单总价")
    private BigDecimal orderPrice;
    // 订单数量
    @ApiModelProperty(value = "订单数量")
    private Integer orderCount;
    // 用户id
//    private Integer uid;
}
