package com.zbkj.crmeb.seckill.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

/**
 * 秒杀商品属性值
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
@TableName("StoreSeckillAttrValueRequest")
@ApiModel(value="StoreSeckillAttrValueRequest对象", description="秒杀商品属性值对象")
public class StoreSeckillAttrValueRequest {
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

    @ApiModelProperty(value = "唯一值")
    private String unique;

    @ApiModelProperty(value = "成本价")
    private BigDecimal cost;

    @ApiModelProperty(value = "秒杀数量")
    private Integer num;

    @ApiModelProperty(value = "原价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "秒杀价价")
    private BigDecimal killPrice;

    @ApiModelProperty(value = "attrValue字段，前端传递后用作sku字段")
    private LinkedHashMap<String,String> attrValue;

}
