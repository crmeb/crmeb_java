package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 拼团商品表
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
@ApiModel(value="StoreCombinationRequest对象", description="拼团商品请求对象")
public class StoreCombinationRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "拼团商品ID|新增时不填，修改时必填")
    private Integer id;

    @ApiModelProperty(value = "商品id")
    @NotNull(message = "商品编号不能为空")
    @Min(value = 1, message = "商品编号不能小于1")
    private Integer productId;

    @ApiModelProperty(value = "推荐图")
    @NotNull(message = "商品主图不能为空")
    @Length(max = 255, message = "推荐图不能超过255个字符")
    private String image;

    @ApiModelProperty(value = "轮播图")
    @NotNull(message = "轮播图不能为空")
    @Length(max = 2000, message = "轮播图不能超过2000个字符")
    private String images;

    @ApiModelProperty(value = "活动标题")
    @NotNull(message = "拼团名称不能为空")
    @Length(max = 200, message = "活动标题不能超过200个字符")
    private String title;

    @ApiModelProperty(value = "参团人数")
    @Min(value = 2, message = "拼团人数不能小于2")
    @Max(value = 99999, message = "拼团人数不能大于99999")
    private Integer people;

    @ApiModelProperty(value = "简介")
//    @NotNull(message = "拼团简介不能为空")
    private String info;

    @ApiModelProperty(value = "活动状态")
    @NotNull(message = "活动状态不能为空")
    private Boolean isShow;

    @ApiModelProperty(value = "拼团开始时间")
    @NotNull(message = "开始时间不能为空")
    private String startTime;

    @ApiModelProperty(value = "拼团结束时间")
    @NotNull(message = "结束时间不能为空")
    private String stopTime;

    @ApiModelProperty(value = "拼团订单有效时间(小时)")
    @NotNull(message = "拼团时效不能为空")
    @Min(value = 1, message = "拼团订单有效时间不能小于1")
    private Integer effectiveTime;

    @ApiModelProperty(value = "单位名")
    @NotNull(message = "单位不能为空")
    @Length(max = 32, message = "单位名不能超过32个字符")
    private String unitName;

    @ApiModelProperty(value = "运费模板ID")
    @NotNull(message = "运费模板不能为空")
    private Integer tempId;

    @ApiModelProperty(value = "购买数量")
    @Min(value = 1, message = "购买数量限制不能小于1")
    private Integer num;

    @ApiModelProperty(value = "单次购买数量")
    @Min(value = 1, message = "单次购买数量限制不能小于1")
    private Integer onceNum;

    @ApiModelProperty(value = "虚拟成团数量")
    @Min(value = 0, message = "虚拟成团数量不能小于0")
    private Integer virtualRation;

    @ApiModelProperty(value = "商品属性")
    private List<StoreProductAttrAddRequest> attr;

    @ApiModelProperty(value = "商品属性详情")
    private List<StoreProductAttrValueAddRequest> attrValue;

    @ApiModelProperty(value = "商品描述")
    private String content;

    @ApiModelProperty(value = "规格 0单 1多")
    private Boolean specType;
}
