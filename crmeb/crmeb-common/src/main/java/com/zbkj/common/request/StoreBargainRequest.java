package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 砍价商品Request
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
@ApiModel(value="StoreBargain对象", description="砍价表")
public class StoreBargainRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "砍价商品id|新增时不填，修改时必填")
    private Integer id;

    @ApiModelProperty(value = "关联商品ID")
    @NotNull(message = "商品编号不能为空")
    @Min(value = 1, message = "关联商品ID不能小于1")
    private Integer productId;

    @ApiModelProperty(value = "砍价活动名称")
    @NotBlank(message = "砍价活动名称不能为空")
    @Length(max = 200, message = "砍价活动名称不能超过200个字符")
    private String title;

    @ApiModelProperty(value = "砍价活动图片")
    @NotBlank(message = "砍价活动图片不能为空")
    @Length(max = 150, message = "砍价活动图片不能超过150个字符")
    private String image;

    @ApiModelProperty(value = "单位名称")
    @NotBlank(message = "单位名称不能为空")
    @Length(max = 16, message = "单位名称不能超过16个字符")
    private String unitName;

    @ApiModelProperty(value = "砍价商品轮播图")
    @NotBlank(message = "轮播图不能为空")
    @Length(max = 2000, message = "砍价商品轮播图不能超过2000个字符")
    private String images;

    @ApiModelProperty(value = "砍价开启时间")
    @NotBlank(message = "砍价开启时间不能为空")
    private String startTime;

    @ApiModelProperty(value = "砍价结束时间")
    @NotBlank(message = "砍价结束时间不能为空")
    private String stopTime;

    @ApiModelProperty(value = "砍价商品名称")
    @NotBlank(message = "砍价商品名称不能为空")
    @Length(max = 200, message = "砍价商品名称不能超过200个字符")
    private String storeName;

    @ApiModelProperty(value = "砍价金额")
    private BigDecimal price;

    @ApiModelProperty(value = "砍价商品最低价")
    private BigDecimal minPrice;

    @ApiModelProperty(value = "购买数量限制")
    @NotNull(message = "购买数量限制不能为空")
    @Min(value = 1, message = "购买数量限制必须大于0")
    private Integer num;

//    @ApiModelProperty(value = "用户每次砍价的最大金额")
//    private BigDecimal bargainMaxPrice;

//    @ApiModelProperty(value = "用户每次砍价的最小金额")
//    private BigDecimal bargainMinPrice;

    @ApiModelProperty(value = "帮砍次数")
    @NotNull(message = "帮砍次数不能为空")
    @Min(value = 1, message = "帮砍次数必须大于0")
    private Integer bargainNum;

    @ApiModelProperty(value = "砍价状态 0(到砍价时间不自动开启)  1(到砍价时间自动开启时间)")
    @NotNull(message = "砍价活动状态不能为空")
    private Boolean status;

//    @ApiModelProperty(value = "砍价活动简介")
//    @NotBlank(message = "砍价活动简介不能为空")
//    private String info;

//    @ApiModelProperty(value = "砍价规则")
//    private String rule;

    @ApiModelProperty(value = "运费模板ID")
    @NotNull(message = "运费模板ID不能为空")
    private Integer tempId;

    @ApiModelProperty(value = "砍价活动发起人数")
    @Min(value = 2, message = "砍价人数最少两人")
    @Max(value = 9999, message = "砍价人数最多9999")
    private Integer peopleNum;

    @ApiModelProperty(value = "商品属性")
    @NotEmpty(message = "商品属性不能为空")
    private List<StoreProductAttrAddRequest> attr;

    @ApiModelProperty(value = "商品属性详情")
    @NotEmpty(message = "商品属性详情不能为空")
    private List<StoreProductAttrValueAddRequest> attrValue;

    @ApiModelProperty(value = "商品描述")
    private String content;
}
