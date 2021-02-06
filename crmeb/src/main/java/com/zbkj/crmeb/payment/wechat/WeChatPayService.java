package com.zbkj.crmeb.payment.wechat;

import com.zbkj.crmeb.finance.model.UserRecharge;
import com.zbkj.crmeb.payment.vo.wechat.CreateOrderResponseVo;
import com.zbkj.crmeb.payment.vo.wechat.PayParamsVo;
import com.zbkj.crmeb.store.model.StoreOrder;

import java.util.Map;

/**
 * 微信支付
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
public interface WeChatPayService {
    CreateOrderResponseVo create(PayParamsVo payParamsVo);

    /**
     * 微信预下单接口
     * @param storeOrder 订单
     * @param ip      ip
     * @return 获取wechat.requestPayment()参数
     */
    Map<String, String> unifiedorder(StoreOrder storeOrder, String ip);

    /**
     * 查询支付结果
     * @param orderNo 订单编号
     * @return
     */
    Boolean queryPayResult(String orderNo);

    /**
     * 微信充值预下单接口
     * @param userRecharge 充值订单
     * @param clientIp      ip
     * @return 获取wechat.requestPayment()参数
     */
    Map<String, String> unifiedRecharge(UserRecharge userRecharge, String clientIp);
}
