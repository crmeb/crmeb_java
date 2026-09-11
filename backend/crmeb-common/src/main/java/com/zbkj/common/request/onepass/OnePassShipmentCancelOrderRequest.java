package com.zbkj.common.request.onepass;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Auther: 大粽子
 * @Date: 2023/8/26 15:40
 * @Description: 一号通 取消商家寄件
 */
@Data
public class OnePassShipmentCancelOrderRequest {

    @ApiModelProperty(value = "任务ID，在[创建商家寄件订单]接口中获得", required = true)
    @NotNull(message = "任务ID 不能为空")
    public String taskId;

    @ApiModelProperty(value = "订单ID，在[创建商家寄件订单]接口中获得", required = true)
    @NotNull(message = "订单ID 不能为空")
    public String orderId;

    @ApiModelProperty(value = "取消理由", required = true)
    @NotNull(message = "取消理由 不能为空")
    public String cancelMsg;
}
