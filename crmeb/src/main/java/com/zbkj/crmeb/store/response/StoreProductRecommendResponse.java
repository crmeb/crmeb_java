package com.zbkj.crmeb.store.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Classname StoreProductRecommendResponse
 * @Description 推荐商品列表response
 * @Date 2020/6/30 10:56 上午
 * @Created by stivepeim
 */
@Data
public class StoreProductRecommendResponse {

    @ApiModelProperty(value = "秒杀，团购，砍价")
    private List activity;

    @ApiModelProperty(value = "可用优惠券")
    private Boolean checkCoupon;

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "img Url")
    private String image;

    @ApiModelProperty(value = "市场价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "商品价")
    private BigDecimal price;

    @ApiModelProperty(value = "商品名称")
    private String storeName;
}
