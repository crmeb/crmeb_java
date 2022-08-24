package com.zbkj.common.constants;

/**
 * 短信常量类
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
public class SmsConstants {

    /** 接口异常错误码 */
    public static final Integer SMS_ERROR_CODE = 400;

    /** 短信发送队列key */
    public static final String SMS_SEND_KEY = "sms_send_list";

    /** 手机验证码redis key */
    public static final String SMS_VALIDATE_PHONE = "sms:validate:code:";

    /**
     * ---------------------
     * 短信模版配置开关常量
     * ---------------------
     */
    /** 验证码 */
    public static final String SMS_CONFIG_VERIFICATION_CODE = "verificationCode";
//    public static final Integer SMS_CONFIG_VERIFICATION_CODE_TEMP_ID = 518076;
    /** 验证码模板ID */
    public static final Integer SMS_CONFIG_VERIFICATION_CODE_TEMP_ID = 538393;

    /** 支付成功短信提醒 */
    public static final String SMS_CONFIG_LOWER_ORDER_SWITCH = "lowerOrderSwitch";
    /** 支付成功短信提醒模版ID */
    public static final Integer SMS_CONFIG_LOWER_ORDER_SWITCH_TEMP_ID = 520268;

    /** 发货短信提醒 */
    public static final String SMS_CONFIG_DELIVER_GOODS_SWITCH = "deliverGoodsSwitch";
    /** 发货短信提醒模版ID */
    public static final Integer SMS_CONFIG_DELIVER_GOODS_SWITCH_TEMP_ID = 520269;

    /** 确认收货短信提醒 */
    public static final String SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH = "confirmTakeOverSwitch";
    /** 确认收货短信提醒模版ID */
    public static final Integer SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID = 520271;

    /** 用户下单管理员短信提醒 */
    public static final String SMS_CONFIG_ADMIN_LOWER_ORDER_SWITCH = "adminLowerOrderSwitch";
    /** 用户下单管理员短信提醒模版ID */
    public static final Integer SMS_CONFIG_ADMIN_LOWER_ORDER_SWITCH_TEMP_ID = 520272;

    /** 支付成功管理员短信提醒 */
    public static final String SMS_CONFIG_ADMIN_PAY_SUCCESS_SWITCH = "adminPaySuccessSwitch";
    /** 支付成功管理员短信提醒模版ID */
    public static final Integer SMS_CONFIG_ADMIN_PAY_SUCCESS_SWITCH_TEMP_ID = 520273;

    /** 用户确认收货管理员短信提醒 */
    public static final String SMS_CONFIG_ADMIN_REFUND_SWITCH = "adminRefundSwitch";
    /** 用户确认收货管理员短信提醒模版ID */
    public static final Integer SMS_CONFIG_ADMIN_REFUND_SWITCH_TEMP_ID = 520422;

    /** 用户发起退款管理员短信提醒 */
    public static final String SMS_CONFIG_ADMIN_CONFIRM_TAKE_OVER_SWITCH = "adminConfirmTakeOverSwitch";
    /** 用户发起退款管理员短信提醒模版ID */
    public static final Integer SMS_CONFIG_ADMIN_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID = 520274;

    /** 改价短信提醒 */
    public static final String SMS_CONFIG_PRICE_REVISION_SWITCH = "priceRevisionSwitch";
    /** 改价短信提醒模版ID */
    public static final Integer SMS_CONFIG_PRICE_REVISION_SWITCH_TEMP_ID = 528288;

    /** 订单未支付 */
    public static final String SMS_CONFIG_ORDER_PAY_FALSE = "orderPayFalse";
    /** 订单未支付模版ID */
    public static final Integer SMS_CONFIG_ORDER_PAY_FALSE_TEMP_ID = 528116;

    /**
     * 短信类型
     * 短信模版配置开关常量
     */
    /** 验证码 */
    public static final int SMS_CONFIG_TYPE_VERIFICATION_CODE = 1;
    /** 支付成功短信提醒 */
    public static final int SMS_CONFIG_TYPE_LOWER_ORDER_SWITCH = 2;
    /** 发货短信提醒 */
    public static final int SMS_CONFIG_TYPE_DELIVER_GOODS_SWITCH = 3;
    /** 确认收货短信提醒 */
    public static final int SMS_CONFIG_TYPE_CONFIRM_TAKE_OVER_SWITCH = 4;
    /** 用户下单管理员短信提醒 */
    public static final int SMS_CONFIG_TYPE_ADMIN_LOWER_ORDER_SWITCH = 5;
    /** 支付成功管理员短信提醒 */
    public static final int SMS_CONFIG_TYPE_ADMIN_PAY_SUCCESS_SWITCH = 6;
    /** 用户确认收货管理员短信提醒 */
    public static final int SMS_CONFIG_TYPE_ADMIN_REFUND_SWITCH = 7;
    /** 用户发起退款管理员短信提醒 */
    public static final int SMS_CONFIG_TYPE_ADMIN_CONFIRM_TAKE_OVER_SWITCH = 8;
    /** 改价短信提醒 */
    public static final int SMS_CONFIG_TYPE_PRICE_REVISION_SWITCH = 9;
    /** 订单未支付 */
    public static final int SMS_CONFIG_TYPE_ORDER_PAY_FALSE = 10;

    /** 发送短信参数模板 */
    public static final String SMS_COMMON_PARAM_FORMAT = "param[{}]";
}
