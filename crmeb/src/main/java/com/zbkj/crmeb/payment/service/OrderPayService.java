package com.zbkj.crmeb.payment.service;

import com.zbkj.crmeb.payment.vo.wechat.CreateOrderResponseVo;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserToken;

/**
 * 订单支付
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
public interface OrderPayService{
    CreateOrderResponseVo payOrder(Integer orderId, String fromType, String clientIp);

    boolean success(String orderId, Integer userId, String payType);

    void afterPaySuccess();

    /**
     * 支付成功处理
     * @param storeOrder 订单
     * @param user  用户
     * @param userToken 用户Token
     */
    Boolean paySuccess(StoreOrder storeOrder, User user, UserToken userToken);
}
