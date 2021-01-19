package com.zbkj.crmeb.store.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * 订单退款表
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
@ApiModel(value="StoreOrderRefundRequest对象", description="订单退款")
public class StoreOrderRefundRequest {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单id")
    @Min(value = 1, message = "请选择订单")
    private Integer orderId;

    @ApiModelProperty(value = "退款金额")
    @DecimalMin(value = "0.00", message = "退款金额不能少于0.00")
    private BigDecimal amount;

}
