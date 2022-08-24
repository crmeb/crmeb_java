package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券记录表
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
@ApiModel(value="StoreCouponUserOrder对象", description="下单之前可以使用的优惠券对象")
public class StoreCouponUserOrder implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "优惠券发布id")
    private Integer couponId;

    @ApiModelProperty(value = "兑换的项目id")
    private Integer cid;

    @ApiModelProperty(value = "领取人id")
    private Integer uid;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "优惠券的面值")
    private BigDecimal money;

    @ApiModelProperty(value = "最低消费多少金额可用优惠券")
    private BigDecimal minPrice;

    @ApiModelProperty(value = "获取方式")
    private String type;

    @ApiModelProperty(value = "状态（0：未使用，1：已使用, 2:已失效）")
    private int status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "开始使用时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    @JsonProperty("useStartTimeStr")
    private Date startTime;

    @ApiModelProperty(value = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    @JsonProperty("useEndTimeStr")
    private Date endTime;

    @ApiModelProperty(value = "使用时间")
    private Date useTime;

    @ApiModelProperty(value = "使用类型 1 全场通用, 2 商品券, 3 品类券")
    private Integer useType;

    @ApiModelProperty(value = "主键id 商品id/分类id", required = true)
    private String primaryKey;


}
