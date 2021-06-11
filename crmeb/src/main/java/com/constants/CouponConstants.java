package com.constants;

/**
 * 优惠券常量类
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
public class CouponConstants {

    /**
     * ---------------------------------------
     * --------优惠券常量----------------------
     * ---------------------------------------
     */

    /** 优惠券类型—手动领取 */
    public static final Integer COUPON_TYPE_RECEIVE = 1;

    /** 优惠券类型—新人券 */
    public static final Integer COUPON_TYPE_NEW_PEOPLE = 2;

    /** 优惠券类型—赠送券 */
    public static final Integer COUPON_TYPE_GIVE_AWAY = 3;

    /** 优惠券使用类型-通用 */
    public static final Integer COUPON_USE_TYPE_COMMON = 1;

    /** 优惠券使用类型-商品 */
    public static final Integer COUPON_USE_TYPE_PRODUCT = 2;

    /** 优惠券使用类型-品类 */
    public static final Integer COUPON_USE_TYPE_CATEGORY = 3;


    /**
     * ---------------------------------------
     * --------用户优惠券常量-------------------
     * ---------------------------------------
     */

    /** 用户优惠券领取类型—用户注册 */
    public static final String STORE_COUPON_USER_TYPE_REGISTER = "new";

    /** 用户优惠券领取类型—用户领取 */
    public static final String STORE_COUPON_USER_TYPE_GET = "receive";

    /** 用户优惠券领取类型—后台发放 */
    public static final String STORE_COUPON_USER_TYPE_SEND = "send";

    /** 用户优惠券领取类型—买赠送 */
    public static final String STORE_COUPON_USER_TYPE_BUY = "buy";

    /** 用户优惠券状态—未使用 */
    public static final Integer STORE_COUPON_USER_STATUS_USABLE = 0;

    /** 用户优惠券状态—已使用 */
    public static final Integer STORE_COUPON_USER_STATUS_USED = 1;

    /** 用户优惠券状态—已失效 */
    public static final Integer STORE_COUPON_USER_STATUS_LAPSED = 2;



}
