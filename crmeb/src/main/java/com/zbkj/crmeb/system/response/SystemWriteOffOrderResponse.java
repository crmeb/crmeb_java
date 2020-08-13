package com.zbkj.crmeb.system.response;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.ArrayList;

import com.common.CommonPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SystemWriteOffOrderResponse对象", description="核销订单")
public class SystemWriteOffOrderResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单总数量")
    private Long total = 0L;

    @ApiModelProperty(value = "订单总金额")
    private BigDecimal orderTotalPrice;

    @ApiModelProperty(value = "退款总金额")
    private BigDecimal refundTotalPrice;

    @ApiModelProperty(value = "退款总单数")
    private Integer refundTotal = 0;

    @ApiModelProperty(value = "订单列表")
    private CommonPage<StoreOrderItemResponse> list;
}
