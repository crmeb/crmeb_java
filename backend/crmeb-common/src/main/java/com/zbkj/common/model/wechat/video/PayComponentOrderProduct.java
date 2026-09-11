package com.zbkj.common.model.wechat.video;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 组件订单详情表
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_pay_component_order_product")
@ApiModel(value="PayComponentOrderProduct对象", description="组件订单详情表")
public class PayComponentOrderProduct implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号,商家自定义订单ID")
    private String orderNo;

    @ApiModelProperty(value = "微信侧订单id ")
    private Long componentOrderId;

    @ApiModelProperty(value = "交易组件平台内部商品ID")
    private Integer productId;

    @ApiModelProperty(value = "商家自定义商品ID")
    private String outProductId;

    @ApiModelProperty(value = "交易组件平台内部skuID，可填0（如果这个product_id下没有sku）")
    private Integer skuId;

    @ApiModelProperty(value = "商家自定义商品skuID，可填空字符串（如果这个product_id下没有sku）")
    private String outSkuId;

    @ApiModelProperty(value = "购买的数量")
    private Integer productCnt;

    @ApiModelProperty(value = "生成这次订单时商品的售卖价（单位：分），可以跟上传商品接口的价格不一致")
    private Long salePrice;

    @ApiModelProperty(value = "生成订单时商品的头图")
    private String headImg;

    @ApiModelProperty(value = "生成订单时商品的标题")
    private String title;

    @ApiModelProperty(value = "绑定的小程序商品路径")
    private String path;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


}
