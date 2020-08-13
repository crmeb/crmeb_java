package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreOrderStatus;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.request.StoreOrderStatusSearchRequest;

import java.math.BigDecimal;
import java.util.List;

/**
* @author Mr.Zhang
* @description StoreOrderStatusService 接口
* @date 2020-05-28
*/
public interface StoreOrderStatusService extends IService<StoreOrderStatus> {

    List<StoreOrderStatus> getList(StoreOrderStatusSearchRequest request, PageParamRequest pageParamRequest);

    void saveRefund(Integer orderId, BigDecimal amount, String message);

    void createLog(Integer orderId, String type, String message);

    /**
     * 根据实体参数获取
     * @param storeOrderStatus 订单状态参数
     * @return 订单状态结果
     */
    List<StoreOrderStatus> getByEntity(StoreOrderStatus storeOrderStatus);
}
