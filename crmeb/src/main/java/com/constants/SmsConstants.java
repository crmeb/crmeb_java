package com.constants;

/**
 * 短信配置常量类
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
public class SmsConstants {
    //------------------------------------------------短信配置------------------------------------------------
    //短信请求地址
//    public static final String SMS_API_URL = "https://sms.crmeb.net/api/";
    public static final String SMS_API_URL = "http://plat.crmeb.net/api/";
    //短信支付回调地址
    public static final String SMS_API_PAY_NOTIFY_URI = "/api/sms/pay/notify";
    //验证码接口
//    public static final String SMS_API_CAPTCHA_URI = "sms/captcha";
    public static final String SMS_API_CAPTCHA_URI = "user/code";
    //账号注册地址
//    public static final String SMS_API_REGISTER_URI = "sms/register";
    public static final String SMS_API_REGISTER_URI = "user/register";
    //公共短信模板列表
    public static final String SMS_API_PUBLIC_TEMP_URI = "sms/publictemp";
    //公共短信模板添加
    public static final String SMS_API_PUBLIC_TEMP_USE_URI = "sms/use";
    //获取账号信息
//    public static final String SMS_API_USER_INFO_URI = "sms/userinfo";
    public static final String SMS_API_USER_INFO_URI = "sms_v2/info";
    //支付套餐
//    public static final String SMS_API_PAY_TEMP_LIST_URI = "sms/meal";
    public static final String SMS_API_PAY_TEMP_LIST_URI = "meal/list";
    //支付二维码
    public static final String SMS_API_PAY_QR_CODE_URI = "meal/code";
    //支付二维码
//    public static final String SMS_API_APPLY_TEMP_MESSAGE_URI = "sms/apply";
    public static final String SMS_API_APPLY_TEMP_MESSAGE_URI = "sms_v2/apply";
    //短信模板列表
//    public static final String SMS_API_TEMP_LIST_URI = "sms/template";
    public static final String SMS_API_TEMP_LIST_URI = "sms_v2/temps";
    // 发送短信
//    public static final String SMS_API_SEND_URI = "sms/send";
    public static final String SMS_API_SEND_URI = "sms_v2/send";
    // 获取发送状态
    public static final String SMS_API_SEND_STATUS = "sms/status";
    // 用户登录地址
    public static final String SMS_API_USER_LOGIN = "user/login";
    // 短信用户开通服务
    public static final String SMS_API_USER_OPEN = "sms_v2/open";

    //接口异常错误码
    public static final Integer SMS_ERROR_CODE = 400;

    //短信发送队列key
    public static final String SMS_SEND_KEY = "sms_send_list";
    // 发送短信后状态同步key
    public static final String SMS_SEND_RESULT_KEY = "sms_send_result_list";

    // 短信模版配置开关常量
    public static final String SMS_CONFIG_VERIFICATION_CODE = "verificationCode";// 验证码
    public static final Integer SMS_CONFIG_VERIFICATION_CODE_TEMP_ID = 518076;//

    public static final String SMS_CONFIG_LOWER_ORDER_SWITCH = "lowerOrderSwitch";// 支付成功短信提醒
    public static final Integer SMS_CONFIG_LOWER_ORDER_SWITCH_TEMP_ID = 520268;// 对应的模版id

    public static final String SMS_CONFIG_DELIVER_GOODS_SWITCH = "deliverGoodsSwitch";// 发货短信提醒
//    public static final Integer SMS_CONFIG_DELIVER_GOODS_SWITCH_TEMP_ID = 520269;
    public static final Integer SMS_CONFIG_DELIVER_GOODS_SWITCH_TEMP_ID = 441596;

    public static final String SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH = "confirmTakeOverSwitch";// 确认收货短信提醒
    public static final Integer SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID = 520271;

    public static final String SMS_CONFIG_ADMIN_LOWER_ORDER_SWITCH = "adminLowerOrderSwitch";// 用户下单管理员短信提醒
    public static final Integer SMS_CONFIG_ADMIN_LOWER_ORDER_SWITCH_TEMP_ID = 520272;

    public static final String SMS_CONFIG_ADMIN_PAY_SUCCESS_SWITCH = "adminPaySuccessSwitch";// 支付成功管理员短信提醒
    public static final Integer SMS_CONFIG_ADMIN_PAY_SUCCESS_SWITCH_TEMP_ID = 520273;

    public static final String SMS_CONFIG_ADMIN_REFUND_SWITCH = "adminRefundSwitch";// 用户确认收货管理员短信提醒
    public static final Integer SMS_CONFIG_ADMIN_REFUND_SWITCH_TEMP_ID = 520422;

    public static final String SMS_CONFIG_ADMIN_CONFIRM_TAKE_OVER_SWITCH = "adminConfirmTakeOverSwitch";// 用户发起退款管理员短信提醒
    public static final Integer SMS_CONFIG_ADMIN_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID = 520274;

    public static final String SMS_CONFIG_PRICE_REVISION_SWITCH = "priceRevisionSwitch";// 改价短信提醒
//    public static final Integer SMS_CONFIG_PRICE_REVISION_SWITCH_TEMP_ID = 528288;
    public static final Integer SMS_CONFIG_PRICE_REVISION_SWITCH_TEMP_ID = 440410;

    public static final String SMS_CONFIG_ORDER_PAY_FALSE = "orderPayFalse";// 订单未支付
    public static final Integer SMS_CONFIG_ORDER_PAY_FALSE_TEMP_ID = 528116;


    //支付
    public static final String PAY_DEFAULT_PAY_TYPE = "weixin";

    //手机验证码redis key
    public static final String SMS_VALIDATE_PHONE = "sms_validate_code_";

    //短信类型
    // 短信模版配置开关常量
    public static final int SMS_CONFIG_TYPE_VERIFICATION_CODE = 1;// 验证码
    public static final int SMS_CONFIG_TYPE_LOWER_ORDER_SWITCH = 2;// 支付成功短信提醒
    public static final int SMS_CONFIG_TYPE_DELIVER_GOODS_SWITCH = 3;// 发货短信提醒
    public static final int SMS_CONFIG_TYPE_CONFIRM_TAKE_OVER_SWITCH = 4;// 确认收货短信提醒
    public static final int SMS_CONFIG_TYPE_ADMIN_LOWER_ORDER_SWITCH = 5;// 用户下单管理员短信提醒
    public static final int SMS_CONFIG_TYPE_ADMIN_PAY_SUCCESS_SWITCH = 6;// 支付成功管理员短信提醒
    public static final int SMS_CONFIG_TYPE_ADMIN_REFUND_SWITCH = 7;// 用户确认收货管理员短信提醒
    public static final int SMS_CONFIG_TYPE_ADMIN_CONFIRM_TAKE_OVER_SWITCH = 8;// 用户发起退款管理员短信提醒
    public static final int SMS_CONFIG_TYPE_PRICE_REVISION_SWITCH = 9;// 改价短信提醒
    public static final int SMS_CONFIG_TYPE_ORDER_PAY_FALSE = 10;// 订单未支付

    // 用户token的redis前缀 拼接secret
    public static final String SMS_USER_TOKEN_REDIS_PREFIX = "sms_user_token_{}";
    // 发送短信参数模板
    public static final String SMS_COMMON_PARAM_FORMAT = "param[{}]";
    // 用户token前缀
    public static final String SMS_USER_TOKEN_PREFIX = "Bearer-";
 }
