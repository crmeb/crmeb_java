package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 商城商品统计数据对象
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
@ApiModel(value="ShoppingProductDataResponse对象", description="商城商品统计数据对象")
public class ShoppingProductDataResponse implements Serializable {

    private static final long serialVersionUID = -2853994865375523003L;

    @ApiModelProperty(value = "新增商品数量")
    private Integer newProductNum;

    @ApiModelProperty(value = "新增商品数量环比")
    private String newProductNumRatio;

    @ApiModelProperty(value = "浏览量")
    private Integer pageView;

    @ApiModelProperty(value = "浏览量环比")
    private String pageViewRatio;

    @ApiModelProperty(value = "收藏量")
    private Integer collectNum;

    @ApiModelProperty(value = "收藏量环比")
    private String collectNumRatio;

    @ApiModelProperty(value = "加购件数")
    private Integer addCartNum;

    @ApiModelProperty(value = "加购件数环比")
    private String addCartNumRatio;

    @ApiModelProperty(value = "交易总件数")
    private Integer orderProductNum;

    @ApiModelProperty(value = "交易总件数环比")
    private String orderProductNumRatio;

    @ApiModelProperty(value = "交易成功件数")
    private Integer orderSuccessProductNum;

    @ApiModelProperty(value = "交易成功件数环比")
    private String orderSuccessProductNumRatio;

}
