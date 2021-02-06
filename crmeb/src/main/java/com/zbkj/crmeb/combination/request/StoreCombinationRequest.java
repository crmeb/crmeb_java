package com.zbkj.crmeb.combination.request;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zbkj.crmeb.store.model.StoreProductAttr;
import com.zbkj.crmeb.store.request.StoreProductAttrValueRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 拼团商品表
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
@TableName("eb_store_combination")
@ApiModel(value="StoreCombination对象", description="拼团商品表")
public class StoreCombinationRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "拼团商品ID")
    private Integer id;

    @ApiModelProperty(value = "商品id")
    @NotNull(message = "商品编号不能为空")
    private Integer productId;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "推荐图")
    @NotNull(message = "商品主图不能为空")
    private String image;

    @ApiModelProperty(value = "轮播图")
    @NotNull(message = "轮播图不能为空")
    private String images;

    @ApiModelProperty(value = "活动标题")
    @NotNull(message = "拼团名称不能为空")
    private String title;

    @ApiModelProperty(value = "参团人数")
    @Min(value = 2, message = "拼团人数不能小于2")
    @Max(value = 99999, message = "拼团人数不能大于99999")
    private Integer people;

    @ApiModelProperty(value = "简介")
//    @NotNull(message = "拼团简介不能为空")
    private String info;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "添加时间")
    private Long addTime;

    @ApiModelProperty(value = "推荐")
    @NotNull(message = "热门推荐不能为空")
    private Boolean isHost;

    @ApiModelProperty(value = "活动状态")
    @NotNull(message = "活动状态不能为空")
    private Boolean isShow;

    private Boolean isDel;

    private Boolean combination;

    @ApiModelProperty(value = "商户是否可用1可用0不可用")
    private Boolean merUse;

    @ApiModelProperty(value = "是否包邮1是0否")
    private Boolean isPostage;

    @ApiModelProperty(value = "邮费")
    private BigDecimal postage;

    @ApiModelProperty(value = "拼团开始时间")
    @NotNull(message = "开始时间不能为空")
    private String startTime;

    @ApiModelProperty(value = "拼团结束时间")
    @NotNull(message = "结束时间不能为空")
    private String stopTime;

    @ApiModelProperty(value = "拼团订单有效时间(小时)")
    @NotNull(message = "拼团时效不能为空")
    private Integer effectiveTime;

    @ApiModelProperty(value = "拼团商品成本")
    private BigDecimal cost;

    @ApiModelProperty(value = "浏览量")
    private Integer browse;

    @ApiModelProperty(value = "单位名")
    @NotNull(message = "单位不能为空")
    private String unitName;

    @ApiModelProperty(value = "运费模板ID")
    @NotNull(message = "运费模板不能为空")
    private Integer tempId;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积")
    private BigDecimal volume;

    @ApiModelProperty(value = "购买数量")
    @Min(value = 1, message = "购买数量限制不能小于1")
    private Integer num;

    @ApiModelProperty(value = "限购总数")
    private Integer quota;

    @ApiModelProperty(value = "限量总数显示")
    private Integer quotaShow;

    @ApiModelProperty(value = "原价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "单次购买数量")
    @Min(value = 1, message = "单次购买数量限制不能小于1")
    private Integer onceNum;

    @ApiModelProperty(value = "虚拟成团数量")
    @Min(value = 0, message = "虚拟成团数量不能小于0")
    private Integer virtualRation;

    @ApiModelProperty(value = "商品属性")
    private List<StoreProductAttr> attr;

    @ApiModelProperty(value = "商品属性详情")
    private List<StoreProductAttrValueRequest> attrValue;

    @ApiModelProperty(value = "商品描述")
    private String content;

    @ApiModelProperty(value = "规格 0单 1多")
    private Boolean specType;
}
