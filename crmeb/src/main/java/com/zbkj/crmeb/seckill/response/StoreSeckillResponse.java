package com.zbkj.crmeb.seckill.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utils.DateUtil;
import com.zbkj.crmeb.store.model.StoreProductAttr;
import com.zbkj.crmeb.store.response.StoreProductAttrValueResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 秒杀商品 response
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
public class StoreSeckillResponse {

    // 自定义statusName值
    // 开启 =     status = 1 && 当前时间小于startTime
    // 关闭 =     status = 0
    // 进行中 =    status = 1 && 当前时间大于startTime 小于 stopTime
    // 已结束 =    status = 1 && 当前时间大于结束时间
    // 秒杀状态【仅仅前端用】 killStatus 1=即将开始 0=关闭 2=进行中 -1=已结束
    public String getStatusName() {
        String _statusName = "-init";
        Date currentDateTime = DateUtil.nowDateTime();
        int l = currentDateTime.compareTo(startTime);
        int r = currentDateTime.compareTo(stopTime);
        int currentHour = DateUtil.getCurrentHour();
        System.out.println("currentHour:"+currentHour);
        int start = 0;
        int end = 0;
        if(null != currentTime){
            String[] skillManagerTime = currentTime.split(",");
            for (int i = 0; i < skillManagerTime.length; i++) {
                if(i==0) start = Integer.parseInt(skillManagerTime[i].split(":")[0]);
                if(i==1) end = Integer.parseInt(skillManagerTime[i].split(":")[0]);
            }
        }

        System.out.println("start:"+start);
        System.out.println("end:"+end);
        if(status == 0) {
            _statusName = "关闭";
            this.setKillStatus(0);
        }else if(status == 1 && l == -1){
            _statusName = "未开始";
            this.setKillStatus(1);
        }
        else if(status == 1 && r <= 0) {
            if(currentHour < start){
                _statusName = "即将开始";
                this.setKillStatus(-1);
            }else if(currentHour <= end && this.timeId == this.currentTimeId){ // 超过当前时段的已结束
                _statusName = "进行中";
                this.setKillStatus(-1);
            }
//            else if(this.timeId == this.currentTimeId){
//                _statusName = "进行中";
//                this.setKillStatus(2);
//            }
            else{
                _statusName = "已结束";
                this.setKillStatus(-1);
            }

        }// 超过当前日期的已结束
        else{
            _statusName = "已结束";
            this.setKillStatus(-1);
        }
        return _statusName;
//        return "s:"+status+"|l:"+l+"|r:"+r;
    }

    @ApiModelProperty(value = "商品秒杀产品表id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "推荐图")
    private String image;

    @ApiModelProperty(value = "轮播图")
    private List<String> images;

    @ApiModelProperty(value = "活动标题")
    private String title;

    @ApiModelProperty(value = "简介")
    private String info;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "成本")
    private BigDecimal cost;

    @ApiModelProperty(value = "原价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "返多少积分")
    private Integer giveIntegral;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "单位名")
    private String unitName;

    @ApiModelProperty(value = "邮费")
    private BigDecimal postage;

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

    @ApiModelProperty(value = "秒杀状态 原本 0=关闭 1=开启")
    private Integer status;

    @ApiModelProperty(value = "秒杀状态String 未开始/进行中/活动已结束")
    private String statusName;

    @ApiModelProperty(value = "秒杀状态，前端用")
    private Integer killStatus;

    @ApiModelProperty(value = "是否包邮")
    private Boolean isPostage;

    @ApiModelProperty(value = "删除 0未删除1已删除")
    private Boolean isDel;

    @ApiModelProperty(value = "最多秒杀几个")
    private Integer num;

    @ApiModelProperty(value = "剩余限量")
    private int limitLeftNum;

    @ApiModelProperty(value = "显示")
    private Boolean isShow;

    @ApiModelProperty(value = "时间段ID")
    private Integer timeId;

    @ApiModelProperty(value = "运费模板ID")
    private Integer tempId;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积")
    private BigDecimal volume;

    @ApiModelProperty(value = "限购总数")
    private Integer quota;

    @ApiModelProperty(value = "限购总数显示")
    private Integer quotaShow;

    @ApiModelProperty(value = "商品属性")
    private List<StoreProductAttr> attr;

    @ApiModelProperty(value = "商品属性详情")
    private List<StoreProductAttrValueResponse> attrValue;

    @ApiModelProperty(value = "管理端用于映射attrResults")
    private List<HashMap<String,Object>> attrValues;

    private Integer[] cateIds;

    @ApiModelProperty(value = "商品描述")
    private String content;

    @ApiModelProperty(value = "秒杀配置")
    private StoreSeckillManagerResponse storeSeckillManagerResponse;

    @ApiModelProperty(value = "规格 0单 1多")
    private boolean specType;

    @ApiModelProperty(value = "时间戳")
    private String timeSwap;

    @ApiModelProperty(value = "已抢百分比")
    private Integer percent;

//    @ApiModelProperty(value = "收藏数量")
//    private Integer collectCount;
//
//    @ApiModelProperty(value = "优惠券")
//    private List<StoreCoupon> coupons;
//
//    @ApiModelProperty(value = "优惠券Ids")
//    private List<Integer> couponIds;
    /////////////////////////////////////////// 自定义
//    @JsonIgnore
    private Integer currentTimeId; // 当前正在秒杀的timeId

    private String currentTime; // 当前参与的秒杀的时间段
}
