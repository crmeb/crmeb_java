package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 创建订单请求对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CreateOrderRequest对象", description="创建订单请求对象")
public class CreateOrderRequest implements Serializable {

    private static final long serialVersionUID = -6133994384185333872L;

    @ApiModelProperty(value = "预下单订单号")
    @NotBlank(message = "预下单订单号不能为空")
    private String preOrderNo;

    @ApiModelProperty(value = "快递类型: 1-快递配送，2-到店自提")
    @NotNull(message = "快递类型不能为空")
    @Range(min = 1, max = 2, message = "未知的快递类型")
    private Integer shippingType;

    @ApiModelProperty(value = "收货地址id")
    private Integer addressId;

    @ApiModelProperty(value = "优惠券编号")
    private Integer couponId;

    @ApiModelProperty(value = "支付类型:weixin-微信支付，yue-余额支付,alipay-支付宝支付")
    @NotBlank(message = "支付类型不能为空")
    private String payType;

    @ApiModelProperty(value = "支付渠道:weixinh5-微信H5支付，public-公众号支付，routine-小程序支付，weixinAppIos-微信appios支付，weixinAppAndroid-微信app安卓支付,alipay-支付宝支付，appAliPay-App支付宝支付")
    @NotBlank(message = "支付渠道不能为空")
    private String payChannel;

    @ApiModelProperty(value = "是否使用积分")
    @NotNull(message = "是否使用积分不能为空")
    private Boolean useIntegral;

    @ApiModelProperty(value = "订单备注")
    private String mark;

    // 以下为到店自提参数

    @ApiModelProperty(value = "自提点id")
    private Integer storeId;

    @ApiModelProperty(value = "真实名称")
    private String realName;

    @ApiModelProperty(value = "手机号码")
    private String phone;
}
