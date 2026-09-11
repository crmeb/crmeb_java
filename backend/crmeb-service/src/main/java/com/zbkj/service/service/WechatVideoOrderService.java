package com.zbkj.service.service;


import com.zbkj.common.vo.*;

/**
 * 视频号交易组件服务——订单部分
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public interface WechatVideoOrderService {

    /**
     * 检查场景值是否在支付校验范围内
     * @return is_matched 0: 不在支付校验范围内，1: 在支付校验范围内
     */
    Integer shopSceneCheck(Integer scene);

    /**
     * 生成订单并获取ticket
     * 注意：该接口可重入，如果order_id或out_order_id已存在，会直接更新整个订单数据
     * 请求成功后将会创建一个status=10的订单
     * 每个ticket只能消费一次，创建订单接口可以多次调，但是不是生成新ticket要视情况而定
     * 场景A: 第一次生成ticketA，拉起收银台消费ticketA后ticketA就失效了 第二次再调就生成新的ticketB了
     * 场景B: 第一次生成ticketA，不调收银台消费这个ticket，那么24小时内再调生成的还是ticketA，超过24小时生成的是新的ticketA
     * @return ShopOrderAddResultVo
     */
    ShopOrderAddResultVo shopOrderAdd(ShopOrderAddVo shopOrderAddVo);

    /**
     * 同步订单支付结果
     * @return Boolean
     */
    Boolean shopOrderPay(ShopOrderPayVo shopOrderPayVo);

    /**
     * 获取订单详情
     * @return ShopOrderVo
     */
    ShopOrderVo shopOrderGet(ShopOrderCommonVo shopOrderCommonVo);
}
