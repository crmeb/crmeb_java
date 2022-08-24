package com.zbkj.service.service;


import com.zbkj.common.model.order.StoreOrder;

/**
 * 订单任务服务
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface StoreOrderTaskService {

    Boolean cancelByUser(StoreOrder storeOrder);

    Boolean complete(StoreOrder storeOrder);

    Boolean refundOrder(StoreOrder storeOrder);

    Boolean autoCancel(StoreOrder storeOrder);

    Boolean orderReceiving(Integer orderId);
}
