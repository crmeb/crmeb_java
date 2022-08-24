package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单数量响应对象
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
@ApiModel(value="OrderDataResponse对象", description="订单数量响应对象")
public class OrderDataResponse implements Serializable {

    private static final long serialVersionUID = 1387727608277207652L;

    @ApiModelProperty(value = "已完成订单数量")
    private Integer completeCount;

    @ApiModelProperty(value = "待核销订单数量")
    private Integer evaluatedCount;

//    @ApiModelProperty(value = "用户昵称")
//    private Integer verificationCount;

    @ApiModelProperty(value = "支付订单总数")
    private Integer orderCount;

    @ApiModelProperty(value = "待收货订单数量")
    private Integer receivedCount;

    @ApiModelProperty(value = "退款订单数量")
    private Integer refundCount;

    @ApiModelProperty(value = "总消费钱数")
    private BigDecimal sumPrice;

    @ApiModelProperty(value = "未支付订单数量")
    private Integer unPaidCount;

    @ApiModelProperty(value = "待发货订单数量")
    private Integer unShippedCount;
}
