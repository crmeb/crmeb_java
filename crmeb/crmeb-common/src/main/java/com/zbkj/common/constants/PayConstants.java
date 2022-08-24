package com.zbkj.common.constants;

/**
 *  支付相关常量类
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
public class PayConstants {

    //支付方式
    public static final String PAY_TYPE_WE_CHAT = "weixin"; //微信支付
    public static final String PAY_TYPE_YUE = "yue"; //余额支付
    public static final String PAY_TYPE_OFFLINE = "offline"; //线下支付
    public static final String PAY_TYPE_ALI_PAY = "alipay"; //支付宝
    public static final String PAY_TYPE_ZERO_PAY = "zeroPay"; // 零元付

    //支付渠道
    public static final String PAY_CHANNEL_WE_CHAT_H5 = "weixinh5"; //H5唤起微信支付
    public static final String PAY_CHANNEL_WE_CHAT_PUBLIC = "public"; //公众号
    public static final String PAY_CHANNEL_WE_CHAT_PROGRAM = "routine"; //小程序
    public static final String PAY_CHANNEL_WE_CHAT_APP_IOS = "weixinAppIos"; //微信App支付ios
    public static final String PAY_CHANNEL_WE_CHAT_APP_ANDROID = "weixinAppAndroid"; //微信App支付android

    public static final String PAY_CHANNEL_ALI_PAY = "alipay"; //支付宝支付
    public static final String PAY_CHANNEL_ALI_APP_PAY = "appAliPay"; //支付宝App支付

    public static final String WX_PAY_TRADE_TYPE_JS = "JSAPI";
    public static final String WX_PAY_TRADE_TYPE_H5 = "MWEB";

    //微信支付接口请求地址
    public static final String WX_PAY_API_URL = "https://api.mch.weixin.qq.com/";
    // 微信统一预下单
    public static final String WX_PAY_API_URI = "pay/unifiedorder";
    // 微信查询订单
    public static final String WX_PAY_ORDER_QUERY_API_URI = "pay/orderquery";
    // 微信支付回调地址
    public static final String WX_PAY_NOTIFY_API_URI = "/api/admin/payment/callback/wechat";
    // 微信退款回调地址
    public static final String WX_PAY_REFUND_NOTIFY_API_URI = "/api/admin/payment/callback/wechat/refund";

    public static final String WX_PAY_SIGN_TYPE_MD5 = "MD5";
    public static final String WX_PAY_SIGN_TYPE_SHA256 = "HMAC-SHA256";

    public static final String PAY_BODY = "Crmeb支付中心-订单支付";
    public static final String FIELD_SIGN = "sign";

    // 公共号退款
    public static final String WX_PAY_REFUND_API_URI= "secapi/pay/refund";
}
