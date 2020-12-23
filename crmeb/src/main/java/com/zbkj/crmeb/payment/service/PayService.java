package com.zbkj.crmeb.payment.service;

import com.zbkj.crmeb.payment.vo.wechat.CreateOrderResponseVo;

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
public abstract class PayService {
    public abstract CreateOrderResponseVo payOrder(Integer orderId, String from, String clientIp);

    public abstract boolean success(String orderId, Integer userId, String payType);
}
