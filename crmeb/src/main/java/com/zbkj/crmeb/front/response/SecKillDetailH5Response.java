package com.zbkj.crmeb.front.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *  秒杀商品移动端对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreSecKillH5Response对象", description="秒杀商品移动端对象")
public class SecKillDetailH5Response implements Serializable {

    private static final long serialVersionUID = -885733985825623484L;

    @ApiModelProperty(value = "商品秒杀产品表id")
    private Integer id;

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "推荐图")
    private String image;

    @ApiModelProperty(value = "轮播图")
    private String sliderImage;

    @ApiModelProperty(value = "秒杀商品名称")
    private String storeName;

    @ApiModelProperty(value = "简介")
    private String storeInfo;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "原价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "单位名")
    private String unitName;

    @ApiModelProperty(value = "内容")
    private String description;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date stopTime;

    @ApiModelProperty(value = "最多秒杀几个")
    private Integer num;

    @ApiModelProperty(value = "时间段ID")
    private Integer timeId;

    @ApiModelProperty(value = "限购总数 随销量递减")
    private Integer quota;

    @ApiModelProperty(value = "限购总数显示 不变")
    private Integer quotaShow;

    @ApiModelProperty(value = "虚拟销量")
    private Integer ficti;

    @ApiModelProperty(value = "秒杀状态:0-关闭，1-即将开始，2-进行中，-1 - 已结束")
    private Integer seckillStatus;

    @ApiModelProperty(value = "商品详情")
    private String content;
}
