package com.zbkj.service.service;


import com.zbkj.common.vo.DeliveryCompanyVo;
import com.zbkj.common.vo.DeliverySendVo;
import com.zbkj.common.vo.ShopOrderCommonVo;

import java.util.List;

/**
 * 视频号交易组件服务——交付部分
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
public interface WechatVideoDeliveryService {

    /**
     * 获取快递公司列表
     * @return List<DeliveryCompanyVo>
     */
    List<DeliveryCompanyVo> shopDeliveryGetCompanyList();

    /**
     * 订单发货
     * @return Boolean
     */
    Boolean shopDeliverySend(DeliverySendVo deliverySendVo);

    /**
     * 订单确认收货
     * 把订单状态从30（待收货）流转到100（完成）
     * @return Boolean
     */
    Boolean shopDeliveryRecieve(ShopOrderCommonVo shopOrderCommonVo);
}
