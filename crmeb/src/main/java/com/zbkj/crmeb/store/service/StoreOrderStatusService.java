package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreOrderStatus;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.request.StoreOrderStatusSearchRequest;

import java.math.BigDecimal;
import java.util.List;

/**
 * StoreOrderStatusService 接口
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
public interface StoreOrderStatusService extends IService<StoreOrderStatus> {

    List<StoreOrderStatus> getList(StoreOrderStatusSearchRequest request, PageParamRequest pageParamRequest);

    void saveRefund(Integer orderId, BigDecimal amount, String message);

    void createLog(Integer orderId, String type, String message);

    Boolean addLog(Integer orderId, String type, String message);

    /**
     * 根据实体参数获取
     * @param storeOrderStatus 订单状态参数
     * @return 订单状态结果
     */
    List<StoreOrderStatus> getByEntity(StoreOrderStatus storeOrderStatus);
}
