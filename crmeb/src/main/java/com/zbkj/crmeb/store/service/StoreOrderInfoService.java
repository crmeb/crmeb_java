package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreOrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.request.StoreOrderInfoSearchRequest;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;

import java.util.HashMap;
import java.util.List;

/**
 * StoreOrderInfoService 接口
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
public interface StoreOrderInfoService extends IService<StoreOrderInfo> {

    List<StoreOrderInfo> getList(StoreOrderInfoSearchRequest request, PageParamRequest pageParamRequest);

    HashMap<Integer, List<StoreOrderInfoOldVo>> getMapInId(List<Integer> orderIdList);

    List<StoreOrderInfoOldVo> getOrderListByOrderId(Integer orderId);

    /**
     * 批量添加订单详情
     * @param storeOrderInfos 订单详情集合
     * @return 保存结果
     */
    boolean saveOrderInfos(List<StoreOrderInfo> storeOrderInfos);

    /**
     * 通过订单编号和规格号查询
     * @param uni 规格号
     * @param orderId 订单编号
     * @return StoreOrderInfo
     */
    StoreOrderInfo getByUniAndOrderId(String uni, Integer orderId);

    /**
     * 获取订单详情vo列表
     * @param orderId 订单id
     * @return List<StoreOrderInfoVo>
     */
    List<StoreOrderInfoVo> getVoListByOrderId(Integer orderId);
}
