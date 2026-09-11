package com.zbkj.service.service;

import com.zbkj.common.model.finance.UserRecharge;

/**
 * 充值支付渠道退款服务
 */
public interface RechargeRefundService {

    /**
     * 原路退回充值本金
     * @param userRecharge 充值订单
     */
    void refund(UserRecharge userRecharge);
}
