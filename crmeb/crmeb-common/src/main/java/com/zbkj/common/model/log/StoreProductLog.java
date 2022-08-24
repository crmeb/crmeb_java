package com.zbkj.common.model.log;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品统计
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
@TableName("eb_store_product_log")
@ApiModel(value="StoreProductLog对象", description="商品统计")
public class StoreProductLog implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商品统计表ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "类型visit,cart,order,pay,collect,refund")
    private String type;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "用户ID")
    private Integer uid;

    @ApiModelProperty(value = "是否浏览")
    private Boolean visitNum;

    @ApiModelProperty(value = "加入购物车数量")
    private Integer cartNum;

    @ApiModelProperty(value = "下单数量")
    private Integer orderNum;

    @ApiModelProperty(value = "支付数量")
    private Integer payNum;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "商品成本价")
    private BigDecimal costPrice;

    @ApiModelProperty(value = "支付用户ID")
    private Integer payUid;

    @ApiModelProperty(value = "退款数量")
    private Integer refundNum;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "收藏")
    private Boolean collectNum;

    @ApiModelProperty(value = "添加时间")
    private Long addTime;


}
