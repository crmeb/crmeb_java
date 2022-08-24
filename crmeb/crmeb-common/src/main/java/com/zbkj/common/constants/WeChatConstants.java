package com.zbkj.common.constants;

/** 微信配置
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
public class WeChatConstants {

    //-------------------------------------------微信系统配置------------------------------------------------------------
    /** 公众号appId key */
    public static final String WECHAT_PUBLIC_APPID = "wechat_appid";
    /** 公众号appSecret key */
    public static final String WECHAT_PUBLIC_APPSECRET = "wechat_appsecret";
    /** 小程序appId key */
    public static final String WECHAT_MINI_APPID = "routine_appid";
    // 小程序 名称
    public static final String WECHAT_MINI_NAME = "routine_name";
    /** 小程序appSecret key */
    public static final String WECHAT_MINI_APPSECRET = "routine_appsecret";





    //------------------------------------------------微信公众号------------------------------------------------
    //微信接口请求地址
    public static final String API_URL = "https://api.weixin.qq.com/";
    //获取token
    public static final String API_TOKEN_URI = "cgi-bin/token?grant_type=client_credential";
    // 微信token 过期时间，娶了一个中间值 4000 官方的7200不靠谱
    public static final Long API_TOKEN_EXPIRES = 3000L;
    //微信公众号菜单创建
    public static final String PUBLIC_API_MENU_CREATE_URI = "cgi-bin/menu/create";
    //微信公众号菜单获取
    public static final String PUBLIC_API_MENU_GET_URI = "cgi-bin/menu/get";
    //微信公众号菜单删除
    public static final String PUBLIC_API_MENU_DELETE_URI = "cgi-bin/menu/delete";
    //微信公众号，获取自定义菜单配置接口
    public static final String PUBLIC_API_MENU_SELF_SET_URI = "cgi-bin/get_current_selfmenu_info";
    //微信公众号，创建个性化菜单
    public static final String PUBLIC_API_MENU_ADD_CONDITIONAL_URI = "cgi-bin/menu/addconditional";
    //微信公众号，删除个性化菜单
    public static final String PUBLIC_API_MENU_DEL_CONDITIONAL_URI = "cgi-bin/menu/delconditional";
    //微信公众号，测试个性化菜单匹配结果
    public static final String PUBLIC_API_USER_INFO_URI = "cgi-bin/menu/trymatch";
    //获取公众号已创建的标签
    public static final String PUBLIC_API_TAG_LIST_URI = "cgi-bin/tags/get";
    //创建标签
    public static final String PUBLIC_API_TAG_CREATE_URI = "cgi-bin/tags/create";
    //编辑标签
    public static final String PUBLIC_API_TAG_UPDATE_URI = "cgi-bin/tags/update";
    //删除标签
    public static final String PUBLIC_API_TAG_DELETE_URI = "cgi-bin/tags/delete";
    //获取标签下粉丝列表
    public static final String PUBLIC_API_TAG_USER_GET_URI = "cgi-bin/user/tag/get";
    //批量为用户打标签
    public static final String PUBLIC_API_TAG_MEMBER_BATCH_URI = "cgi-bin/tags/members/batchtagging";
    //批量为用户取消标签
    public static final String PUBLIC_API_TAG_MEMBER_BATCH_UN_URI = "cgi-bin/tags/members/batchuntagging";
    //获取用户身上的标签列表
    public static final String PUBLIC_API_TAG_GET_ID_LIST_URI = "cgi-bin/tags/getidlist";
    //获取 JsApiTicket
    public static final String PUBLIC_API_JS_API_TICKET = "cgi-bin/ticket/getticket";
    //发送公众号模板消息
    public static final String PUBLIC_API_PUBLIC_TEMPLATE_MESSAGE_SEND = "cgi-bin/message/template/send";
    //获取设置的行业信息
    public static final String PUBLIC_API_TEMPLATE_MESSAGE_INDUSTRY = "cgi-bin/template/get_industry";
    //新增其他类型永久素材
    public static final String PUBLIC_API_MEDIA_UPLOAD = "cgi-bin/material/add_material";
    //获取永久素材
    public static final String PUBLIC_API_MEDIA_GET = "cgi-bin/material/get_material";
    //获取微信素材总数
    public static final String PUBLIC_API_MEDIA_COUNT = "cgi-bin/material/get_materialcount";
    //发送客服消息
    public static final String PUBLIC_API_KF_MESSAGE_SEND = "cgi-bin/message/custom/send";


    //------------------------------------------------微信小程序------------------------------------------------
    //小程序行业消息
    public static final String PUBLIC_API_PROGRAM_CATEGORY = "wxaapi/newtmpl/getcategory";
    //小程序公共模板库
    public static final String PUBLIC_API_PROGRAM_PUBLIC_TEMP = "wxaapi/newtmpl/getpubtemplatetitles";
    //小程序模板关键词列表
    public static final String PUBLIC_API_PROGRAM_PUBLIC_TEMP_KEYWORDS = "wxaapi/newtmpl/getpubtemplatekeywords";
    //添加小程序订阅消息
    public static final String PUBLIC_API_ADD_PROGRAM_TEMPLATE = "wxaapi/newtmpl/addtemplate";
    //删除小程序订阅消息
    public static final String PUBLIC_API_DELETE_PROGRAM_TEMPLATE = "wxaapi/newtmpl/deltemplate";
    //发送小程序模板消息
    public static final String PUBLIC_API_PROGRAM_TEMPLATE_MESSAGE_SEND = "cgi-bin/message/subscribe/send";

    //授权登录

    //获取临时code跳转地址
    public static final String WE_CHAT_AUTHORIZE_REDIRECT_URI_URL  = "/api/front/wechat/authorize/login";

    //获取openId
    public static final String WE_CHAT_AUTHORIZE_GET_OPEN_ID = "sns/oauth2/access_token";

    //获取小程序openId
    public static final String WE_CHAT_AUTHORIZE_PROGRAM_GET_OPEN_ID = "sns/jscode2session";

    //获取用户信息
    public static final String WE_CHAT_AUTHORIZE_GET_USER_INFO = "sns/userinfo";

    //生成二维码
    public static final String WE_CHAT_CREATE_QRCODE = "wxa/getwxacodeunlimit";

    //微信消息存储队列
    public static final String WE_CHAT_MESSAGE_SEND_KEY = "we_chat_message_send_list";

    //大家注意这里消息类型的定义，以 RESP 开头的表示返回的消息类型，以 REQ 表示微信服务器发来的消息类型
    /**
     * 返回消息类型：文本
     */
    public static final String WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_TEXT = "text";
    /**
     * 返回消息类型：音乐
     */
    public static final String WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_MUSIC = "music";
    /**
     * 返回消息类型：图文
     */
    public static final String WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_NEWS = "news";
    /**
     * 返回消息类型：图片
     */
    public static final String WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_IMAGE = "image";
    /**
     * 返回消息类型：语音
     */
    public static final String WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_VOICE = "voice";
    /**
     * 返回消息类型：视频
     */
    public static final String WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_VIDEO = "video";
    /**
     * 请求消息类型：文本
     */
    public static final String WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_TEXT = "text";
    /**
     * 请求消息类型：图片
     */
    public static final String WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_IMAGE = "image";
    /**
     * 请求消息类型：链接
     */
    public static final String WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_LINK = "link";
    /**
     * 请求消息类型：地理位置
     */
    public static final String WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_LOCATION = "location";
    /**
     * 请求消息类型：音频
     */
    public static final String WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_VOICE = "voice";
    /**
     * 请求消息类型：视频
     */
    public static final String WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_VIDEO = "video";
    /**
     * 请求消息类型：推送
     */
    public static final String WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_EVENT = "event";
    /**
     * 事件类型：subscribe(订阅)
     */
    public static final String WE_CHAT_MESSAGE_EVENT_TYPE_SUBSCRIBE = "subscribe";
    /**
     * 事件类型：unsubscribe(取消订阅)
     */
    public static final String WE_CHAT_MESSAGE_EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
    /**
     * 事件类型：CLICK(自定义菜单点击事件)
     */
    public static final String WE_CHAT_MESSAGE_EVENT_TYPE_CLICK = "click";
    /**
     * 事件类型：VIEW(自定义菜单 URl 视图)
     */
    public static final String WE_CHAT_MESSAGE_EVENT_TYPE_VIEW = "view";
    /**
     * 事件类型：LOCATION(上报地理位置事件)
     */
    public static final String WE_CHAT_MESSAGE_EVENT_TYPE_LOCATION = "LOCATION";
    /**
     * 事件类型：LOCATION(上报地理位置事件)
     */
    public static final String WE_CHAT_MESSAGE_EVENT_TYPE_SCAN = "SCAN";

    //无效关键字key
    public static final String WE_CHAT_MESSAGE_DEFAULT_CONTENT_KEY = "default";
    //Js sdk api 列表
    public static final String PUBLIC_API_JS_API_SDK_LIST = "openAddress,updateTimelineShareData,updateAppMessageShareData,onMenuShareTimeline,onMenuShareAppMessage,onMenuShareQQ,onMenuShareWeibo,onMenuShareQZone,startRecord,stopRecord,onVoiceRecordEnd,playVoice,pauseVoice,stopVoice,onVoicePlayEnd,uploadVoice,downloadVoice,chooseImage,previewImage,uploadImage,downloadImage,translateVoice,getNetworkType,openLocation,getLocation,hideOptionMenu,showOptionMenu,hideMenuItems,showMenuItems,hideAllNonBaseMenuItem,showAllNonBaseMenuItem,closeWindow,scanQRCode,chooseWXPay,openProductSpecificView,addCard,chooseCard,openCard";


    //token
    public static final String REDIS_TOKEN_KEY = "wechat_token";
    public static final String REDIS_PROGRAM_TOKEN_KEY = "wechat_program_token";
    //tag
    public static final String REDIS_TAGS_LIST_KEY = "wechat_tags_list";
    //user tag
    public static final String REDIS_TAGS_LIST_USER_KEY = "wechat_tags_user_list";
    //微信菜单
    public static final String REDIS_PUBLIC_MENU_KEY = "wechat_public_menu_key";
    //微信自定义菜单
    public static final String REDIS_PUBLIC_MENU_SELF_KEY = "wechat_public_menu_self_key";



    //授权请求地址
    public static final String WE_CHAT_AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid={$appId}&redirect_uri={$redirectUri}&response_type=code&scope=snsapi_base&state=#wechat_redirect";


    //-------------------------------------------微信支付------------------------------------------------------------
    //微信支付接口请求地址
    public static final String PAY_API_URL = "https://api.mch.weixin.qq.com/";
    public static final String PAY_API_URI = "pay/unifiedorder";
    public static final String PAY_NOTIFY_API_URI_WECHAT = "/api/admin/payment/callback/wechat";
    // 公共号退款
    public static final String PAY_REFUND_API_URI_WECHAT = "secapi/pay/refund";

    public static final String PAY_TYPE_JS = "JSAPI";
    public static final String PAY_TYPE_H5 = "MWEB";

    // --------------------------------------------------------------------------------------------------------
    // 微信部分
    // --------------------------------------------------------------------------------------------------------

    /** 获取accessToken的url */
    public static final String WECHAT_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={}&secret={}";
    /** 开放平台获取accessToken的url */
    public static final String WECHAT_OAUTH2_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={}&secret={}&code={}&grant_type=authorization_code";
    /** 开放平台获取用户的url */
    public static final String WECHAT_SNS_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token={}&openid={}&lang={}";

    /** 公众号js-sdk获取ticket的url */
    public static final String WECHAT_PUBLIC_JS_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={}&type=jsapi";
    /** 公众号发送模板消息的url */
    public static final String WECHAT_PUBLIC_SEND_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token={}";
    /** 公众号获取自定义菜单配置的url */
    public static final String WECHAT_PUBLIC_MENU_GET_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token={}";
    /** 公众号创建自定义菜单的url */
    public static final String WECHAT_PUBLIC_MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token={}";
    /** 公众号删除自定义菜单的url */
    public static final String WECHAT_PUBLIC_MENU_DELETE_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token={}";
    /** 企业号上传其他类型永久素材的url */
    public static final String WECHAT_PUBLIC_QYAPI_ADD_MATERIAL_URL = "https://qyapi.weixin.qq.com/cgi-bin/material/add_material?type={}&access_token={}";
    /** 公众号获取模板列表（自己的） */
    public static final String WECHAT_PUBLIC_GET_ALL_PRIVATE_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token={}";
    /** 公众号删除模板（自己的） */
    public static final String WECHAT_PUBLIC_DEL_PRIVATE_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/template/del_private_template?access_token={}";
    /** 公众号添加模板（自己的） */
    public static final String WECHAT_PUBLIC_API_ADD_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token={}";


    /** 小程序登录凭证校验的url */
    public static final String WECHAT_MINI_SNS_AUTH_CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid={}&secret={}&js_code={}&grant_type=authorization_code";
    /** 小程序生成小程序码的url */
    public static final String WECHAT_MINI_QRCODE_UNLIMITED_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token={}";
    /** 小程序发送订阅消息的url */
    public static final String WECHAT_MINI_SEND_SUBSCRIBE_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token={}";
    /** 小程序获取订阅列表（自己的） */
    public static final String WECHAT_MINI_GET_ALL_PRIVATE_TEMPLATE_URL = "https://api.weixin.qq.com/wxaapi/newtmpl/gettemplate?access_token={}";
    /** 小程序删除模板（自己的） */
    public static final String WECHAT_MINI_DEL_PRIVATE_TEMPLATE_URL = "https://api.weixin.qq.com/wxaapi/newtmpl/deltemplate?access_token={}";
    /** 小程序获取订阅模板（小程序的） */
    public static final String WECHAT_MINI_GET_TEMPLATE_URL = "https://api.weixin.qq.com/wxaapi/newtmpl/getpubtemplatekeywords?access_token={}&tid={}";
    /** 公众号添加模板（自己的） */
    public static final String WECHAT_MINI_API_ADD_TEMPLATE_URL = "https://api.weixin.qq.com/wxaapi/newtmpl/addtemplate?access_token={}";

    /** 小程序accessToken redis key */
    public static final String REDIS_WECAHT_MINI_ACCESS_TOKEN_KEY = "wechat_mini_accessToken";
    /** 公众号accessToken redis key */
    public static final String REDIS_WECAHT_PUBLIC_ACCESS_TOKEN_KEY = "wechat_public_accessToken";
    /** 公众号JsApiTicket redis key */
    public static final String REDIS_PUBLIC_JS_API_TICKET = "wechat_js_api_ticket";
    public static final Long REDIS_PUBLIC_JS_API_TICKET_EXPRESS = 7100L;

    /**
     * --------------------------------------------------------------------------------------------------------
     * 以下为视频号相关部分
     * --------------------------------------------------------------------------------------------------------
     */

    /*------------------------------------------ 申请接入接口 START ---------------------------------------*/

    /* 申请接入申请 */
    public static final String WECHAT_SHOP_REGISTER_APPLY = "https://api.weixin.qq.com/shop/register/apply?access_token={}";
    /* 获取接入状态 */
    public static final String WECHAT_SHOP_REGISTER_CHECK = "https://api.weixin.qq.com/shop/register/check?access_token={}";

    /*------------------------------------------ 申请接入接口 END ---------------------------------------*/


    /*------------------------------------------ 接入商品前必须接口 START ---------------------------------------*/
    /** 获取商品类目(自定义交易组件) */
    public static final String WECHAT_SHOP_CAT_GET_URL = "https://api.weixin.qq.com/shop/cat/get?access_token={}";
    /** 上传图片 **/
    public static final String WECHAT_SHOP_IMG_UPLOAD = "https://api.weixin.qq.com/shop/img/upload?&access_token={}";
    /** 上传品牌信息 */
    public static final String WECHAT_SHOP_AUDIT_AUDIT_BRAND = "https://api.weixin.qq.com/shop/audit/audit_brand?&access_token={}";
    /** 上传类目资质 */
    public static final String WECHAT_SHOP_AUDIT_AUDIT_CATEGORY = "https://api.weixin.qq.com/shop/audit/audit_category?&access_token={}";
    /** 查询类目审核结果 */
    public static final String WECHAT_SHOP_AUDIT_RESULT= "https://api.weixin.qq.com/shop/audit/result?access_token={}";
    /** 获取小程序提交过的入驻资质信息 */
    public static final String WECHAT_SHOP_AUDIT_GET_MINIAPP_CERTIFICATE= "https://api.weixin.qq.com/shop/audit/get_miniapp_certificate?access_token={}";
    /*------------------------------------------ 接入商品前必须接口 END ---------------------------------------*/


    /*------------------------------------------ 商家入驻接口 START ---------------------------------------*/
    /** 获取类目列表 */
    public static final String WECHAT_SHOP_ACCOUNT_GET_CATEGORY_LIST = "https://api.weixin.qq.com/shop/account/get_category_list?access_token={}";
    /** 获取品牌列表 */
    public static final String WECHAT_SHOP_ACCOUNT_GET_BRAND_LIST = "https://api.weixin.qq.com/shop/account/get_brand_list?access_token={}";
    /** 更新商家信息 */
    public static final String WECHAT_SHOP_ACCOUNT_UPDATE_INFO = "https://api.weixin.qq.com/shop/account/update_info?access_token={}";
    /** 获取商家信息 */
    public static final String WECHAT_SHOP_ACCOUNT_GET_INFO = "https://api.weixin.qq.com/shop/account/get_info?access_token={}";
    /*------------------------------------------ 商家入驻接口 END ---------------------------------------*/

    /*------------------------------------------ SPU 接口 START ---------------------------------------*/
//    /** 获取品牌列表(自定义交易组件) */
//    public static final String WECHAT_SHOP_BRAND_GET_URL = "https://api.weixin.qq.com/shop/account/get_brand_list?access_token={}";
    /** 添加商品(自定义交易组件) */
    public static final String WECHAT_SHOP_SPU_ADD_URL = "https://api.weixin.qq.com/shop/spu/add?access_token={}";
    /** 删除商品(自定义交易组件) */
    public static final String WECHAT_SHOP_SPU_DEL_URL = "https://api.weixin.qq.com/shop/spu/del?access_token={}";
    /** 撤回商品审核(自定义交易组件) */
    public static final String WECHAT_SHOP_SPU_DEL_AUDIT_URL = "https://api.weixin.qq.com/shop/spu/del_audit?access_token={}";
    /** 获取商品(自定义交易组件) */
    public static final String WECHAT_SHOP_SPU_GET_URL = "https://api.weixin.qq.com/shop/spu/get?access_token={}";
    /** 获取商品列表(自定义交易组件) */
    public static final String WECHAT_SHOP_SPU_GET_LIST_URL = "https://api.weixin.qq.com/shop/spu/get_list?access_token={}";
    /** 更新商品(自定义交易组件) */
    public static final String WECHAT_SHOP_SPU_UPDATE_URL = "https://api.weixin.qq.com/shop/spu/update?access_token={}";
    /** 上架商品(自定义交易组件) */
    public static final String WECHAT_SHOP_SPU_LISTING_URL = "https://api.weixin.qq.com/shop/spu/listing?access_token={}";
    /** 下架商品(自定义交易组件) */
    public static final String WECHAT_SHOP_SPU_DELISTING_URL = "https://api.weixin.qq.com/shop/spu/delisting?access_token={}";
    /** 检查场景值是否在支付校验范围内(自定义交易组件) */
    public static final String WECHAT_SHOP_SCENE_CHECK_URL = "https://api.weixin.qq.com/shop/scene/check?access_token={}";
    /*------------------------------------------ SPU 接口 END ---------------------------------------*/

    /*------------------------------------------ 订单 接口 START ---------------------------------------*/
    /** 生成订单并获取ticket(自定义交易组件) */
    public static final String WECHAT_SHOP_ORDER_ADD_URL = "https://api.weixin.qq.com/shop/order/add?access_token={}";
    /** 同步订单支付结果(自定义交易组件) */
    public static final String WECHAT_SHOP_ORDER_PAY_URL = "https://api.weixin.qq.com/shop/order/pay?access_token={}";
    /** 获取订单(自定义交易组件) */
    public static final String WECHAT_SHOP_ORDER_GET_URL = "https://api.weixin.qq.com/shop/order/get?access_token={}";
    /*------------------------------------------ 订单 接口 END ---------------------------------------*/

    /*------------------------------------------ 物流 接口 START ---------------------------------------*/
    /** 获取快递公司列表(自定义交易组件) */
    public static final String WECHAT_SHOP_DELIVERY_GET_COMPANY_LIST_URL = "https://api.weixin.qq.com/shop/delivery/get_company_list?access_token={}";
    /** 订单发货(自定义交易组件) */
    public static final String WECHAT_SHOP_DELIVERY_SEND_URL = "https://api.weixin.qq.com/shop/delivery/send?access_token={}";
    /** 订单确认收货(自定义交易组件) */
    public static final String WECHAT_SHOP_DELIVERY_RECIEVE_URL = "https://api.weixin.qq.com/shop/delivery/recieve?access_token={}";
    /*------------------------------------------ 物流 接口 END ---------------------------------------*/

    /*------------------------------------------ 售后 接口 START ---------------------------------------*/
    /** 创建售后(自定义交易组件) */
    public static final String WECHAT_SHOP_AFTERSALE_ADD_URL = "https://api.weixin.qq.com/shop/aftersale/add?access_token={}";
    /** 获取售后(自定义交易组件) */
    public static final String WECHAT_SHOP_AFTERSALE_GET_URL = "https://api.weixin.qq.com/shop/aftersale/get?access_token={}";
    /** 更新售后(自定义交易组件) */
    public static final String WECHAT_SHOP_AFTERSALE_UPDATE_URL = "https://api.weixin.qq.com/shop/aftersale/update?access_token={}";
    /*------------------------------------------ 售后 接口 END ---------------------------------------*/


    /** 自定义组件，商品类型 redis key */
    public static final String REDIS_WECHAT_SHOP_CAT_KEY = "wechat_shop_cat";

    /** 微信小程序回调，商品审核回调事件 */
    public static final String WECAHT_CALLBACK_EVENT_SPU_AUDIT = "open_product_spu_audit";
    /** 微信小程序回调，品牌审核回调事件 */
    public static final String WECAHT_CALLBACK_EVENT_BRAND_AUDIT = "open_product_brand_audit";
}
