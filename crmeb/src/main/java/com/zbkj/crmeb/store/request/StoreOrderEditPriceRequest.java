package com.zbkj.crmeb.store.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 订单改价请求对象
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
@ApiModel(value="StoreOrderEditPriceRequest对象", description="订单改价请求对象")
public class StoreOrderEditPriceRequest {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "订单金额")
    @DecimalMin(value = "0.00", message = "订单金额不能少于0.00")
    @NotNull(message = "订单金额不能为空")
    private BigDecimal price;

}
