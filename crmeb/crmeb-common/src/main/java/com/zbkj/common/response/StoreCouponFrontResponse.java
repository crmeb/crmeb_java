package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券表
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
@ApiModel(value="StoreCouponFrontResponse对象", description="web优惠券相应对象")
public class StoreCouponFrontResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "优惠券表ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "兑换的优惠券面值")
    private BigDecimal money;

    @ApiModelProperty(value = "是否限量, 默认0 不限量， 1限量")
    private Boolean isLimited;

//    @ApiModelProperty(value = "发放总数")
//    private Integer total;

    @ApiModelProperty(value = "剩余数量")
    private Integer lastTotal;

    @ApiModelProperty(value = "所属商品id / 分类id")
    private String primaryKey;

    @ApiModelProperty(value = "最低消费，0代表不限制")
    private BigDecimal minPrice;

    @ApiModelProperty(value = "可领取开始时间")
    private Date receiveStartTime;

    @ApiModelProperty(value = "可领取结束时间")
    private Date receiveEndTime;

    @ApiModelProperty(value = "是否固定使用时间, 默认0 否， 1是")
    private Boolean isFixedTime;

//    @ApiModelProperty(value = "可使用时间范围 开始时间")
//    private Date useStartTime;
//
//    @ApiModelProperty(value = "可使用时间范围 结束时间")
//    private Date useEndTime;

    @ApiModelProperty(value = "天数")
    private Integer day;

    @ApiModelProperty(value = "优惠券类型 1 手动领取, 2 新人券, 3 赠送券")
    private Integer type;

//    @ApiModelProperty(value = "排序")
//    private Integer sort;

//    @ApiModelProperty(value = "状态（0：关闭，1：开启）")
//    private Boolean status;
//
//    @ApiModelProperty(value = "是否删除 状态（0：否，1：是）")
//    private Boolean isDel;
//
//    @ApiModelProperty(value = "创建时间")
//    private Date createTime;
//
//    @ApiModelProperty(value = "更新时间")
//    private Date updateTime;

    @ApiModelProperty(value = "是否已领取未使用")
    private Boolean isUse = false;

    @ApiModelProperty(value = "使用类型 1 全场通用, 2 商品券, 3 品类券")
    private Integer useType;

    @ApiModelProperty(value = "可使用时间范围 开始时间字符串")
    private String useStartTimeStr;

    @ApiModelProperty(value = "可使用时间范围 结束时间字符串")
    private String useEndTimeStr;
}
