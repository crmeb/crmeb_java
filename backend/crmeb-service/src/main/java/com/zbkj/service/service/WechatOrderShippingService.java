package com.zbkj.service.service;

/**
 * 微信订单发货管理服务类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2023/11/27
 */
public interface WechatOrderShippingService {

    /**
     * 录入发货信息
     */
    void uploadShippingInfo(String orderNo);

    /**
     * 批量录入充值订单发货
     */
    void batchUploadRechargeOrderShipping();

    void uploadVirtualShippingInfo(String orderNo);

    void uploadVerifyShippingInfo(String orderNo);
}
