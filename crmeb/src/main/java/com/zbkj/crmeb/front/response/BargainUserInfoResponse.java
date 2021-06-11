package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.bargain.model.StoreBargainUser;
import com.zbkj.crmeb.bargain.model.StoreBargainUserHelp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 砍价用户详情响应对象
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
@ApiModel(value="BargainUserInfoResponse对象", description="砍价用户详情响应对象")
public class BargainUserInfoResponse implements Serializable {

    private static final long serialVersionUID = 4177599369617161973L;

    @ApiModelProperty(value = "当前用户砍价状态：1-可以参与砍价,2-参与次数已满，3-砍价中,4-已完成，5-可以帮砍，6-已帮砍,7-帮砍次数已满，8-已生成订单未支付，9-已支付")
    private Integer bargainStatus;

    @ApiModelProperty(value = "已砍金额")
    private BigDecimal alreadyPrice;

    @ApiModelProperty(value = "剩余金额")
    private BigDecimal surplusPrice;

    @ApiModelProperty(value = "砍价百分比")
    private Integer bargainPercent;

//    @ApiModelProperty(value = "砍价活动图片")
//    private String image;
//
//    @ApiModelProperty(value = "单位名称")
//    private String unitName;
//
//    @ApiModelProperty(value = "销量")
//    private Integer sales;
//
//    @ApiModelProperty(value = "砍价开启时间")
//    private Long startTime;
//
//    @ApiModelProperty(value = "砍价结束时间")
//    private Long stopTime;
//
//    @ApiModelProperty(value = "砍价金额")
//    private BigDecimal price;
//
//    @ApiModelProperty(value = "砍价商品最低价")
//    private BigDecimal minPrice;

//    @ApiModelProperty(value = "购买数量限制")
//    // 单个活动每个用户发起砍价次数限制
//    private Integer num;

//    @ApiModelProperty(value = "砍价活动简介")
//    private String info;

//    @ApiModelProperty(value = "砍价规则")
//    private String rule;

//    @ApiModelProperty(value = "限购总数")
//    private Integer quota;
//
//    @ApiModelProperty(value = "限量总数显示")
//    private Integer quotaShow;
//
//    @ApiModelProperty(value = "商品规格sku")
//    private String sku;
//
//    @ApiModelProperty(value = "商品规格属性id")
//    private Integer attrValueId;

//    @ApiModelProperty(value = "商品详情")
//    private String content;

    @ApiModelProperty(value = "用户帮砍列表")
    private List<StoreBargainUserHelp> userHelpList;

    @ApiModelProperty(value = "用户砍价活动id")
    private Integer storeBargainUserId;

    @ApiModelProperty(value = "用户砍价活动昵称")
    private String storeBargainUserName;

    @ApiModelProperty(value = "用户砍价活动头像")
    private String storeBargainUserAvatar;
}
