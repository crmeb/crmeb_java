package com.zbkj.common.response;

import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.common.model.product.StoreProductAttr;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 商品详情
 * +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ProductDetailResponse对象", description="商品详情H5")
public class ProductDetailResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "产品属性")
    private List<StoreProductAttr> productAttr;

    @ApiModelProperty(value = "商品属性详情")
    private HashMap<String, Object> productValue;

    @ApiModelProperty(value = "返佣金额区间")
    private String priceName;

    @ApiModelProperty(value = "为移动端特定参数 所有参与的活动")
    private List<ProductActivityItemResponse> activityAllH5;

    @ApiModelProperty(value = "商品信息")
    private StoreProduct productInfo;

    @ApiModelProperty(value = "收藏标识")
    private Boolean userCollect;
}
