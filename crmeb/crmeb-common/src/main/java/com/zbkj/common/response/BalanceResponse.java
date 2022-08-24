package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户地址表
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BalanceResponse对象", description="提现金额")
public class BalanceResponse implements Serializable {

    public BalanceResponse() {}
    public BalanceResponse(BigDecimal withdrawn, BigDecimal unDrawn, BigDecimal commissionTotal, BigDecimal toBeWithdrawn) {
        this.withdrawn = withdrawn;
        this.unDrawn = unDrawn;
        this.commissionTotal = commissionTotal;
        ToBeWithdrawn = toBeWithdrawn;
    }
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "已提现")
    private BigDecimal withdrawn;

    @ApiModelProperty(value = "未提现")
    private BigDecimal unDrawn;

    @ApiModelProperty(value = "佣金总金额")
    private BigDecimal commissionTotal;

    @ApiModelProperty(value = "待提现")
    private BigDecimal ToBeWithdrawn;

}
