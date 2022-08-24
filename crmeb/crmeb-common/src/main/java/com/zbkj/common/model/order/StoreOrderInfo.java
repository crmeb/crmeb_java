package com.zbkj.common.model.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 订单购物详情表
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_order_info")
@ApiModel(value="StoreOrderInfo对象", description="订单购物详情表")
public class StoreOrderInfo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单id")
    private Integer orderId;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "购买东西的详细信息")
    private String info;

    @ApiModelProperty(value = "唯一id")
    @TableField(value = "`unique`")
    private String unique;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "规格属性id")
    private Integer attrValueId;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "购买数量")
    private Integer payNum;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积")
    private BigDecimal volume;

    @ApiModelProperty(value = "获得积分")
    private Integer giveIntegral;

    @ApiModelProperty(value = "是否评价")
    private Boolean isReply;

    @ApiModelProperty(value = "是否单独分佣")
    private Boolean isSub;

    @ApiModelProperty(value = "会员价")
    private BigDecimal vipPrice;

    @ApiModelProperty(value = "商品类型:0-普通，1-秒杀，2-砍价，3-拼团，4-视频号")
    private Integer productType;
}
