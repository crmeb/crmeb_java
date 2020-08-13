package com.zbkj.crmeb.front.request;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author stivepeim
 * @title: CartResetRequest
 * @projectName crmeb
 * @description: 购物车重选request
 * @date 2020/7/2814:50
 */
@Data
public class CartResetRequest {

    @ApiModelProperty(value = "购物车id")
    @NotNull(message = "id 不能为空")
    private Long id;

    @ApiModelProperty(value = "购物车数量")
    @NotNull(message = "num 不能为空")
    private Integer num;

    @ApiModelProperty(value = "商品id")
    @NotNull(message = "productId 不能为空")
    private Integer productId;

    @ApiModelProperty(value = "AttrValue Id")
    @NotNull(message = "unique 不能为空")
    private Integer unique;
}
