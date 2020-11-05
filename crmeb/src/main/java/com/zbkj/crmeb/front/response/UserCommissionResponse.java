package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 推广佣金明细
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
@ApiModel(value="UserCommissionResponse对象", description="推广佣金明细")
public class UserCommissionResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "昨天的佣金")
    private BigDecimal lastDayCount = BigDecimal.ZERO;

    @ApiModelProperty(value = "累计提现金额")
    private BigDecimal extractCount = BigDecimal.ZERO;

    @ApiModelProperty(value = "当前佣金")
    private BigDecimal commissionCount = BigDecimal.ZERO;
}
