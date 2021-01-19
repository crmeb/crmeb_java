package com.zbkj.crmeb.store.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 分销统计响应对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RetailShopStatisticsResponse", description="分销统计响应对象")
public class RetailShopStatisticsResponse {

    @ApiModelProperty(value = "分销人员人数")
    private Integer distributionNum;

    @ApiModelProperty(value = "发展会员人数")
    private Integer developNum;

    @ApiModelProperty(value = "订单总数")
    private Integer orderNum;

    @ApiModelProperty(value = "订单金额（元）")
    private BigDecimal orderPriceCount;

    @ApiModelProperty(value = "提现次数")
    private Integer withdrawCount;

    @ApiModelProperty(value = "未提现金额（元）")
    private BigDecimal noWithdrawPrice;

    public RetailShopStatisticsResponse() {}

    public RetailShopStatisticsResponse(Integer distributionNum, Integer developNum,
                                        Integer orderNum, BigDecimal orderPriceCount,
                                        Integer withdrawCount, BigDecimal noWithdrawPrice) {
        this.distributionNum = distributionNum;
        this.developNum = developNum;
        this.orderNum = orderNum;
        this.orderPriceCount = orderPriceCount;
        this.withdrawCount = withdrawCount;
        this.noWithdrawPrice = noWithdrawPrice;
    }
}
