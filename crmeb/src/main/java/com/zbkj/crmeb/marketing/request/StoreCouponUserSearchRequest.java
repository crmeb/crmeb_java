package com.zbkj.crmeb.marketing.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 优惠券发放记录表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_coupon_user")
@ApiModel(value="StoreCouponUser对象", description="优惠券领取搜索")
public class StoreCouponUserSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "优惠券所属用户")
    private Integer uid;

    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "状态（0：未使用，1：已使用, 2:已过期）")
    private Integer status;

    @ApiModelProperty(value = "最低消费多少金额可用优惠券")
    private BigDecimal minPrice;


}
