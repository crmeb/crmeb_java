package com.zbkj.crmeb.store.service;


import com.zbkj.crmeb.store.model.StoreOrder;

/**
 * 订单任务服务
 * @author Mr.Zhang
 * @description StoreOrderService 接口
 * @date 2020-05-28
 */
 public interface StoreOrderTaskService {

     Boolean cancelByUser(StoreOrder storeOrder);

     Boolean refundApply(StoreOrder storeOrder);

     Boolean complete(StoreOrder storeOrder);

     Boolean takeByUser(StoreOrder storeOrder);

     Boolean deleteByUser(StoreOrder storeOrder);
 }
