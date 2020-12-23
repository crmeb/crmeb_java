package com.constants;

/** 微信配置
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
public class WeChatConstants {
    //------------------------------------------------公众号------------------------------------------------
    //微信接口请求地址
    public static final String API_URL = "https://api.weixin.qq.com/";
    //获取token
    public static final String API_TOKEN_URI = "cgi-bin/token?grant_type=client_credential";
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
    //JsApiTicket
    public static final String REDIS_PUBLIC_JS_API_TICKET = "wechat_js_api_ticket";
    public static final Long REDIS_PUBLIC_JS_API_TICKET_EXPRESS = 7100L;


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
}
