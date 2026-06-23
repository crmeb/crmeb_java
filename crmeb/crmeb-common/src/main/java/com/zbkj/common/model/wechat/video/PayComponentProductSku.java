package com.zbkj.common.model.wechat.video;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 组件商品sku表
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
@TableName("eb_pay_component_product_sku")
@ApiModel(value="PayComponentProductSku对象", description="组件商品sku表")
public class PayComponentProductSku implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID，商家自定义skuID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "交易组件平台内部商品ID")
    private Integer componentProductId;

    @ApiModelProperty(value = "交易组件平台自定义skuID")
    private String skuId;

    @ApiModelProperty(value = "sku小图")
    private String thumbImg;

    @ApiModelProperty(value = "售卖价格,以分为单位")
    private Long salePrice;

    @ApiModelProperty(value = "市场价格,以分为单位")
    private Long marketPrice;

    @ApiModelProperty(value = "库存")
    private Integer stockNum;

    @ApiModelProperty(value = "条形码")
    private String barcode;

    @ApiModelProperty(value = "商品编码")
    private String skuCode;

    @ApiModelProperty(value = "是否删除 0未删除 1删除")
    private Boolean isDel;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "商品规格属性id")
    private Integer attrValueId;

    @ApiModelProperty(value = "attrList")
    @TableField(exist = false)
    private List<PayComponentProductSkuAttr> attrList;

    @ApiModelProperty(value = "并发版本控制")
    private Integer version;
}
