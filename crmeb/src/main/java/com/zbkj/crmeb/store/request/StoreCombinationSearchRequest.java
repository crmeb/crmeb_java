package com.zbkj.crmeb.store.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author stivepeim
 * @title: StoreCombinationSearchRequest
 * @projectName crmeb
 * @description: TODO
 * @date 2020/5/2816:01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_combination")
@ApiModel(value="StoreCombination对象", description="拼团商品表")
public class StoreCombinationSearchRequest {
    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "推荐图")
    private String image;

    @ApiModelProperty(value = "轮播图")
    private String images;

    @ApiModelProperty(value = "活动标题")
    private String title;

    @ApiModelProperty(value = "活动属性")
    private String attr;

    @ApiModelProperty(value = "参团人数")
    private Integer people;

    @ApiModelProperty(value = "简介")
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
    private String addTime;

    @ApiModelProperty(value = "推荐")
    private Boolean isHost;

    @ApiModelProperty(value = "商品状态")
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
    private Integer startTime;

    @ApiModelProperty(value = "拼团结束时间")
    private Integer stopTime;

    @ApiModelProperty(value = "拼团订单有效时间")
    private Integer effectiveTime;

    @ApiModelProperty(value = "拼图商品成本")
    private Integer cost;

    @ApiModelProperty(value = "浏览量")
    private Integer browse;

    @ApiModelProperty(value = "单位名")
    private String unitName;

    @ApiModelProperty(value = "运费模板ID")
    private Integer tempId;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积")
    private BigDecimal volume;

    @ApiModelProperty(value = "单次购买数量")
    private Integer num;

    @ApiModelProperty(value = "限购总数")
    private Integer quota;

    @ApiModelProperty(value = "限量总数显示")
    private Integer quotaShow;
}
