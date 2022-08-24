package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 交易统计数据对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="TradeDataResponse对象", description="交易统计数据对象")
public class TradeDataResponse implements Serializable {

    private static final long serialVersionUID = -6332062115310922579L;

    @ApiModelProperty(value = "今日订单数量")
    private Integer todayOrderNum;

    @ApiModelProperty(value = "昨日订单数量")
    private Integer yesterdayOrderNum;

    @ApiModelProperty(value = "昨日交易数量日环比")
    private String yesterdayOrderNumRatio;

    @ApiModelProperty(value = "昨日交易金额")
    private BigDecimal yesterdayOrderSales;

    @ApiModelProperty(value = "昨日交易金额日环比")
    private String yesterdayOrderSalesRatio;

    @ApiModelProperty(value = "本月交易数量")
    private Integer monthOrderNum;

    @ApiModelProperty(value = "本月交易数量月环比")
    private String monthOrderNumRatio;

    @ApiModelProperty(value = "本月交易金额")
    private BigDecimal monthOrderSales;

    @ApiModelProperty(value = "本月交易金额月环比")
    private String monthOrderSalesRatio;

}
