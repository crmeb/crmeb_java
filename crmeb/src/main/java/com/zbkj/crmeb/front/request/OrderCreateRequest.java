package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.net.Inet4Address;

/**
 * 创建订单参数
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
@ApiModel(value="OrderCreateRequest对象", description="创建订单参数")
public class OrderCreateRequest {

    @ApiModelProperty(value = "真实名称")
    private String realName;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "收货地址id")
    private Integer addressId;

//    @ApiModelProperty(value = "")
//    private Integer formId;

    @ApiModelProperty(value = "优惠券编号")
    private Integer couponId;

    @ApiModelProperty(value = "支付类型:weixin-微信支付，yue-余额支付，offline-线下支付，alipay-支付包支付")
    private String payType;

    @ApiModelProperty(value = "支付渠道:weixinh5-微信H5支付，public-公众号支付，routine-小程序支付，weixinAppIos-微信appios支付，weixinAppAndroid-微信app安卓支付")
    private String payChannel;

    @ApiModelProperty(value = "是否使用积分")
    private Boolean useIntegral;

    @ApiModelProperty(value = "砍价商品id")
    private Integer bargainId;

    @ApiModelProperty(value = "拼团商品id")
    private Integer combinationId;

    @ApiModelProperty(value = "拼团团长id")
    private Integer pinkId;

    @ApiModelProperty(value = "秒杀商品id")
    private Integer seckillId;

    @ApiModelProperty(value = "订单备注")
    private String mark;

    @ApiModelProperty(value = "自提点id")
    private Integer storeId;

//    @ApiModelProperty(value = "")
//    private String from;

    @ApiModelProperty(value = "快递类型")
    private Integer shippingType;

    @ApiModelProperty(value = "是否为立即购买", required = true)
    @NotNull(message = "是否为立即购买不能为空")
    private Boolean isNew;
}
