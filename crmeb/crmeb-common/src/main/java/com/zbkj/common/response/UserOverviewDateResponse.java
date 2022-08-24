package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户概览日数据对象
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
@ApiModel(value="UserOverviewDateResponse对象", description="用户概览日数据对象")
public class UserOverviewDateResponse implements Serializable {

    private static final long serialVersionUID = -6332062115310922579L;

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "注册用户数")
    private Integer registerNum;

    @ApiModelProperty(value = "活跃用户数")
    private Integer activeUserNum;

    @ApiModelProperty(value = "充值用户数")
    private Integer rechargeUserNum;

    @ApiModelProperty(value = "浏览量")
    private Integer pageviews;

    @ApiModelProperty(value = "下单用户数量")
    private Integer orderUserNum;

    @ApiModelProperty(value = "成交用户数量")
    private Integer orderPayUserNum;

    @ApiModelProperty(value = "成交金额")
    private BigDecimal payOrderAmount;

    @ApiModelProperty(value = "客单价")
    private BigDecimal customerPrice;

}
