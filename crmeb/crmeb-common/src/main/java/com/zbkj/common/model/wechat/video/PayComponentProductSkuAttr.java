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

/**
 * 组件商品sku属性表
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
@TableName("eb_pay_component_product_sku_attr")
@ApiModel(value="PayComponentProductSkuAttr对象", description="组件商品sku属性表")
public class PayComponentProductSkuAttr implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID，商家自定义skuID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "skuID")
    private Integer skuId;

    @ApiModelProperty(value = "交易组件平台自定义skuID")
    private String componentSkuId;

    @ApiModelProperty(value = "销售属性key（自定义）")
    private String attrKey;

    @ApiModelProperty(value = "销售属性value（自定义）")
    private String attrValue;

    @ApiModelProperty(value = "是否删除 0未删除 1删除")
    private Boolean isDel;


}
