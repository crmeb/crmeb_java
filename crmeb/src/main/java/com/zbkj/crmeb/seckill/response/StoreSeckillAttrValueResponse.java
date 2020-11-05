package com.zbkj.crmeb.seckill.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Classname StoreSeckillAttrValueResponse
 * @Description 商品秒杀属性值 response
 * @Date 9/19/20 11:48 上午
 * @Created by stivepeim
 */
@Data
public class StoreSeckillAttrValueResponse {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "商品属性索引值 (attr_value|attr_value[|....])")
    private String suk;

    @ApiModelProperty(value = "属性对应的库存")
    private Integer stock;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "属性金额")
    private BigDecimal price;

    @ApiModelProperty(value = "图片")
    private String image;

    @ApiModelProperty(value = "秒杀数量")
    private Integer num;

    @ApiModelProperty(value = "原价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "秒杀价价")
    private BigDecimal killPrice;

    @ApiModelProperty(value = "成本价")
    private BigDecimal cost;
}
