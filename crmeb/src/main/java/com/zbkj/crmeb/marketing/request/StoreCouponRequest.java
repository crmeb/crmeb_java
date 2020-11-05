package com.zbkj.crmeb.marketing.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠卷表
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
@TableName("eb_store_coupon")
@ApiModel(value="StoreCouponRequest对象", description="优惠券表")
public class StoreCouponRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "优惠券名称", required = true)
    @NotBlank(message = "请填写优惠券名称")
    private String name;

    @ApiModelProperty(value = "兑换的优惠券面值", required = true)
    @NotNull(message = "请填写优惠券兑换的优惠券面值")
    private BigDecimal money;

    @ApiModelProperty(value = "是否限量, 默认0 否， 1是", required = true)
    @NotNull(message = "请设置是否限量")
    private Boolean isLimited;

    @ApiModelProperty(value = "发放总数")
    private Integer total;

    @ApiModelProperty(value = "使用类型 1 全场通用, 2 商品券, 3 品类券")
    @Range(min = 1, max = 3, message = "请选择适用商品")
    private Integer useType;

    @ApiModelProperty(value = "主键id 商品id/分类id", required = true)
    private String primaryKey;

    @ApiModelProperty(value = "最低消费，0代表不限制", required = true)
    private BigDecimal minPrice;

    @ApiModelProperty(value = "是否固定领取时间， 默认0 否， 1是", required = true)
    private Boolean isForever;

    @ApiModelProperty(value = "可领取开始时间")
    private Date receiveStartTime;

    @ApiModelProperty(value = "可领取结束时间")
    private Date receiveEndTime;

    @ApiModelProperty(value = "请设置是否固定使用时间, 默认0 否， 1是", required = true)
    @NotNull(message = "请设置是否固定使用时间")
    private Boolean isFixedTime;

    @ApiModelProperty(value = "可使用时间范围 开始时间")
    private Date useStartTime;

    @ApiModelProperty(value = "可使用时间范围 结束时间")
    private Date useEndTime;

    @ApiModelProperty(value = "天数")
    private Integer day;

    @ApiModelProperty(value = "优惠券类型 1 手动领取, 2 新人券, 3 赠送券")
    private Integer type;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态（0：关闭，1：开启）")
    private Boolean status;
}
