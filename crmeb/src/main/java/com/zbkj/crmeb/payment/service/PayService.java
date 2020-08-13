package com.zbkj.crmeb.payment.service;

import com.zbkj.crmeb.payment.vo.wechat.CreateOrderResponseVo;

/**
 * <p>
 * 订单支付
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-10
 */
public abstract class PayService {
    public abstract CreateOrderResponseVo payOrder(Integer orderId, String fromType, String clientIp);

    public abstract boolean success(String orderId, Integer userId, String payType);
}
