package com.zbkj.common.constants;

/**
 * 商品常量类
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public class ProductConstants {

    /** 单规格属性 */
    public static final String SINGLE_ATTR_NAME = "规格";
    /** 单规格属性值 */
    public static final String SINGLE_ATTR_VALUE = "默认";

    // 商品类型 活动类型 0=商品，1=秒杀，2=砍价，3=拼团 attrResult表用到
    /** 商品类型——普通商品 */
    public static final Integer PRODUCT_TYPE_NORMAL = 0;
    /** 商品类型——普通商品文字 */
    public static final String PRODUCT_TYPE_NORMAL_STR = "默认";
    /** 商品类型——秒杀商品 */
    public static final Integer PRODUCT_TYPE_SECKILL = 1;
    /** 商品类型——秒杀商品文字 */
    public static final String PRODUCT_TYPE_SECKILL_STR = "秒杀";
    /** 商品类型——砍价商品 */
    public static final Integer PRODUCT_TYPE_BARGAIN = 2;
    /** 商品类型——砍价商品文字 */
    public static final String PRODUCT_TYPE_BARGAIN_STR = "砍价";
    /** 商品类型——拼团商品 */
    public static final Integer PRODUCT_TYPE_PINGTUAN= 3;
    /** 商品类型——拼团商品文字 */
    public static final String PRODUCT_TYPE_PINGTUAN_STR= "拼团";
    /** 商品类型——组件商品 */
    public static final Integer PRODUCT_TYPE_COMPONENT= 4;
    /** 商品类型——组件商品文字 */
    public static final String PRODUCT_TYPE_COMPONENT_STR= "组件";
    /** 商品类型——云盘商品 */
    public static final Integer PRODUCT_TYPE_CLOUD= 5;
    /** 商品类型——卡密商品 */
    public static final Integer PRODUCT_TYPE_CDKEY= 6;


    /** 商品删除类型-回收站 */
    public static final String PRODUCT_DELETE_TYPE_RECYCLE = "recycle";
    /** 商品删除类型-删除 */
    public static final String PRODUCT_DELETE_TYPE_DELETE = "delete";

    /** 商品评论类型-所有 */
    public static final String PRODUCT_REPLY_TYPE_ALL = "all";
    /** 商品评论类型-好评 */
    public static final String PRODUCT_REPLY_TYPE_GOOD = "good";
    /** 商品评论类型-中评 */
    public static final String PRODUCT_REPLY_TYPE_MEDIUM = "medium";
    /** 商品评论类型-差评 */
    public static final String PRODUCT_REPLY_TYPE_POOR = "poor";

    /** 商品关系类型-收藏 */
    public static final String PRODUCT_RELATION_TYPE_COLLECT = "collect";
    /** 商品关系商品类型-普通商品 */
    public static final Integer PRODUCT_RELATION_CATEGORY_NORMAL = 0;
    /** 商品关系商品类型-秒杀商品 */
    public static final Integer PRODUCT_RELATION_CATEGORY_SECKILL = 1;
    /** 商品关系商品类型-砍价商品 */
    public static final Integer PRODUCT_RELATION_CATEGORY_BRANGAIN = 2;
    /** 商品关系商品类型-拼团商品 */
    public static final Integer PRODUCT_RELATION_CATEGORY_COMBINATION = 3;

    /** 商品审核状态-无需审核 */
    public static final Integer AUDIT_STATUS_EXEMPTION = 0;
    /** 商品审核状态-待审核 */
    public static final Integer AUDIT_STATUS_WAIT = 1;
    /** 商品审核状态-审核成功 */
    public static final Integer AUDIT_STATUS_SUCCESS = 2;
    /** 商品审核状态-审核拒绝 */
    public static final Integer AUDIT_STATUS_FAIL = 3;

    /** 活动边框 */
    public static final String PRODUCT_ACTIVITY_STYLE_BORDER = "activity_style_border";
    /** 活动背景 */
    public static final String PRODUCT_ACTIVITY_STYLE_BACKGROUND = "activity_style_background";
}
