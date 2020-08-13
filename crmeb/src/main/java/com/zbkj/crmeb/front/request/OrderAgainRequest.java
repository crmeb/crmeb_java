package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author stivepeim
 * @title: OrderAgainRequest
 * @projectName crmeb
 * @description: TODO
 * @date 2020/7/1515:00
 */
@Data
public class OrderAgainRequest {

    @ApiModelProperty(value = "订单id")
    @NotNull(message = "uni参数不能为空")
    private String nui;
}
