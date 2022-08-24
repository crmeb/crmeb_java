package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 交易概览数据对象
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
@ApiModel(value="TradingDataResponse对象", description="交易概览数据对象")
public class TradingDataResponse implements Serializable {

    private static final long serialVersionUID = -6332062115310922579L;

    @ApiModelProperty(value = "营业额")
    private BigDecimal turnover;

    @ApiModelProperty(value = "营业额环比")
    private String turnoverRatio;

    @ApiModelProperty(value = "商品支付金额")
    private BigDecimal proPayAmount;

    @ApiModelProperty(value = "商品支付金额环比")
    private String proPayAmountRatio;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal rechargeAmount;

    @ApiModelProperty(value = "充值金额环比")
    private String rechargeAmountRatio;

    @ApiModelProperty(value = "支出金额")
    private BigDecimal payoutAmount;

    @ApiModelProperty(value = "支出金额环比")
    private String payoutAmountRatio;

    @ApiModelProperty(value = "余额支付金额")
    private BigDecimal balanceAmount;

    @ApiModelProperty(value = "余额支付金额月环比")
    private String balanceAmountRatio;

    @ApiModelProperty(value = "支付佣金金额")
    private BigDecimal payoutBrokerageAmount;

    @ApiModelProperty(value = "支付佣金金额环比")
    private String payoutBrokerageAmountRatio;

    @ApiModelProperty(value = "商品退款金额")
    private BigDecimal proRefundAmount;

    @ApiModelProperty(value = "商品退款金额环比")
    private String proRefundAmountRatio;

}
