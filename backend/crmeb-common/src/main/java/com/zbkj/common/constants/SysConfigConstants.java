package com.zbkj.common.constants;

/**
 *  系统设置常量类
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public class SysConfigConstants {

    //后台首页登录图片
    /** 登录页LOGO */
    public static final String CONFIG_KEY_ADMIN_LOGIN_LOGO_LEFT_TOP = "site_logo_lefttop";
    public static final String CONFIG_KEY_ADMIN_LOGIN_LOGO_LOGIN = "site_logo_login";
    public static final String CONFIG_KEY_ADMIN_LOGO_SQUARE = "site_logo_square";
    public static final String CONFIG_KEY_SITE_NAME = "site_name";
    /** 登录页背景图 */
    public static final String CONFIG_KEY_ADMIN_LOGIN_BACKGROUND_IMAGE = "admin_login_bg_pic";

    /** 微信分享图片（公众号） */
    public static final String CONFIG_KEY_ADMIN_WECHAT_SHARE_IMAGE = "wechat_share_img";
    /** 微信分享标题（公众号） */
    public static final String CONFIG_KEY_ADMIN_WECHAT_SHARE_TITLE = "wechat_share_title";
    /** 微信分享简介（公众号） */
    public static final String CONFIG_KEY_ADMIN_WECHAT_SHARE_SYNOSIS = "wechat_share_synopsis";


    /** 是否启用分销 */
    public static final String CONFIG_KEY_BROKERAGE_FUNC_STATUS = "brokerage_func_status";
    /** 分销模式 :1-指定分销，2-人人分销 */
    public static final String CONFIG_KEY_STORE_BROKERAGE_STATUS = "store_brokerage_status";
    /** 分销模式 :1-指定分销 */
    public static final String STORE_BROKERAGE_STATUS_APPOINT = "1";
    /** 分销模式 :2-人人分销 */
    public static final String STORE_BROKERAGE_STATUS_PEOPLE = "2";
    /** 一级返佣比例 */
    public static final String CONFIG_KEY_STORE_BROKERAGE_RATIO = "store_brokerage_ratio";
    /** 二级返佣比例 */
    public static final String CONFIG_KEY_STORE_BROKERAGE_TWO = "store_brokerage_two";
    /** 判断是否开启气泡 */
    public static final String CONFIG_KEY_STORE_BROKERAGE_IS_BUBBLE = "store_brokerage_is_bubble";
    /** 判断是否分销消费门槛 */
    public static final String CONFIG_KEY_STORE_BROKERAGE_QUOTA = "store_brokerage_quota";
    /** 分销关系绑定:0-所有用户，1-新用户 */
    public static final String CONFIG_KEY_BROKERAGE_BINDIND = "brokerage_bindind";
    /** 分销佣金分账节点:pay:订单支付后，receipt:订单收货后，complete:订单完成后 */
    public static final String RETAIL_STORE_BROKERAGE_SHARE_NODE = "store_brokerage_share_node";



    /** 是否开启会员功能 */
    public static final String CONFIG_KEY_VIP_OPEN = "vip_open";
    /** 是否开启充值功能 */
    public static final String CONFIG_KEY_RECHARGE_SWITCH = "recharge_switch";
    /** 是否开启门店自提 */
    public static final String CONFIG_KEY_STORE_SELF_MENTION = "store_self_mention";
//    /** 腾讯地图key */
    public static final String CONFIG_SITE_TENG_XUN_MAP_KEY = "tengxun_map_key";
    /** 退款理由 */
    public static final String CONFIG_KEY_STOR_REASON = "stor_reason";
    /** 提现最低金额 */
    public static final String CONFIG_EXTRACT_MIN_PRICE = "user_extract_min_price";
    /** 提现冻结时间 */
    public static final String CONFIG_EXTRACT_FREEZING_TIME = "extract_time";
    /** 提现银行卡 */
    public static final String CONFIG_EXTRACT_BANK = "user_extract_bank";



    /** 全场满额包邮开关 */
    public static final String STORE_FEE_POSTAGE_SWITCH = "store_free_postage_switch";
    /** 全场满额包邮金额 */
    public static final String STORE_FEE_POSTAGE = "store_free_postage";
    /** 积分抵用比例(1积分抵多少金额) */
    public static final String CONFIG_KEY_INTEGRAL_RATE = "integral_ratio";
    /** 下单支付金额按比例赠送积分（实际支付1元赠送多少积分) */
    public static final String CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE = "order_give_integral";

    /** 微信支付开关 */
    public static final String CONFIG_PAY_WEIXIN_OPEN  = "pay_weixin_open";
    /** 余额支付状态 */
    public static final String CONFIG_YUE_PAY_STATUS  = "yue_pay_status";
    /** 支付宝支付状态 */
    public static final String CONFIG_ALI_PAY_STATUS = "ali_pay_status";

    /** 版权-授权标签 */
    public static final String CONFIG_COPYRIGHT_LABEL = "copyright_label";
    /** 版权-公司信息 */
    public static final String CONFIG_COPYRIGHT_COMPANY_INFO = "copyright_company_name";
    /** 版权-公司图片 */
    public static final String CONFIG_COPYRIGHT_COMPANY_IMAGE = "copyright_company_image";
    /** 版权-授权地址 */
    public static final String CONFIG_COPYRIGHT_AUTH_HOST = "authHost";
    /** 安装统计-是否已上报 */
    public static final String CONFIG_INSTALL_STATISTICS = "install_statistics";

    /** 主题测配置 */
    public static final String CONFIG_CHANGE_COLOR = "change_color_config";

    /** 电子面单-快递公司ID */
    public static final String ELECTRONIC_FACE_SHEET_EXPORT_ID = "config_export_id";
    /** 电子面单-快递公司模板id */
    public static final String ELECTRONIC_FACE_SHEET_EXPORT_TEMP_ID = "config_export_temp_id";
    /** 电子面单-快递公司编码 */
    public static final String ELECTRONIC_FACE_SHEET_EXPORT_COM = "config_export_com";
    /** 电子面单-发货人姓名 */
    public static final String ELECTRONIC_FACE_SHEET_TO_NAME = "config_export_to_name";
    /** 电子面单-发货人电话 */
    public static final String ELECTRONIC_FACE_SHEET_TO_TEL = "config_export_to_tel";
    /** 电子面单-发货人详细地址 */
    public static final String ELECTRONIC_FACE_SHEET_ADDRESS = "config_export_to_address";
    /** 电子面单-电子面单打印机编号 */
    public static final String ELECTRONIC_FACE_SHEET_PRINTER_NUM = "config_export_siid";
    /** 电子面单-开关 */
    public static final String ELECTRONIC_FACE_SHEET_OPEN = "config_export_open";


    /** 系统配置列表 */
    public static final String CONFIG_LIST = "config_list";
    /** 移动端域名 */
    public static final String CONFIG_KEY_SITE_URL = "site_url";
    /** 后台api地址(回调地址) */
    public static final String CONFIG_KEY_API_URL = "api_url";
    /** 移动商城api接口地址 */
    public static final String CONFIG_KEY_FRONT_API_URL = "front_api_url";

    /** 充值注意事项 */
    public static final String CONFIG_RECHARGE_ATTENTION = "recharge_attention";
    /********************************************************************************************* 协议START ****************/
    /** 商户入驻协议 */
    public static final String MERCHANT_SETTLEMENT_AGREEMENT = "merSettlementAgreement";
    /** 用户隐私政策 */
    public static final String USER_PRIVACY_AGREEMENT = "userPrivacyAgreement";
    /** 用户注册协议 */
    public static final String USER_REGISTER_AGREEMENT = "merLoginAgreement";
    /** 用户注销声明 */
    public static final String USER_CANCEL_AGREEMENT = "userCancelAccountAgreement";
    /** 用户注销重要提示 */
    public static final String USER_CANCEL_NOTICE_AGREEMENT = "userCancelAccountNoticeAgreement";
    /** 关于我们 */
    public static final String ABOUTUS_AGREEMENT = "userAboutAgreement";
    /** 资质证明 */
    public static final String PLATFROM_INTELLIGENT_AGREEMENT = "platfromIntelligentAgreement";
    /** 优惠券规则 */
    public static final String COUPON_AGREEMENT = "couponAgreement";

    /********************************************************************************************* 协议END ****************/


    /** 图片上传类型 1本地 2七牛云 3OSS 4COS 5京东, 默认本地 */
    public static final String CONFIG_UPLOAD_TYPE = "uploadType";
    /** 文件上传是否保存本地 */
    public static final String CONFIG_FILE_IS_SAVE = "file_is_save";
    /** 全局本地图片域名 */
    public static final String CONFIG_LOCAL_UPLOAD_URL = "localUploadUrl";
    /** 图片上传,拓展名 */
    public static final String UPLOAD_IMAGE_EXT_STR_CONFIG_KEY = "image_ext_str";
    /** 图片上传,最大尺寸 */
    public static final String UPLOAD_IMAGE_MAX_SIZE_CONFIG_KEY = "image_max_size";
    /** 文件上传,拓展名 */
    public static final String UPLOAD_FILE_EXT_STR_CONFIG_KEY = "file_ext_str";
    /** 文件上传,最大尺寸 */
    public static final String UPLOAD_FILE_MAX_SIZE_CONFIG_KEY = "file_max_size";

    /** 七牛云上传URL */
    public static final String CONFIG_QN_UPLOAD_URL = "qnUploadUrl";
    /** 七牛云Access Key */
    public static final String CONFIG_QN_ACCESS_KEY = "qnAccessKey";
    /** 七牛云Secret Key */
    public static final String CONFIG_QN_SECRET_KEY = "qnSecretKey";
    /** 七牛云存储名称 */
    public static final String CONFIG_QN_STORAGE_NAME = "qnStorageName";
    /** 七牛云存储区域 */
    public static final String CONFIG_QN_STORAGE_REGION = "qnStorageRegion";

    /** 阿里云上传URL */
    public static final String CONFIG_AL_UPLOAD_URL = "alUploadUrl";
    /** 阿里云Access Key */
    public static final String CONFIG_AL_ACCESS_KEY = "alAccessKey";
    /** 阿里云Secret Key */
    public static final String CONFIG_AL_SECRET_KEY = "alSecretKey";
    /** 阿里云存储名称 */
    public static final String CONFIG_AL_STORAGE_NAME = "alStorageName";
    /** 阿里云存储区域 */
    public static final String CONFIG_AL_STORAGE_REGION = "alStorageRegion";

    /** 腾讯云上传URL */
    public static final String CONFIG_TX_UPLOAD_URL = "txUploadUrl";
    /** 腾讯云Access Key */
    public static final String CONFIG_TX_ACCESS_KEY = "txAccessKey";
    /** 腾讯云Secret Key */
    public static final String CONFIG_TX_SECRET_KEY = "txSecretKey";
    /** 腾讯云存储名称 */
    public static final String CONFIG_TX_STORAGE_NAME = "txStorageName";
    /** 腾讯云存储区域 */
    public static final String CONFIG_TX_STORAGE_REGION = "txStorageRegion";

    /** 京东云上传URL */
    public static final String CONFIG_JD_UPLOAD_URL = "jdUploadUrl";
    /** 京东云Access Key */
    public static final String CONFIG_JD_ACCESS_KEY = "jdAccessKey";
    /** 京东云Secret Key */
    public static final String CONFIG_JD_SECRET_KEY = "jdSecretKey";
    /** 京东云存储桶名称 */
    public static final String CONFIG_JD_BUCKET_NAME = "jdBucketName";
    /** 京东云存储区域 */
    public static final String CONFIG_JD_CLOUD_SIGNING_REGION = "jdSigningRegion";
    /** 京东云存储端点 */
    public static final String CONFIG_JD_CLOUD_ENDPOINT = "jdEndpoint";

    /** 开屏广告-开关 */
    public static final String SPLASH_AD_SWITCH = "splash_ad_switch";
    /** 开屏广告-广告时间 */
    public static final String SPLASH_AD_SHOW_TIME = "splash_ad_show_time";
    /** 开屏广告-展示间隔 */
    public static final String SPLASH_AD_SHOW_INTERVAL = "splash_ad_show_interval";



    /** 默认好评开关 '0'-关闭、'1'-打开*/
    public static final String CONFIG_AUTO_REPLY_SWITCH = "auto_reply_switch";
    /** 默认好评时间 */
    public static final String CONFIG_AUTO_REPLY_TIME = "auto_reply_time";
    /** 默认好评内容 */
    public static final String CONFIG_AUTO_REPLY_CONTENT = "auto_reply_content";





}
