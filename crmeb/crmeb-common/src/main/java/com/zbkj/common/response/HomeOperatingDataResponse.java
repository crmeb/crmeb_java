package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 首页经营数据响应对象
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
@ApiModel(value="HomeOperatingDataResponse对象", description="首页经营数据响应对象")
public class HomeOperatingDataResponse implements Serializable {

    private static final long serialVersionUID = -1486435421582495511L;

    @ApiModelProperty(value = "待发货订单数量")
    private Integer notShippingOrderNum;

    @ApiModelProperty(value = "退款中订单数量")
    private Integer refundingOrderNum;

    @ApiModelProperty(value = "待核销订单数量")
    private Integer notWriteOffOrderNum;

    @ApiModelProperty(value = "库存预警商品数量")
    private Integer vigilanceInventoryNum;

    @ApiModelProperty(value = "上架商品数量")
    private Integer onSaleProductNum;

    @ApiModelProperty(value = "仓库中商品数量")
    private Integer notSaleProductNum;

    @ApiModelProperty(value = "提现申请待审核数量")
    private Integer notAuditNum;

    @ApiModelProperty(value = "用户充值总金额")
    private BigDecimal totalRechargeAmount;

}
