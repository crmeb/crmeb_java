package com.constants;

/**
 *  配置类
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
public class Constants {
    public static final long TOKEN_EXPRESS_MINUTES = (60 * 3); //3小时

    public static final int HTTPSTATUS_CODE_SUCCESS = 200;

    public static final int NUM_ZERO = 0;
    public static final int NUM_ONE = 1;
    public static final int NUM_TWO = 2;
    public static final int NUM_THREE = 3;
    public static final int NUM_FIVE = 5;
    public static final int NUM_SEVEN = 7;
    public static final int NUM_TEN = 10;
    public static final int NUM_ONE_HUNDRED = 100;
    //头部 token令牌key
    public static final String HEADER_AUTHORIZATION_KEY = "Authori-zation";

    //验证码redis key前缀
    public static final String VALIDATE_REDIS_KEY_PREFIX = "validate_code_";


    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_FORMAT_DATE = "yyyy-MM-dd";
    public static final String DATE_FORMAT_YEAR = "yyyy";
    public static final String DATE_FORMAT_MONTH_DATE = "MM-dd";
    public static final String DATE_FORMAT_MONTH = "yyyy-MM";
    public static final String DATE_TIME_FORMAT_NUM = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_NUM = "yyyyMMdd";
    public static final String DATE_FORMAT_START = "yyyy-MM-dd 00:00:00";
    public static final String DATE_FORMAT_END = "yyyy-MM-dd 23:59:59";
    public static final String DATE_FORMAT_MONTH_START = "yyyy-MM-01 00:00:00";
    public static final String DATE_FORMAT_YEAR_START = "yyyy-01-01 00:00:00";
    public static final String DATE_FORMAT_YEAR_END = "yyyy-12-31 23:59:59";
    public static final String DATE_FORMAT_HHMMSS = "HH:mm:ss";
    public static final String DATE_FORMAT_START_PEREND = "00:00:00";
    public static final String DATE_FORMAT_END_PEREND = "23:59:59";
    public static final String DATE_FORMAT_HHMM = "yyyy-MM-dd HH:mm";

    //后台管理员操作资金mark
    public static final String USER_BILL_OPERATE_LOG_TITLE = "{$title}{$operate}了{$value}{$founds}";

    //用户等级升级
    public static final String USER_LEVEL_OPERATE_LOG_MARK = "尊敬的用户 【{$userName}】, 在{$date}赠送会员等级成为{$levelName}会员";
    public static final String USER_LEVEL_UP_LOG_MARK = "尊敬的用户 【{$userName}】, 在{$date}您升级为为{$levelName}会员";


    //用户登录密码加密混淆字符串
    public static final String USER_LOGIN_PASSWORD_MD5_KEYWORDS = "crmeb";

    //用户登token redis存储前缀
    public static final String USER_TOKEN_REDIS_KEY_PREFIX = "TOKEN_USER:";


    //用户登录方式 h5
    public static final String USER_LOGIN_TYPE_H5 = "h5";

    //用户登录方式 公众号
    public static final String USER_LOGIN_TYPE_PUBLIC = "wechat";

    //用户登录方式 小程序
    public static final String USER_LOGIN_TYPE_PROGRAM = "routine";



    //用户默认头像
    public static final String USER_DEFAULT_AVATAR_CONFIG_KEY = "h5_avatar";

    //用户默认推广人id
    public static final Integer USER_DEFAULT_SPREAD_ID = 0;

    //默认分页
    public static final int DEFAULT_PAGE = 1;

    //默认分页
    public static final int DEFAULT_LIMIT = 20;

    //升序排序
    public static final String SORT_ASC = "asc";

    //降序排序
    public static final String SORT_DESC = "desc";

    //导出最大数值
    public static final Integer EXPORT_MAX_LIMIT = 99999;

    //商品最多选择的分类数量
    public static final Integer PRODUCT_SELECT_CATEGORY_NUM_MAX = 10;

    //上传类型
//    public static final String UPLOAD_TYPE_IMAGE = "image";
    public static final String UPLOAD_TYPE_IMAGE = "crmebimage";

    //上传类型
    public static final String UPLOAD_TYPE_FILE = "file";
    public static final String UPLOAD_ROOT_PATH_CONFIG_KEY = "upload_root_path";//上传地址
    public static final String UPLOAD_MODEL_PATH_EXCEL = "excel";// excel

    //图片上传
    public static final String UPLOAD_IMAGE_EXT_STR_CONFIG_KEY = "image_ext_str";//图片上传
    public static final String UPLOAD_IMAGE_MAX_SIZE_CONFIG_KEY = "image_max_size";

    //文件上传
    public static final String UPLOAD_FILE_EXT_STR_CONFIG_KEY = "file_ext_str";
    //最大上传文件
    public static final String UPLOAD_FILE_MAX_SIZE_CONFIG_KEY = "file_max_size";

    public static final int UPLOAD_TYPE_USER = 7; //用户上传

    // 移动端文章顶部的banner图最大数量 配置数据最小3最大10
    public static final String ARTICLE_BANNER_LIMIT = "news_slides_limit";

    //城市数据 redis key
    public static final String CITY_LIST = "city_list";
    //城市数据 tree redis key
    public static final String CITY_LIST_TREE = "city_list_tree";
    //城市数据 tree redis key
    public static final String CITY_LIST_LEVEL_1 = "city_list_level_1";

    //商品库存变动队列key
    public static final String PRODUCT_STOCK_UPDATE = "product_stock_update";
    // 商品秒杀库存队列Key
    public static final String PRODUCT_SECKILL_STOCK_UPDATE = "product_seckill_stock_update";
    // 商品砍价库存队列Key
    public static final String PRODUCT_BARGAIN_STOCK_UPDATE = "product_bargain_stock_update";
    // 商品拼团库存队列Key
    public static final String PRODUCT_COMBINATION_STOCK_UPDATE = "product_combination_stock_update";
    //商品库存redis key
    public static final String PRODUCT_STOCK_LIST = "product_stock_list";


    //sku库存变动队列key
//    public static final String SKU_STOCK_UPDATE = "product_stock_update";
    //sku库存redis key
//    public static final String SKU_STOCK_LIST = "product_stock_list";

    //消息模板队列key
    public static final String WE_CHAT_MESSAGE_KEY_PUBLIC = "we_chat_public_message_list";
    public static final String WE_CHAT_MESSAGE_KEY_PROGRAM = "we_chat_program_message_list";
    public static final String WE_CHAT_MESSAGE_INDUSTRY_KEY = "we_chat_message_industry";

    //config表数据redis
    public static final String CONFIG_LIST = "config_list"; //配置列表


    //快递信息缓存
    public static final String LOGISTICS_KEY = "logistics_";

    //config配置的key
    public static final String CONFIG_KEY_SITE_URL = "site_url"; //域名
    public static final String CONFIG_KEY_API_URL = "api_url"; //接口地址
    public static final String CONFIG_KEY_SITE_LOGO = "mobile_top_logo"; //logo地址
    public static final String CONFIG_KEY_MOBILE_LOGIN_LOGO = "mobile_login_logo"; // 移动端登录 logo
    public static final String CONFIG_KEY_SITE_NAME = "site_name"; //网站名称
    public static final String CONFIG_SITE_TENG_XUN_MAP_KEY = "tengxun_map_key"; //腾讯地图key
    public static final String CONFIG_BANK_LIST = "user_extract_bank"; //可提现银行
    public static final String CONFIG_EXTRACT_FREEZING_TIME = "extract_time"; //提现冻结时间
    public static final String CONFIG_EXTRACT_MIN_PRICE = "user_extract_min_price"; //提现最低金额
    public static final String CONFIG_RECHARGE_ATTENTION = "recharge_attention"; //充值注意事项

    public static final String CONFIG_KEY_PAY_WE_CHAT_APP_ID = "pay_weixin_appid"; //公众号appId
    public static final String CONFIG_KEY_PAY_WE_CHAT_MCH_ID = "pay_weixin_mchid"; //公众号配的商户号
    public static final String CONFIG_KEY_PAY_WE_CHAT_APP_SECRET = "pay_weixin_appsecret"; //公众号秘钥
    public static final String CONFIG_KEY_PAY_WE_CHAT_APP_KEY = "pay_weixin_key"; //公众号支付key
    public static final String CONFIG_KEY_PAY_ROUTINE_APP_ID = "pay_routine_appid"; //小程序appId
    public static final String CONFIG_KEY_PAY_ROUTINE_MCH_ID = "pay_routine_mchid"; //小程序分配的商户号
    public static final String CONFIG_KEY_PAY_ROUTINE_APP_SECRET = "pay_routine_appsecret"; //小程序秘钥
    public static final String CONFIG_KEY_PAY_ROUTINE_APP_KEY = "pay_routine_key"; //小程序支付key

    public static final String CONFIG_KEY_RECHARGE_MIN_AMOUNT = "store_user_min_recharge"; //最小充值金额
//    public static final String CONFIG_KEY_PROGRAM_LOGO = "routine_logo"; //小程序logo
//    public static final String CONFIG_KEY_PUBLIC_LOGO = "wechat_avatar"; //公众号logo

    public static final String CONFIG_KEY_LOGISTICS_APP_CODE = "system_express_app_code"; //快递查询密钥

    //云智服 小程序插件
    public static final String CONFIG_KEY_YZF_H5_URL = "yzf_h5_url"; //云智服H5 url



    //分销
    public static final String CONFIG_KEY_STORE_BROKERAGE_LEVEL = "store_brokerage_rate_num"; //返佣比例前缀
    public static final String CONFIG_KEY_STORE_BROKERAGE_RATE_ONE = "store_brokerage_ratio"; //一级返佣比例前缀
    public static final String CONFIG_KEY_STORE_BROKERAGE_RATE_TWO = "store_brokerage_two"; //二级返佣比例前缀
    public static final String CONFIG_KEY_STORE_BROKERAGE_USER_EXTRACT_MIN_PRICE = "user_extract_min_price"; //提现最低金额
    public static final String CONFIG_KEY_STORE_BROKERAGE_MODEL = "store_brokerage_status"; //分销模式1-指定分销2-人人分销
    public static final String CONFIG_KEY_STORE_BROKERAGE_USER_EXTRACT_BANK = "user_extract_bank"; //提现银行卡
    public static final String CONFIG_KEY_STORE_BROKERAGE_EXTRACT_TIME = "extract_time"; //冻结时间
    public static final String CONFIG_KEY_STORE_BROKERAGE_PERSON_PRICE = "store_brokerage_price"; //人人分销满足金额
    public static final String CONFIG_KEY_STORE_BROKERAGE_IS_OPEN = "brokerage_func_status"; //分销启用
    public static final String CONFIG_KEY_STORE_BROKERAGE_BIND_TYPE = "brokerageBindind"; //分销关系绑定0-所有游湖，2-新用户

    //积分
    public static final String CONFIG_KEY_INTEGRAL_RATE = "integral_ratio"; //积分抵用比例(1积分抵多少金额
    public static final String CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE = "order_give_integral"; //下单支付金额按比例赠送积分（实际支付1元赠送多少积分

    //分销
    public static final String CONFIG_KEY_DISTRIBUTION_TYPE = "brokerage_bindind";

    //验证码过期时间
    public static final String CONFIG_KEY_SMS_CODE_EXPIRE = "sms_code_expire";


    //config配置的formId
    public static final int CONFIG_FORM_ID_INDEX = 133; //首页配置
    public static final int CONFIG_FORM_ID_PUBLIC = 65; //公众号配置


    //第三方登录token类型
    public static final int THIRD_LOGIN_TOKEN_TYPE_PUBLIC  = 1; //公众号
    public static final int THIRD_LOGIN_TOKEN_TYPE_PROGRAM  = 2; //小程序
    public static final int THIRD_LOGIN_TOKEN_TYPE_UNION_ID  = 3; //unionid
    public static final int THIRD_ADMIN_LOGIN_TOKEN_TYPE_PUBLIC  = 4; //后台登录公众号


    // 商品类型 活动类型 0=商品，1=秒杀，2=砍价，3=拼团 attrResult表用到
    public static final Integer PRODUCT_TYPE_NORMAL = 0;
    public static final String PRODUCT_TYPE_NORMAL_STR = "默认";
    public static final Integer PRODUCT_TYPE_SECKILL = 1;
    public static final String PRODUCT_TYPE_SECKILL_STR = "秒杀";
    public static final Integer PRODUCT_TYPE_BARGAIN = 2;
    public static final String PRODUCT_TYPE_BARGAIN_STR = "砍价";
    public static final Integer PRODUCT_TYPE_PINGTUAN= 3;
    public static final String PRODUCT_TYPE_PINGTUAN_STR= "拼团";
    public static final Integer PRODUCT_TYPE_GROUP = 0;




    //group gid
    public static final Integer GROUP_DATA_ID_INDEX_BEST_BANNER = 37; //中部推荐banner图
    public static final Integer GROUP_DATA_ID_INDEX_BANNER = 48; //首页banner滚动图
    public static final Integer GROUP_DATA_ID_INDEX_RECOMMEND_BANNER = 52; //首页精品推荐Banner图片
    public static final Integer GROUP_DATA_ID_ORDER_STATUS_PIC = 53; //订单详情状态图
    public static final Integer GROUP_DATA_ID_USER_CENTER_MENU = 54; //个人中心菜单
    public static final Integer GROUP_DATA_ID_SIGN = 55; //签到配置
    public static final Integer GROUP_DATA_ID_HOT_SEARCH = 56; //热门搜索
    public static final Integer GROUP_DATA_ID_INDEX_HOT_BANNER = 57; //热门榜单推荐Banner图片
    public static final Integer GROUP_DATA_ID_INDEX_NEW_BANNER = 58; //首发新品推荐Banner图片
    public static final Integer GROUP_DATA_ID_INDEX_BENEFIT_BANNER = 59; //首页促销单品推荐Banner图片
    public static final Integer GROUP_DATA_ID_SPREAD_BANNER_LIST = 60; //推广海报图
    public static final Integer GROUP_DATA_ID_RECHARGE_LIST = 62; //充值金额设置
    public static final Integer GROUP_DATA_ID_USER_CENTER_BANNER = 65; //个人中心轮播图
    public static final Integer GROUP_DATA_ID_INDEX_MENU = 67; //导航模块
    public static final Integer GROUP_DATA_ID_INDEX_NEWS_BANNER = 68; //首页滚动新闻
    public static final Integer GROUP_DATA_ID_INDEX_ACTIVITY_BANNER = 69; //首页活动区域图片
    public static final Integer GROUP_DATA_ID_INDEX_EX_BANNER = 70; //首页超值爆款
    public static final Integer GROUP_DATA_ID_INDEX_KEYWORDS = 71; //热门搜索
    public static final Integer GROUP_DATA_ID_ADMIN_LOGIN_BANNER_IMAGE_LIST = 72; //后台登录页面轮播图



    //签到
    public static final Integer SIGN_TYPE_INTEGRAL = 1; //积分
    public static final Integer SIGN_TYPE_EXPERIENCE = 2; //经验
    public static final String SIGN_TYPE_INTEGRAL_TITLE = "签到积分奖励"; //积分
    public static final String SIGN_TYPE_EXPERIENCE_TITLE = "签到经验奖励"; //经验


    //会员搜索日期类型
    public static final String SEARCH_DATE_DAY = "today"; //今天
    public static final String SEARCH_DATE_YESTERDAY = "yesterday"; //昨天
    public static final String SEARCH_DATE_LATELY_7 = "lately7"; //最近7天
    public static final String SEARCH_DATE_LATELY_30 = "lately30"; //最近30天
    public static final String SEARCH_DATE_WEEK = "week"; //本周
    public static final String SEARCH_DATE_PRE_WEEK = "preWeek"; //上周
    public static final String SEARCH_DATE_MONTH = "month"; //本月
    public static final String SEARCH_DATE_PRE_MONTH = "preMonth"; //上月
    public static final String SEARCH_DATE_YEAR = "year"; //年
    public static final String SEARCH_DATE_PRE_YEAR = "preYear"; //上一年

    //分类服务类型  类型，1 产品分类，2 附件分类，3 文章分类， 4 设置分类， 5 菜单分类， 6 配置分类， 7 秒杀配置
    public static final int CATEGORY_TYPE_PRODUCT = 1; //产品
    public static final int CATEGORY_TYPE_ATTACHMENT = 2; //附件分类
    public static final int CATEGORY_TYPE_ARTICLE = 3; //文章分类
    public static final int CATEGORY_TYPE_SET = 4; //设置分类
    public static final int CATEGORY_TYPE_MENU = 5; //菜单分类
    public static final int CATEGORY_TYPE_CONFIG = 6; //配置分类
    public static final int CATEGORY_TYPE_SKILL = 7; //秒杀配置

    //首页Banner图片
    public static final int INDEX_RECOMMEND_BANNER = 1; //首页精品推荐Banner图片
    public static final int INDEX_HOT_BANNER = 2; //热门榜单推荐Banner图片
    public static final int INDEX_NEW_BANNER = 3; //首页首发新品推荐Banner图片
    public static final int INDEX_BENEFIT_BANNER = 4; //首页促销单品推荐Banner图片
    public static final int INDEX_LIMIT_DEFAULT = 3; //首页默认list分页条数

    public static final String INDEX_BAST_LIMIT = "bastNumber"; //精品推荐个数
    public static final String INDEX_FIRST_LIMIT = "firstNumber"; //首发新品个数
    public static final String INDEX_SALES_LIMIT = "promotionNumber"; //促销单品个数
    public static final String INDEX_HOT_LIMIT = "hotNumber"; //热门推荐个数


    //用户资金
    public static final String USER_BILL_CATEGORY_MONEY = "now_money"; //用户余额
    public static final String USER_BILL_CATEGORY_INTEGRAL = "integral"; //积分
    public static final String USER_BILL_CATEGORY_SHARE = "share"; //分享
    public static final String USER_BILL_CATEGORY_EXPERIENCE = "experience"; //经验
    public static final String USER_BILL_CATEGORY_BROKERAGE_PRICE = "brokerage_price"; //佣金金额
    public static final String USER_BILL_CATEGORY_SIGN_NUM = "sign_num"; //签到天数

    public static final String USER_BILL_TYPE_BROKERAGE = "brokerage"; //推广佣金
    public static final String USER_BILL_TYPE_DEDUCTION = "deduction"; //抵扣
    public static final String USER_BILL_TYPE_EXTRACT = "extract"; //提现
    public static final String USER_BILL_TYPE_TRANSFER_IN = "transferIn"; //佣金转入余额
    public static final String USER_BILL_TYPE_GAIN = "gain"; //购买商品赠送
    public static final String USER_BILL_TYPE_PAY_MONEY = "pay_money"; //购买
    public static final String USER_BILL_TYPE_PAY_PRODUCT = "pay_product"; //购买商品
    public static final String USER_BILL_TYPE_PAY_PRODUCT_INTEGRAL_BACK = "pay_product_integral_back"; //商品退积分
    public static final String USER_BILL_TYPE_PAY_PRODUCT_REFUND = "pay_product_refund"; //商品退款
    public static final String USER_BILL_TYPE_RECHARGE = "recharge"; //佣金转入
    public static final String USER_BILL_TYPE_PAY_RECHARGE = "pay_recharge"; //充值
    public static final String USER_BILL_TYPE_SHARE = "share"; //用户分享记录
    public static final String USER_BILL_TYPE_SIGN = "sign"; //签到
    public static final String USER_BILL_TYPE_ORDER = "order"; //订单
    public static final String USER_BILL_TYPE_PAY_ORDER = "pay_order"; //订单支付
    public static final String USER_BILL_TYPE_SYSTEM_ADD = "system_add"; //系统增加
    public static final String USER_BILL_TYPE_SYSTEM_SUB = "system_sub"; //系统减少
    public static final String USER_BILL_TYPE_PAY_MEMBER = "pay_member";// 会员支付
    public static final String USER_BILL_TYPE_OFFLINE_SCAN = "offline_scan";// 线下支付
    public static final String USER_BILL_TYPE_USER_RECHARGE_REFUND = "user_recharge_refund";// 用户充值退款

    //订单状态
    public static final String ORDER_STATUS_ALL = "all"; //所有
    public static final String ORDER_STATUS_UNPAID = "unPaid"; //未支付
    public static final String ORDER_STATUS_NOT_SHIPPED = "notShipped"; //未发货
    public static final String ORDER_STATUS_SPIKE = "spike"; //待收货
    public static final String ORDER_STATUS_BARGAIN = "bargain"; //已收货待评价
    public static final String ORDER_STATUS_COMPLETE = "complete"; //交易完成
    public static final String ORDER_STATUS_TOBE_WRITTEN_OFF = "toBeWrittenOff"; //待核销
    public static final String ORDER_STATUS_APPLY_REFUNDING = "applyRefund"; //申请退款
    public static final String ORDER_STATUS_REFUNDING = "refunding"; //退款中
    public static final String ORDER_STATUS_REFUNDED = "refunded"; //已退款
    public static final String ORDER_STATUS_DELETED = "deleted"; //已删除

    public static final String ORDER_STATUS_STR_UNPAID = "未支付"; //未支付
    public static final String ORDER_STATUS_STR_NOT_SHIPPED = "未发货"; //未发货
    public static final String ORDER_STATUS_STR_SPIKE = "待收货"; //待收货
    public static final String ORDER_STATUS_STR_BARGAIN = "待评价"; //已收货待评价
    public static final String ORDER_STATUS_STR_TAKE = "用户已收货"; //用户已收货
    public static final String ORDER_STATUS_STR_COMPLETE = "交易完成"; //交易完成
    public static final String ORDER_STATUS_STR_TOBE_WRITTEN_OFF = "待核销"; //待核销
    public static final String ORDER_STATUS_STR_APPLY_REFUNDING = "申请退款"; //申请退款
    public static final String ORDER_STATUS_STR_REFUNDING = "退款中"; //退款中
    public static final String ORDER_STATUS_STR_REFUNDED = "已退款"; //已退款
    public static final String ORDER_STATUS_STR_DELETED = "已删除"; //已删除

    // H5 端订单状态
    public static final int ORDER_STATUS_H5_UNPAID = 0; // 未支付
    public static final int ORDER_STATUS_H5_NOT_SHIPPED = 1; // 待发货
    public static final int ORDER_STATUS_H5_SPIKE = 2; // 待收货
    public static final int ORDER_STATUS_H5_JUDGE = 3; // 待评价
    public static final int ORDER_STATUS_H5_COMPLETE = 4; // 已完成
    public static final int ORDER_STATUS_H5_VERIFICATION = 5; // 待核销
    public static final int ORDER_STATUS_H5_REFUNDING = -1; // 退款中
    public static final int ORDER_STATUS_H5_REFUNDED = -2; // 已退款
    public static final int ORDER_STATUS_H5_REFUND = -3; // 退款

    public static final int ORDER_STATUS_INT_PAID = 0; //已支付
    public static final int ORDER_STATUS_INT_SPIKE = 1; //待收货
    public static final int ORDER_STATUS_INT_BARGAIN = 2; //已收货，待评价
    public static final int ORDER_STATUS_INT_COMPLETE = 3; //已完成


    //订单操作redis队列
    public static final String ORDER_TASK_REDIS_KEY_AFTER_DELETE_BY_USER = "alterOrderDeleteByUser"; // 用户删除订单后续操作
    public static final String ORDER_TASK_REDIS_KEY_AFTER_COMPLETE_BY_USER = "alterOrderCompleteByUser"; // 用户完成订单后续操作
    public static final String ORDER_TASK_REDIS_KEY_AFTER_CANCEL_BY_USER = "alterOrderCancelByUser"; // 用户取消订单后续操作
    public static final String ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER = "alterOrderRefundByUser"; // 用户订单退款后续操作
    public static final String ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER = "alterOrderTakeByUser"; // 用户订单收货后续操作
    public static final String ORDER_TASK_PAY_SUCCESS_AFTER = "orderPaySuccessTask"; // 订单支付成功后续操作

    public static final String ORDER_STATUS_CACHE_CREATE_ORDER = "cache_key_create_order";

    //支付方式
    public static final String PAY_TYPE_WE_CHAT = "weixin"; //微信支付
    public static final String PAY_TYPE_YUE = "yue"; //余额支付
    public static final String PAY_TYPE_OFFLINE = "offline"; //线下支付
    public static final String PAY_TYPE_ALI_PAY = "alipay"; //支付宝

    //支付客户端类型
    public static final String PAY_TYPE_WE_CHAT_FROM_H5 = "weixinh5"; //H5唤起微信支付
    public static final String PAY_TYPE_WE_CHAT_FROM_PUBLIC = "public"; //公众号
    public static final String PAY_TYPE_WE_CHAT_FROM_PROGRAM = "routine"; //小程序

    //后台微信登录类型
    public static final String ADMIN_LOGIN_TYPE_WE_CHAT_FROM_PUBLIC = "admin_public"; //公众号



    //支付方式
    public static final String PAY_TYPE_STR_WE_CHAT = "微信支付"; //微信支付
    public static final String PAY_TYPE_STR_YUE = "余额支付"; //余额支付
    public static final String PAY_TYPE_STR_OFFLINE = "线下支付"; //线下支付
    public static final String PAY_TYPE_STR_ALI_PAY = "支付宝"; //支付宝
    public static final String PAY_TYPE_STR_OTHER = "其他支付"; //其他支付

    //需要支付的业务类型
    public static final String SERVICE_PAY_TYPE_ORDER = "order"; //订单
    public static final String SERVICE_PAY_TYPE_RECHARGE = "recharge"; //充值



    //订单操作类型
    public static final String ORDER_STATUS_STR_SPIKE_KEY = "send"; //待收货 KEY
    public static final String ORDER_LOG_REFUND_PRICE = "refund_price"; //退款
    public static final String ORDER_LOG_EXPRESS = "express"; //快递
    public static final String ORDER_LOG_DELIVERY = "delivery"; //送货
    public static final String ORDER_LOG_DELIVERY_GOODS = "delivery_goods"; //送货
    public static final String ORDER_LOG_REFUND_REFUSE = "refund_refuse"; //不退款
    public static final String ORDER_LOG_REFUND_APPLY = "apply_refund"; //
    public static final String ORDER_LOG_PAY_SUCCESS = "pay_success"; //支付成功
    public static final String ORDER_LOG_DELIVERY_VI = "delivery_fictitious"; //虚拟发货
    public static final String ORDER_LOG_EDIT = "order_edit"; //编辑订单
    public static final String ORDER_LOG_PAY_OFFLINE = "offline"; //线下付款订单


    // 订单缓存
    public static final long ORDER_CASH_CONFIRM = (60);
    public static final String ORDER_CACHE_PER = "ORDER_CACHE:"; // redis缓存订单前缀

    //订单操作类型 -> 消息
    public static final String ORDER_LOG_MESSAGE_REFUND_PRICE = "退款给用户{amount}元"; //退款
    public static final String ORDER_LOG_MESSAGE_EXPRESS = "已发货 快递公司：{deliveryName}, 快递单号：{deliveryCode}"; //快递
    public static final String ORDER_LOG_MESSAGE_DELIVERY = "已配送 发货人：{deliveryName}, 发货人电话：{deliveryCode}"; //送货
    public static final String ORDER_LOG_MESSAGE_DELIVERY_FICTITIOUS = "已虚拟发货"; //已虚拟发货
    public static final String ORDER_LOG_MESSAGE_REFUND_REFUSE = "不退款款因：{reason}"; //不退款款因
    public static final String ORDER_LOG_MESSAGE_PAY_SUCCESS = "用户付款成功"; //用户付款成功

    public static final String ORDER_NO_PREFIX_WE_CHAT = "wx"; //微信平台下单订单号前缀
    public static final String ORDER_NO_PREFIX_H5 = "h5"; //微信平台下单订单号前缀

    //支付渠道 订单表
    public static final int ORDER_PAY_CHANNEL_PUBLIC = 0; //公众号
    public static final int ORDER_PAY_CHANNEL_PROGRAM = 1; //小程序

    //微信消息模板 tempKey
    public static final String WE_CHAT_TEMP_KEY_FIRST = "first";
    public static final String WE_CHAT_TEMP_KEY_END = "remark";
    // 公众号模板消息
    public static final String WE_CHAT_TEMP_KEY_COMBINATION_SUCCESS = "OPENTM407456411";// 拼团成功
    public static final String WE_CHAT_TEMP_KEY_BARGAIN_SUCCESS = "OPENTM410292733";// 砍价成功
    public static final String WE_CHAT_TEMP_KEY_EXPRESS = "OPENTM200565259";// 订单发货提醒
    public static final String WE_CHAT_TEMP_KEY_DELIVERY = "OPENTM207707249";// 订单配送通知
    public static final String WE_CHAT_TEMP_KEY_ORDER_PAY = "OPENTM207791277";// 订单支付成功通知
    public static final String WE_CHAT_TEMP_KEY_ORDER_RECEIVING = "OPENTM413386489";// 订单收货通知
    public static final String WE_CHAT_TEMP_KEY_ORDER_REFUND = "OPENTM207791277";// 退款进度通知
    public static final String WE_CHAT_PUBLIC_TEMP_KEY_RECHARGE = "OPENTM200565260";// 充值成功

    // 小程序服务通知
    public static final String WE_CHAT_PROGRAM_TEMP_KEY_COMBINATION_SUCCESS = "5164";
    public static final String WE_CHAT_PROGRAM_TEMP_KEY_BARGAIN_SUCCESS = "2920";
    public static final String WE_CHAT_PROGRAM_TEMP_KEY_EXPRESS = "467";
    public static final String WE_CHAT_PROGRAM_TEMP_KEY_DELIVERY = "14198";
    public static final String WE_CHAT_PROGRAM_TEMP_KEY_ORDER_PAY = "516";
    public static final String WE_CHAT_PROGRAM_TEMP_KEY_ORDER_RECEIVING = "9283";
    public static final String WE_CHAT_PROGRAM_TEMP_KEY_RECHARGE = "OPENTM200565260";



    //CND  URL测试用
    public static String CND_URL = "https://wuht-1300909283.cos.ap-chengdu.myqcloud.com";

    //物流  https://market.aliyun.com/products/56928004/cmapi021863.html#sku=yuncode15863000015
    public static String LOGISTICS_API_URL = "https://wuliu.market.alicloudapi.com/kdi";

    // 订单 基本 操作字样
    public static String RESULT_ORDER_NOTFOUND = "订单号 ${orderCode} 未找到";
    public static String RESULT_ORDER_NOTFOUND_IN_ID = "订单id ${orderId} 未找到";
    public static String RESULT_ORDER_PAYED = "订单号 ${orderCode} 已支付";
    public static String RESULT_ORDER_EDIT_PRICE_SAME = "修改价格不能和支付价格相同 原价 ${oldPrice} 修改价 ${editPrice}";
    public static String RESULT_ORDER_EDIT_PRICE_SUCCESS = "订单号 ${orderNo} 修改价格 ${price} 成功";
    public static String RESULT_ORDER_EDIT_PRICE_LOGS = "订单价格 ${orderPrice} 修改实际支付金额为 ${price} 元";

    // 订单 支付 操作字样
    public static String RESULT_ORDER_PAY_OFFLINE = "订单号 ${orderNo} 现在付款 ${price} 成功";

    // 订单核销 返回字样 Order response text info
    public static String RESULT_VERIFICATION_ORDER_NOT_FUND = "核销码 ${vCode} 的订单未找到";
    public static String RESULT_VERIFICATION_ORDER_VED = "核销码 ${vCode} 的订单已核销";
    public static String RESULT_VERIFICATION_NOTAUTH = "没有核销权限";
    public static String RESULT_VERIFICATION_USER_EXIST = "当前用户已经是核销员";


    // QRcode Response text info
    public static String RESULT_QRCODE_PRAMERROR = "生成二维码参数不合法";

    // 砍价计算比例下行
    public static String BARGAIN_TATIO_DOWN = "0.2";
    // 砍价计算比例上行
    public static String BARGAIN_TATIO_UP = "0.8";

    // 时间类型开始时间
    public static String DATE_TIME_TYPE_BEGIN = "begin";

    // 时间类型结束时间
    public static String DATE_TIME_TYPE_END = "end";

    // 商品评论类型——普通商品
    public static String STORE_REPLY_TYPE_PRODUCT = "product";
    // 商品评论类型——秒杀
    public static String STORE_REPLY_TYPE_SECKILL = "seckill";
    // 商品评论类型——拼团
    public static String STORE_REPLY_TYPE_PINTUAN = "pintuan";
    // 商品评论类型——砍价
    public static String STORE_REPLY_TYPE_BARGAIN = "bargain";

    // 商品记录Key（pv、uv）用
    public static String PRODUCT_LOG_KEY = "visit_log_key";

    public static final String FAIL     = "FAIL";
    public static final String SUCCESS  = "SUCCESS";

    // 订单取消Key
    public static final String ORDER_AUTO_CANCEL_KEY = "order_auto_cancel_key";
}
