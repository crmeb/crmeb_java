package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * 支付订单参数
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
@ApiModel(value="OrderPayRequest对象", description="订单支付")
public class OrderPayRequest {

    @ApiModelProperty(value = "订单id")
//    @NotNull(message = "订单id不能为空")
    private String uni;

    @ApiModelProperty(value = "订单编号")
    @NotNull(message = "订单编号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "支付类型：weixin-微信支付，yue-余额支付，offline-线下支付，alipay-支付包支付")
    @NotNull(message = "支付类型不能为空")
    private String payType;

    @ApiModelProperty(value = "支付渠道:weixinh5-微信H5支付，public-公众号支付，routine-小程序支付")
    @NotNull(message = "支付渠道不能为空")
    private String payChannel;

    @ApiModelProperty(value = "支付平台")
    private String from;
}
