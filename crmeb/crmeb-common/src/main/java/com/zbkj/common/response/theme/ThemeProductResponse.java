package com.zbkj.common.response.theme;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 主题商品列表响应对象
 */
@Data
@ApiModel(value = "ThemeProductResponse对象", description = "主题商品列表响应对象")
public class ThemeProductResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品ID")
    private Integer id;

    @JsonProperty(value = "store_name")
    @ApiModelProperty(value = "商品名称")
    private String storeName;

    @JsonProperty(value = "cate_id")
    @ApiModelProperty(value = "分类ID")
    private String cateId;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "销量")
    private String sales;

    @ApiModelProperty(value = "商品价格")
    private BigDecimal price;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "活动显示排序 0=默认，1=秒杀，2=砍价，3=拼团")
    private String activity;

    @JsonProperty(value = "ot_price")
    @ApiModelProperty(value = "市场价")
    private BigDecimal otPrice;

    @JsonProperty(value = "spec_type")
    @ApiModelProperty(value = "规格 0单 1多")
    private Integer specType;

    @JsonProperty(value = "unit_name")
    @ApiModelProperty(value = "单位名")
    private String unitName;
}
