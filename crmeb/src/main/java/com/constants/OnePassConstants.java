package com.constants;

/**
 * 一号通常量类
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
public class OnePassConstants {

    // 一号通token
    public static final String ONE_PASS_TOKEN_KEY_PREFIX = "one_pass_token_{}";

    // 接口异常错误码
    public static final Integer ONE_PASS_ERROR_CODE = 400;

    // 用户token前缀
    public static final String ONE_PASS_USER_TOKEN_PREFIX = "Bearer-";

    // 一号通请求地址
    public static final String ONE_PASS_API_URL = "https://sms.crmeb.net/api/";// 正式环境
//    public static final String ONE_PASS_API_URL = "http://plat.crmeb.net/api/";// 测试环境

    // 验证码地址
    public static final String REGISTER_CAPTCHA_URI = "user/code";

    // 账号注册地址
    public static final String USER_REGISTER_URI = "user/register";

    // 用户登录地址
    public static final String USER_LOGIN_URI = "user/login";

    //  获取账号信息
    public static final String USER_INFO_URI = "user/info";

    // 修改用户密码
    public static final String USER_UPDATE_PASSWORD_URI = "user/modify";

    // 修改用户手机
    public static final String USER_UPDATE_PHONE_URI = "user/modify/phone";

    // 套餐列表
    public static final String ONE_PASS_MEAL_LIST_URI = "meal/list";

    // 支付二维码
    public static final String ONE_PASS_MEAL_CODE_URI = "meal/code";

    // 开通短信服务
    public static final String ONE_PASS_SMS_OPEN_URI = "sms_v2/open";

    // 开通物流服务
    public static final String ONE_PASS_EXPRESS_OPEN_URI = "expr/open";

    // 开通产品复制服务
    public static final String ONE_PASS_COPY_OPEN_URI = "copy/open";

    // 用量记录
    public static final String ONE_PASS_USER_RECORD_URI = "user/record";

    // 修改签名
    public static final String ONE_PASS_SMS_MODIFY_URI = "sms_v2/modify";

    // 短信模板列表
    public static final String ONE_PASS_TEMP_LIST_URI = "sms_v2/temps";

    // 申请模板
    public static final String ONE_PASS_TEMP_APPLY_URI = "sms_v2/apply";

    // 申请模板记录
    public static final String ONE_PASS_APPLYS_LIST_URI = "sms_v2/applys";

    // 发送短信
    public static final String ONE_PASS_API_SEND_URI = "sms_v2/send";

    // 快递公司
    public static final String ONE_PASS_API_EXPRESS_URI = "expr/express";

    // 快递公司面单模板
    public static final String ONE_PASS_API_EXPRESS_TEMP_URI = "expr/temp";

    // 复制商品
    public static final String ONE_PASS_API_COPY_GOODS_URI = "copy/goods";

    // 电子面单
    public static final String ONE_PASS_API_EXPRESS_DUMP_URI = "expr/dump";

    // 物流追踪
    public static final String ONE_PASS_API_EXPRESS_QUEARY_URI = "expr/query";

    // 套餐类型——短信
    public static final String ONE_PASS_MEAL_TYPE_SMS = "sms";
    // 套餐类型——物流
    public static final String ONE_PASS_MEAL_TYPE_EXPR = "expr_query";
    // 套餐类型——电子面单
    public static final String ONE_PASS_MEAL_TYPE_DUMP = "expr_dump";
    // 套餐类型——产品复制
    public static final String ONE_PASS_MEAL_TYPE_COPY = "copy";

    // 物流公司缓存key
    public static final String ONE_PASS_EXPRESS_CACHE_KEY = "sync_express";
}
