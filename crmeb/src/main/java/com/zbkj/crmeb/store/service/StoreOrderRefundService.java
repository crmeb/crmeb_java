package com.zbkj.crmeb.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.request.StoreOrderRefundRequest;


/**
* @author Mr.Zhang
* @description StoreOrderService 接口
* @date 2020-05-28
*/
public interface StoreOrderRefundService extends IService<StoreOrder> {
    void refund(StoreOrderRefundRequest request, StoreOrder storeOrder);
}
