package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 移动端商家管理工作台统计响应
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MobileAdminManageStatisticsResponse", description = "移动端商家管理工作台统计响应")
public class MobileAdminManageStatisticsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "今日销售额")
    private BigDecimal todayOrderPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "今日订单数")
    private Integer todayOrderCount = 0;

    @ApiModelProperty(value = "今日支付人数")
    private Integer todayOrderUserCount = 0;

    @ApiModelProperty(value = "今日浏览量")
    private Integer todayVisitCount = 0;

    @ApiModelProperty(value = "待发货订单数")
    private Integer unDeliveryOrderCount = 0;

    @ApiModelProperty(value = "售后处理中订单数")
    private Integer refundingCount = 0;

    @ApiModelProperty(value = "售罄商品数")
    private Integer outOfStock = 0;

    @ApiModelProperty(value = "库存预警商品数")
    private Integer policeForce = 0;
}
