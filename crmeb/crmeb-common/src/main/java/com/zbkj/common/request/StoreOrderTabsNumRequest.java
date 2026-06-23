package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "StoreOrderTabsNumRequest", description = "订单表头请求对象")
public class StoreOrderTabsNumRequest extends UserCommonSearchRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "创建时间区间")
    private String dateLimit;

    @ApiModelProperty(value = "订单类型：0普通订单，1-视频号订单, 2-全部订单")
    @NotNull(message = "订单类型不能为空")
    @Range(min = 0, max = 2, message = "未知的订单类型")
    private Integer type;

    @ApiModelProperty(value = "物流单号")
    private String deliveryId;
}
