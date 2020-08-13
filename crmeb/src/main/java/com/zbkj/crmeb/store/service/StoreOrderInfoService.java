package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreOrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.request.StoreOrderInfoSearchRequest;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;

import java.util.HashMap;
import java.util.List;

/**
* @author Mr.Zhang
* @description StoreOrderInfoService 接口
* @date 2020-05-28
*/
public interface StoreOrderInfoService extends IService<StoreOrderInfo> {

    List<StoreOrderInfo> getList(StoreOrderInfoSearchRequest request, PageParamRequest pageParamRequest);

    HashMap<Integer, List<StoreOrderInfoVo>> getMapInId(List<Integer> orderIdList);

    public List<StoreOrderInfoVo> getOrderListByOrderId(Integer orderId);

    /**
     * 批量添加订单详情
     * @param storeOrderInfos 订单详情集合
     * @return 保存结果
     */
    boolean saveOrderInfos(List<StoreOrderInfo> storeOrderInfos);
}
