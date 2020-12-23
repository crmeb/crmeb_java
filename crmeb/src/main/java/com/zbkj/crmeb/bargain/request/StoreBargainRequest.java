package com.zbkj.crmeb.bargain.request;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zbkj.crmeb.store.model.StoreProductAttr;
import com.zbkj.crmeb.store.request.StoreProductAttrValueRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 砍价商品Request
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
@TableName("eb_store_bargain")
@ApiModel(value="StoreBargain对象", description="砍价表")
public class StoreBargainRequest implements Serializable {

    private static final long serialVersionUID=1L;

    private Integer id;

    @ApiModelProperty(value = "关联商品ID")
    @NotNull(message = "商品编号不能为空")
    private Integer productId;

    @ApiModelProperty(value = "砍价活动名称")
    @NotBlank(message = "砍价活动名称不能为空")
    private String title;

    @ApiModelProperty(value = "砍价活动图片")
    @NotBlank(message = "砍价活动图片不能为空")
    private String image;

    @ApiModelProperty(value = "单位名称")
    private String unitName;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "砍价商品轮播图")
//    @NotBlank(message = "砍价商品轮播图不能为空")
    private String images;

    @ApiModelProperty(value = "砍价开启时间")
    @NotBlank(message = "砍价开启时间不能为空")
    private String startTime;

    @ApiModelProperty(value = "砍价结束时间")
    @NotBlank(message = "砍价结束时间不能为空")
    private String stopTime;

    @ApiModelProperty(value = "砍价商品名称")
    private String storeName;

    @ApiModelProperty(value = "砍价金额")
    private BigDecimal price;

    @ApiModelProperty(value = "砍价商品最低价")
    private BigDecimal minPrice;

    @ApiModelProperty(value = "购买数量限制")
    @Min(value = 1, message = "购买数量限制必须大于0")
    private Integer num;

    @ApiModelProperty(value = "用户每次砍价的最大金额")
    private BigDecimal bargainMaxPrice;

    @ApiModelProperty(value = "用户每次砍价的最小金额")
    private BigDecimal bargainMinPrice;

    @ApiModelProperty(value = "帮砍次数")
    @Min(value = 1, message = "帮砍次数必须大于0")
    private Integer bargainNum;

    @ApiModelProperty(value = "砍价状态 0(到砍价时间不自动开启)  1(到砍价时间自动开启时间)")
    @NotNull(message = "砍价活动状态不能为空")
    private Boolean status;

    @ApiModelProperty(value = "反多少积分")
    private BigDecimal giveIntegral;

    @ApiModelProperty(value = "砍价活动简介")
    @NotBlank(message = "砍价活动简介不能为空")
    private String info;

    @ApiModelProperty(value = "成本价")
    private BigDecimal cost;

    @ApiModelProperty(value = "排序")
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @ApiModelProperty(value = "是否推荐0不推荐1推荐")
    private Boolean isHot;

    @ApiModelProperty(value = "是否删除 0未删除 1删除")
    private Boolean isDel;

    @ApiModelProperty(value = "添加时间")
    private Integer addTime;

    @ApiModelProperty(value = "是否包邮 0不包邮 1包邮")
    private Boolean isPostage;

    @ApiModelProperty(value = "邮费")
    private BigDecimal postage;

    @ApiModelProperty(value = "砍价规则")
    private String rule;

    @ApiModelProperty(value = "砍价商品浏览量")
    private Integer look;

    @ApiModelProperty(value = "砍价商品分享量")
    private Integer share;

    @ApiModelProperty(value = "运费模板ID")
    @NotNull(message = "运费模板ID不能为空")
    private Integer tempId;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积")
    private BigDecimal volume;

    @ApiModelProperty(value = "限购总数")
    private Integer quota;

    @ApiModelProperty(value = "限量总数显示")
    private Integer quotaShow;

    @ApiModelProperty(value = "砍价活动发起人数")
//    @Size(min = 1, max = 9999, message = "砍价人数在1-9999之间")
    @Min(value = 2, message = "砍价人数最少两人")
    @Max(value = 9999, message = "砍价人数最多9999")
    private Integer peopleNum;

    @ApiModelProperty(value = "商品属性")
    private List<StoreProductAttr> attr;

    @ApiModelProperty(value = "商品属性详情")
    private List<StoreProductAttrValueRequest> attrValue;

    @ApiModelProperty(value = "商品描述")
    private String content;

    @ApiModelProperty(value = "砍价商品名称")
    @NotBlank(message = "砍价商品名称不能为空")
    private String proName;
}
