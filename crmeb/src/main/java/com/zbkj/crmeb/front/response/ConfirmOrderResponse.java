package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.marketing.response.StoreCouponUserResponse;
import com.zbkj.crmeb.store.response.StoreCartResponse;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserAddress;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * 订单确认ApiResponse对象
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
@Data
public class ConfirmOrderResponse {
    // 收否扣减
    private Boolean deduction;
    // 可用优惠券
    private StoreCouponUserResponse usableCoupon;
    // 用户地址
    private UserAddress addressInfo;
    // 购物车信息
    private List<StoreCartResponse> cartInfo;
    // 价格集合
//    private HashMap<String, Object> priceGroup;
    private PriceGroupResponse priceGroup;

    private HashMap<String, Object> other;


    private String orderKey;

    private String offlinePostage;

    private User userInfo;

    private String integralRatio;
    private String offlinePayStatus;

    // 余额支付 1 开启 2 关闭
    private String yuePayStatus;

    // 门店自提是否开启
    private String storeSelfMention;

    // 门店信息
    private String systemStore;

    // 微信支付 1 开启 0 关闭
    private String payWeixinOpen;

    // 秒杀id
    private Integer secKillId;

    // 砍价id
    private Integer bargainId;

    // 拼团id
    private Integer combinationId;

    private Integer pinkId;
}
