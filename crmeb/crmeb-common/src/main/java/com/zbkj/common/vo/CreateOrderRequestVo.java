package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 微信统一下单对象
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
@ApiModel(value="CreateOrderRequestVo对象", description="微信统一下单对象")
public class CreateOrderRequestVo {
    @ApiModelProperty(value = "appId，公众号名称，由商户传入", required = true)
    private String appid;

    @ApiModelProperty(value = "直连商户的商户号，由微信支付生成并下发", required = true)
    private String mch_id;

    @ApiModelProperty(value = "终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传 WEB")
    private String device_info = "WEB";

    @ApiModelProperty(value = "随机字符串，不长于32位", required = true)
    private String nonce_str;

    @ApiModelProperty(value = "签名", required = true)
    private String sign;

    @ApiModelProperty(value = "签名类型，默认为MD5，支持HMAC-SHA256和MD5。")
    private String sign_type;

    @ApiModelProperty(value = "商品简单描述，该字段须严格按照规范传递", required = true)
    private String body;

    @ApiModelProperty(value = "商品简单描述")
    private String detail;

    @ApiModelProperty(value = "附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据, String(127)")
    private String attach;

    @ApiModelProperty(value = "商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号", required = true)
    private String out_trade_no;

    @ApiModelProperty(value = "符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型")
    private String fee_type = "CNY";

    @ApiModelProperty(value = "订单总金额，单位为分", required = true)
    private int total_fee;

    @ApiModelProperty(value = "必须传正确的用户端IP,支持ipv4、ipv6格式，获取方式详见获取用户ip指引", required = true)
    private String spbill_create_ip;

    @ApiModelProperty(value = "订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则")
    private String time_start;

    @ApiModelProperty(value = "订单失效时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则")
    private String time_expire;

    @ApiModelProperty(value = "商品标记，代金券或立减优惠功能的参数，说明详见代金券或立减优惠")
    private String goods_tag;

    @ApiModelProperty(value = "接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。")
    private String notify_url;

    @ApiModelProperty(value = "JSAPI -JSAPI支付（或小程序支付）， NATIVE -Native支付， APP -APP支付，MWEB--H5支付", required = true)
    private String trade_type;

    @ApiModelProperty(value = "trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义。")
    private String product_id;

    @ApiModelProperty(value = "no_credit--指定不能使用信用卡支付")
    private String limit_pay;

    @ApiModelProperty(value = "trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识。openid如何获取，可参考【获取openid】。企业号请使用【企业号OAuth2.0接口】获取企业号内成员userid，再调用【企业号userid转openid接口】进行转换")
    private String openid;

    @ApiModelProperty(value = "Y，传入Y时，支付成功消息和支付详情页将出现开票入口。需要在微信支付商户平台或微信公众平台开通电子发票功能，传此字段才可生效")
    private String receipt = "N";

    @ApiModelProperty(value = "该字段用于上报支付的场景信息,针对H5支付有以下三种场景,请根据对应场景上报,H5支付不建议在APP端使用，针对场景1，2请接入APP支付，不然可能会出现兼容性问题")
    private String scene_info;




}
