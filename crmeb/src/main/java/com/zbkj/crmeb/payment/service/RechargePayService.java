package com.zbkj.crmeb.payment.service;

import com.zbkj.crmeb.finance.model.UserRecharge;
import com.zbkj.crmeb.payment.vo.wechat.CreateOrderResponseVo;
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
public interface RechargePayService {
    CreateOrderResponseVo payOrder(Integer orderId, String payType, String clientIp);

    boolean success(String orderId, Integer userId, String payType);

    /**
     * 支付成功处理
     * @param userRecharge 充值订单
     */
    Boolean paySuccess(UserRecharge userRecharge);
}
