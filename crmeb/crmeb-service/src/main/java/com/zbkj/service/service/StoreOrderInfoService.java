package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.order.StoreOrderInfo;
import com.zbkj.common.vo.StoreOrderInfoOldVo;
import com.zbkj.common.vo.StoreOrderInfoVo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * StoreOrderInfoService 接口
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
public interface StoreOrderInfoService extends IService<StoreOrderInfo> {

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

    /**
     * 获取订单详情-订单编号
     * @param orderNo 订单编号
     * @return List
     */
    List<StoreOrderInfo> getListByOrderNo(String orderNo);

    /**
     * 根据时间、商品id获取销售件数
     * @param date 时间，格式'yyyy-MM-dd'
     * @param proId 商品id
     * @return Integer
     */
    Integer getSalesNumByDateAndProductId(String date, Integer proId);

    /**
     * 根据时间、商品id获取销售额
     * @param date 时间，格式'yyyy-MM-dd'
     * @param proId 商品id
     * @return BigDecimal
     */
    BigDecimal getSalesByDateAndProductId(String date, Integer proId);
}
