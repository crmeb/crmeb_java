package com.zbkj.crmeb.payment.service;

/**
 * <p>
 * 订单支付
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-10
 */
public interface CallbackService {
    boolean weChat(String request) throws Exception;
    boolean aliPay(String request);
}
