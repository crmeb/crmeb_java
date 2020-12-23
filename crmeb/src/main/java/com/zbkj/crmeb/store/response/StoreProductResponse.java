package com.zbkj.crmeb.store.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.constants.Constants;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.front.response.ProductActivityItemResponse;
import com.zbkj.crmeb.marketing.model.StoreCoupon;
import com.zbkj.crmeb.store.model.StoreProductAttr;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品表
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
@TableName("eb_store_product")
@ApiModel(value="StoreProduct对象", description="商品表")
public class StoreProductResponse implements Serializable {


    public String getActivity() {
        List<String> _activity = new ArrayList<>();
        if(StringUtils.isBlank(activity)){
            this.setActivityStr(String.join(",",_activity));
            return activity;
        }else{
            List<Integer> activityValue = CrmebUtil.stringToArrayInt(activity);
            activityValue.stream().map(e->{
                switch (e){
                    case Constants.PRODUCT_TYPE_NORMAL:
                        _activity.add(Constants.PRODUCT_TYPE_NORMAL_STR);
                        break;
                    case Constants.PRODUCT_TYPE_SECKILL:
                        _activity.add(Constants.PRODUCT_TYPE_SECKILL_STR);
                        break;
                    case Constants.PRODUCT_TYPE_BARGAIN:
                        _activity.add(Constants.PRODUCT_TYPE_BARGAIN_STR);
                        break;
                    case Constants.PRODUCT_TYPE_PINGTUAN:
                        _activity.add(Constants.PRODUCT_TYPE_PINGTUAN_STR);
                        break;
                }
                return e;
            }).collect(Collectors.toList());
        }
        this.setActivityStr(String.join(",",_activity));
        return activity;
    }

    @ApiModelProperty(value = "商品id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户Id(0为总后台管理员创建,不为0的时候是商户后台创建)")
    private Integer merId;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "轮播图")
    private String sliderImage;

    @ApiModelProperty(value = "商品名称")
    private String storeName;

    @ApiModelProperty(value = "商品简介")
    private String storeInfo;

    @ApiModelProperty(value = "关键字")
    private String keyword;

    @ApiModelProperty(value = "商品条码（一维码）")
    private String barCode;

    @ApiModelProperty(value = "分类id")
    private String cateId;

    @ApiModelProperty(value = "分类中文")
    private String cateValues;

    @ApiModelProperty(value = "商品价格")
    private BigDecimal price;

    @ApiModelProperty(value = "会员价格")
    private BigDecimal vipPrice;

    @ApiModelProperty(value = "市场价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "邮费")
    private BigDecimal postage;

    @ApiModelProperty(value = "单位名")
    private String unitName;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "状态（0：未上架，1：上架）")
    private Boolean isShow;

    @ApiModelProperty(value = "是否热卖")
    private Boolean isHot;

    @ApiModelProperty(value = "是否优惠")
    private Boolean isBenefit;

    @ApiModelProperty(value = "是否精品")
    private Boolean isBest;

    @ApiModelProperty(value = "是否新品")
    private Boolean isNew;

    @ApiModelProperty(value = "添加时间")
    private Integer addTime;

    @ApiModelProperty(value = "是否包邮")
    private Boolean isPostage;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "商户是否代理 0不可代理1可代理")
    private Boolean merUse;

    @ApiModelProperty(value = "获得积分")
    private BigDecimal giveIntegral;

    @ApiModelProperty(value = "成本价")
    private BigDecimal cost;

    @ApiModelProperty(value = "秒杀状态 0 未开启 1已开启")
    private Boolean isSeckill;

    @ApiModelProperty(value = "砍价状态 0未开启 1开启")
    private Boolean isBargain;

    @ApiModelProperty(value = "是否优品推荐")
    private Boolean isGood;

    @ApiModelProperty(value = "是否单独分佣")
    private Boolean isSub;

    @ApiModelProperty(value = "虚拟销量")
    private Integer ficti;

    @ApiModelProperty(value = "浏览量")
    private Integer browse;

    @ApiModelProperty(value = "商品二维码地址(用户小程序海报)")
    private String codePath;

    @ApiModelProperty(value = "淘宝京东1688类型")
    private String soureLink;

    @ApiModelProperty(value = "主图视频链接")
    private String videoLink;

    @ApiModelProperty(value = "运费模板ID")
    private Integer tempId;

    @ApiModelProperty(value = "规格 0单 1多")
    private Boolean specType;

    @ApiModelProperty(value = "活动显示排序 0=默认，1=秒杀，2=砍价，3=拼团")
    private String activity;

    @ApiModelProperty(value = "活动显示排序 0=默认，1=秒杀，2=砍价，3=拼团")
    private String activityStr;

    @ApiModelProperty(value = "为移动端特定参数")
    private ProductActivityItemResponse activityH5;

    @ApiModelProperty(value = "为移动端特定参数 所有参与的活动")
    private List<ProductActivityItemResponse> activityAllH5;

    @ApiModelProperty(value = "商品属性")
    private List<StoreProductAttr> attr;

    @ApiModelProperty(value = "商品属性详情")
    private List<StoreProductAttrValueResponse> attrValue;

    @ApiModelProperty(value = "管理端用于映射attrResults")
    private List<HashMap<String,Object>> attrValues;

    private Integer[] cateIds;

    @ApiModelProperty(value = "商品描述")
    private String content;

    @ApiModelProperty(value = "收藏数量")
    private Integer collectCount;

    @ApiModelProperty(value = "优惠券")
    private List<StoreCoupon> coupons;

    @ApiModelProperty(value = "优惠券Ids")
    private List<Integer> couponIds;

    // 秒杀用到
    @ApiModelProperty(value = "活动标题")
    private String title;

    @ApiModelProperty(value = "简介")
    private String info;

    @ApiModelProperty(value = "时间段ID")
    private Integer timeId;

    @ApiModelProperty(value = "最多秒杀几个")
    private Integer num;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date stopTime;

    @ApiModelProperty(value = "开始时间")
    private String startTimeStr;

    @ApiModelProperty(value = "结束时间")
    private String stopTimeStr;

    @ApiModelProperty(value = "秒杀状态 0=关闭 1=开启")
    private Integer status;

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "秒杀轮播图")
    private String images;

    @ApiModelProperty(value = "限购数量 - 销量")
    private Integer quota;

    @ApiModelProperty(value = "限购总数")
    private Integer quotaShow;

    @ApiModelProperty(value = "砍价规则")
    private String rule;

    @ApiModelProperty(value = "用户每次砍价的次数")
    private Integer bargainNum;

    @ApiModelProperty(value = "帮助砍价好友人数")
    private Integer peopleNum;

    // 拼团部分
    @ApiModelProperty(value = "推荐")
    private Boolean isHost;

    @ApiModelProperty(value = "参团人数")
    private Integer people;

    @ApiModelProperty(value = "拼团订单有效时间(小时)")
    private Integer effectiveTime;

    @ApiModelProperty(value = "单次购买数量")
    private Integer onceNum;

    @ApiModelProperty(value = "虚拟成团百分比")
    private Integer virtualRation;

    @ApiModelProperty(value = "砍价商品最低价")
    private BigDecimal minPrice;

    @ApiModelProperty(value = "砍价结束时间")
    private Long endTime;
}
