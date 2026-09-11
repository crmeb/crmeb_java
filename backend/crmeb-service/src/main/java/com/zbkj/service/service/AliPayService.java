package com.zbkj.service.service;

import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.request.StoreOrderRefundRequest;

/**
 * 支付宝支付 Service
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface AliPayService {


    /**
     * 查询支付结果
     * @param orderNo 订单编号
     * @return
     */
    Boolean queryPayResult(String orderNo);

    void refund(StoreOrderRefundRequest request, StoreOrder storeOrder);

    /**
     * 查询退款
     * @param orderNo 订单编号
     */
    Boolean queryRefund(String orderNo);
}
