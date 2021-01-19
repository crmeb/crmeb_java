package com.zbkj.crmeb.seckill.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.utils.DateUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 秒杀商品详情
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
public class StoreSeckillStoreInfoResponse {

    // 自定义statusName值
    // 开启 =     status = 1 && 当前时间小于startTime
    // 关闭 =     status = 0
    // 进行中 =    status = 1 && 当前时间大于startTime 小于 stopTime
    // 已结束 =    status = 1 && 当前时间大于结束时间
    // 秒杀状态【仅仅前端用】 killStatus 1=即将开始 0=关闭 2=进行中 -1=已结束
    public String getStatusName() {
        String _statusName = null;
        if(status == 1 && DateUtil.nowDateTime().compareTo(startTime) < 0){
            _statusName = "即将开始";
            this.setKillStatus(1);
        }
        else if(status == 0) {
            _statusName = "关闭";
            this.setKillStatus(0);
        }
        else if(status == 1 && DateUtil.nowDateTime().compareTo(startTime)>0
                && DateUtil.nowDateTime().compareTo(stopTime) < 0) {
            _statusName = "进行中";
            this.setKillStatus(2);
        }
        else if(status == 1 && DateUtil.nowDateTime().compareTo(stopTime) >= 0){
            _statusName = "已结束";
            this.setKillStatus(-1);
        }
        return _statusName;
    }
    @ApiModelProperty(value = "秒杀状态，前端用")
    private Integer killStatus;

    @ApiModelProperty(value = "商品id")
    private Integer id;

    @ApiModelProperty(value = "商户Id(0为总后台管理员创建,不为0的时候是商户后台创建)")
    private Integer merId;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "轮播图")
    private String images;

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
    private Integer giveIntegral;

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

    @ApiModelProperty(value = "活动显示排序1=秒杀，2=砍价，3=拼团")
    private String activity;

    private Integer[] cateIds;

    @ApiModelProperty(value = "商品描述")
    private String content;

    @ApiModelProperty(value = "收藏标识")
    private boolean userCollect = false;

    @ApiModelProperty(value = "点赞标识")
    private boolean userLike = false;

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "活动标题")
    private String title;

    @ApiModelProperty(value = "简介")
    private String info;

    @ApiModelProperty(value = "内容")
    private String description;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date stopTime;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "秒杀状态 0=关闭 1=开启")
    private Integer status;

    @ApiModelProperty(value = "秒杀状态String 未开始/进行中/活动已结束")
    private String statusName;

    @ApiModelProperty(value = "最多秒杀几个")
    private Integer num;

    @ApiModelProperty(value = "剩余限量")
    private int limitLeftNum;

    @ApiModelProperty(value = "时间段ID")
    private Integer timeId;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积")
    private BigDecimal volume;

    @ApiModelProperty(value = "限购总数")
    private Integer quota;

    @ApiModelProperty(value = "限购总数显示")
    private Integer quotaShow;

    @ApiModelProperty(value = "商品属性详情")
    private List<StoreSeckillAttrValueResponse> attrValue;

    @ApiModelProperty(value = "管理端用于映射attrResults")
    private List<HashMap<String,Object>> attrValues;

    @ApiModelProperty(value = "秒杀配置")
    private StoreSeckillManagerResponse storeSeckillManagerResponse;


    @ApiModelProperty(value = "时间戳")
    private String timeSwap;
}
