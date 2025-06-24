package com.zbkj.common.constants;

/**
 * 一号通常量类
 * 202308 添加最新一号通 https://api.crmeb.com/docs/%E5%BF%AB%E9%80%9F%E5%85%A5%E9%97%A8/
 * 一号通更新逻辑
 * 1. 用户登录嵌入的一号通页面后创建或者查看应用配置信息
 * 2. 将配置信息保存到本地
 * 3. 调用login接口，获取的token在调用的业务api中使用即可，产生的记录等都会在嵌入的页面中查看
 * 4. 商家寄件功能，在订单表创建三个字段用来存储商家寄件的数据，用来区分当前订单是否商家寄件的订单(此字段前端不用展示)，测试时记得在一号通的记录中取消下，要不会有快递小哥哥上门找你的哦！
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
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
    public static final String REGISTER_CAPTCHA_URI = "v2/user/code";

    // 账号注册地址
    public static final String USER_REGISTER_URI = "user/register";

    // 用户登录地址
    public static final String USER_LOGIN_URI = "v2/user/login";

    //  获取账号信息
    public static final String USER_INFO_URI = "v2/user/info";

    // 修改用户密码
    public static final String USER_UPDATE_PASSWORD_URI = "user/modify";

    // 修改用户手机
    public static final String USER_UPDATE_PHONE_URI = "user/modify/phone";

    // 套餐列表
    public static final String ONE_PASS_MEAL_LIST_URI = "meal/list";

    // 支付二维码
    public static final String ONE_PASS_MEAL_CODE_URI = "meal/code";

    // 开通短信服务
    public static final String ONE_PASS_SMS_OPEN_URI = "v2/sms_v2/open";

    // 开通物流服务
    public static final String ONE_PASS_EXPRESS_OPEN_URI = "v2/expr/open";

    // 开通产品复制服务
    public static final String ONE_PASS_COPY_OPEN_URI = "v2/copy/open";

    // 用量记录
    public static final String ONE_PASS_USER_RECORD_URI = "v2/user/record";

    // 修改签名
    public static final String ONE_PASS_SMS_MODIFY_URI = "v2/sms_v2/modify";

    // 短信模板列表
    public static final String ONE_PASS_TEMP_LIST_URI = "v2/sms_v2/temps";

    // 申请模板
    public static final String ONE_PASS_TEMP_APPLY_URI = "v2/sms_v2/apply";

    // 申请模板记录
    public static final String ONE_PASS_APPLYS_LIST_URI = "v2/sms_v2/applys";

    // 发送短信
    public static final String ONE_PASS_API_SEND_URI = "v2/sms_v2/send";

    // 快递公司
    public static final String ONE_PASS_API_EXPRESS_URI = "v2/expr/express";

    // 快递公司面单模板
    public static final String ONE_PASS_API_EXPRESS_TEMP_URI = "v2/expr_dump/temp";

    // 电子面单模版获取
    public static final String ONE_PASS_API_EXPRESS_DUMP_RECORD_URI = "v2/expr_dump/record";

    // 复制商品
    public static final String ONE_PASS_API_COPY_GOODS_URI = "v2/copy/goods";

    // 电子面单
    public static final String ONE_PASS_API_EXPRESS_DUMP_URI = "v2/expr/dump";

    // 物流追踪
    public static final String ONE_PASS_API_EXPRESS_QUEARY_URI = "v2/expr/query";


    /** 商家寄件 START  文档链接 https://api.crmeb.com/docs/%E5%95%86%E5%AE%B6%E5%AF%84%E4%BB%B6/API/%E5%88%9B%E5%BB%BA%E5%95%86%E5%AE%B6%E5%AF%84%E4%BB%B6%E8%AE%A2%E5%8D%95.html */
    public static final String ONE_PASS_API_SHIPMENT_CREATE_ORDER_URI = "v2/shipment/create_order";
    public static final String ONE_PASS_API_SHIPMENT_CANCEL_ORDER_URI = "v2/shipment/cancel_order";
    public static final String ONE_PASS_API_SHIPMENT_INDEX_URI = "v2/shipment/index";
    public static final String ONE_PASS_API_SHIPMENT_GET_KUAIDI_COMS_URI = "v2/shipment/get_kuaidi_coms";

    // 此处为商家寄件回调地址定义，在controller中使用，不得随意更改，在创建订单时当作配置使用
    public static final String ONE_PASS_API_SHIPMENT_CALLBACK_URI = "/shipment/callback";

    /** 商家寄件 END */

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

    // 一号通当前应用access
    public static final String ONE_PASS_ACCESS_KEY = "access_key";
    // 一号通通当前应用secret
    public static final String ONE_PASS_SECRET_KEY = "secret_key";
}
