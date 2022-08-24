package com.zbkj.common.model.bargain;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 砍价表
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
@TableName("eb_store_bargain")
@ApiModel(value="StoreBargain对象", description="砍价表")
public class StoreBargain implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "砍价商品ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "关联商品ID")
    private Integer productId;

    @ApiModelProperty(value = "砍价活动名称")
    private String title;

    @ApiModelProperty(value = "砍价活动图片")
    private String image;

    @ApiModelProperty(value = "单位名称")
    private String unitName;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "砍价商品轮播图")
    private String images;

    @ApiModelProperty(value = "砍价开启时间")
    private Long startTime;

    @ApiModelProperty(value = "砍价结束时间")
    private Long stopTime;

    @ApiModelProperty(value = "砍价商品名称")
    private String storeName;

    @ApiModelProperty(value = "砍价金额")
    private BigDecimal price;

    @ApiModelProperty(value = "砍价商品最低价")
    private BigDecimal minPrice;

    @ApiModelProperty(value = "购买数量限制")
    // 单个活动每个用户发起砍价次数限制
    private Integer num;

    @ApiModelProperty(value = "用户每次砍价的最大金额")
    private BigDecimal bargainMaxPrice;

    @ApiModelProperty(value = "用户每次砍价的最小金额")
    private BigDecimal bargainMinPrice;

    @ApiModelProperty(value = "帮砍次数")
    // 单个商品用户可以帮砍的次数，例：次数设置为1，甲和乙同时将商品A的砍价链接发给丙，丙只能帮甲或乙其中一个人砍价
    private Integer bargainNum;

    @ApiModelProperty(value = "砍价状态 0(到砍价时间不自动开启)  1(到砍价时间自动开启时间)")
    private Boolean status;

    @ApiModelProperty(value = "反多少积分")
    private Integer giveIntegral;

    @ApiModelProperty(value = "砍价活动简介")
    private String info;

    @ApiModelProperty(value = "成本价")
    private BigDecimal cost;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "是否推荐0不推荐1推荐")
    private Boolean isHot;

    @ApiModelProperty(value = "是否删除 0未删除 1删除")
    private Boolean isDel;

    @ApiModelProperty(value = "添加时间")
    private Long addTime;

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
    private Integer tempId;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积")
    private BigDecimal volume;

    @ApiModelProperty(value = "限购总数")
    private Integer quota;

    @ApiModelProperty(value = "限量总数显示")
    private Integer quotaShow;

    @ApiModelProperty(value = "帮助砍价好友人数")
    private Integer peopleNum;
}
