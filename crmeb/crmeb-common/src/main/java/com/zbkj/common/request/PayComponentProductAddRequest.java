package com.zbkj.common.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 组件商品添加Request对象
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
@ApiModel(value="PayComponentProductAddRequest对象", description="组件商品添加Request对象")
public class PayComponentProductAddRequest implements Serializable {

    private static final long serialVersionUID = -2196197495866986580L;

    @ApiModelProperty(value = "商品ID(更新时必填)")
    private Integer id;

    @ApiModelProperty(value = "主商品ID", required = true)
    @NotNull(message = "请先选择主商品")
    private Integer primaryProductId;

    @ApiModelProperty(value = "标题", required = true)
    @NotNull(message = "标题不能为空")
    private String title;

    @ApiModelProperty(value = "轮播图,多张", required = true)
    @NotNull(message = "轮播图不能为空")
    private String headImg;

    @ApiModelProperty(value = "商品资质图片,多张")
    private String qualificationPics;

    @ApiModelProperty(value = "第三级类目ID", required = true)
    @NotNull(message = "第三级类目不能为空")
    private Integer thirdCatId;

    @ApiModelProperty(value = "品牌id")
    private Integer brandId;

    @ApiModelProperty(value = "商品详情")
    private String descInfo;

    @ApiModelProperty(value = "商品详情图片,多图")
    private String descImgs;

    @ApiModelProperty(value = "运费模板ID", required = true)
    @NotNull(message = "运费模板不能为空")
    private Integer tempId;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "单位名", required = true)
    private String unitName;

    @ApiModelProperty(value = "获得积分", required = true)
    private Integer giveIntegral;

    @ApiModelProperty(value = "虚拟销量", required = true)
    private Integer ficti;

    @ApiModelProperty(value = "规格 0单 1多", required = true)
    private Boolean specType;

    @ApiModelProperty(value = "商品属性")
    private List<StoreProductAttrAddRequest> attr;

    @ApiModelProperty(value = "商品属性详情")
    private List<StoreProductAttrValueAddRequest> attrValue;
}
