package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author stivepeim
 * @title: GetProductReply
 * @projectName crmeb
 * @description: TODO
 * @date 2020/8/517:44
 */
@Data
public class GetProductReply {

    @ApiModelProperty(value = "商品attrid")
    @NotBlank(message = "商品uniId不能为空")
    private String uni;

    @ApiModelProperty(value = "订单id")
    @NotNull(message = "订单id不能为空")
    private Integer orderId;
}
