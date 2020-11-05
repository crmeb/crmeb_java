package com.zbkj.crmeb.finance.request;

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
 * 用户充值表
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
@TableName("eb_user_recharge")
@ApiModel(value="UserRecharge对象", description="用户充值表")
public class UserRechargeRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "充值用户UID")
    private Integer uid;

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal price;

    @ApiModelProperty(value = "购买赠送金额")
    private BigDecimal givePrice;

    @ApiModelProperty(value = "充值类型")
    private String rechargeType;

    @ApiModelProperty(value = "是否充值")
    private Boolean paid;

    @ApiModelProperty(value = "充值支付时间")
    private Integer payTime;

    @ApiModelProperty(value = "充值时间")
    private Integer addTime;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;


}
