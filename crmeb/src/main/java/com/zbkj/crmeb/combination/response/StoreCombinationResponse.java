package com.zbkj.crmeb.combination.response;

import com.zbkj.crmeb.store.model.StoreProductAttr;
import com.zbkj.crmeb.store.model.StoreProductAttrValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * 拼团商品响应体
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
public class StoreCombinationResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "拼团商品ID")
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
    private Long addTime;

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
    private Long startTime;

    @ApiModelProperty(value = "拼团结束时间")
    private Long stopTime;

    @ApiModelProperty(value = "拼团结束时间")
    private String stopTimeStr;

    @ApiModelProperty(value = "拼团订单有效时间(小时)")
    private Integer effectiveTime;

    @ApiModelProperty(value = "拼图商品成本")
    private BigDecimal cost;

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

    @ApiModelProperty(value = "原价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "每个订单可购买数量")
    private Integer onceNum;

    @ApiModelProperty(value = "虚拟成团百分比")
    private Integer virtualRation;

    @ApiModelProperty(value = "拼团人数")
    private Integer countPeople;

    @ApiModelProperty(value = "参与人数")
    private Integer countPeopleAll;

    @ApiModelProperty(value = "成团数量")
    private Integer countPeoplePink;

    @ApiModelProperty(value = "限量剩余")
    private Integer remainingQuota;

    @ApiModelProperty(value = "商品规格")
//    private List<StoreProductAttr> productAttr;
    private List<HashMap<String, Object>> productAttr;

    @ApiModelProperty(value = "商品规格值")
//    private List<StoreProductAttrValue> productValue;
    private HashMap<String,Object> productValue;

    @ApiModelProperty(value = "商品单双规格")
    private Boolean specType;

    @ApiModelProperty(value = "拼团单属性AttrValueId")
    private Integer aloneAttrValueId;
}
