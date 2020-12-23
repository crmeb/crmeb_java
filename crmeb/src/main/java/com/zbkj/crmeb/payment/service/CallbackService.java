package com.zbkj.crmeb.payment.service;

/**
 * 订单支付回调 service
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
public interface CallbackService {
    /**
     * 微信支付回调
     * @param xmlInfo 微信会到json
     * @return String
     */
    String weChat(String xmlInfo);

    /**
     * 支付宝支付回调
     * @param request
     * @return
     */
    boolean aliPay(String request);
}
