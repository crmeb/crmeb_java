package com.zbkj.crmeb.front.service;

import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.zbkj.crmeb.front.request.*;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.store.request.StoreProductReplyAddRequest;

import java.util.HashMap;
import java.util.List;

/**
 * H5端订单操作
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public interface OrderService {

    /**
     * 订单列表
     * @param type 类型
     * @param pageRequest 分页
     * @return 订单集合
     */
    CommonPage<OrderDetailResponse> list(Integer type, PageParamRequest pageRequest);

    /**
     * 订单详情
     * @param orderId 订单id
     */
    StoreOrderDetailResponse detailOrder(String orderId);

    /**
     * 订单状态数量
     * @return 订单状态数据量
     */
    OrderDataResponse orderData();

    /**
     * 查询退款理由
     * @return 退款理由集合
     */
    List<String> getRefundReason();

    Boolean delete(Integer id);

    /**
     * 创建订单商品评价
     * @param request 请求参数
     * @return Boolean
     */
    Boolean reply(StoreProductReplyAddRequest request);

    boolean take(Integer id);

    boolean cancel(Integer id);

    boolean refundApply(OrderRefundApplyRequest request);

    /**
     * 订单退款申请Task使用
     * @param applyList 退款List
     * @return Boolean
     */
    Boolean refundApplyTask(List<OrderRefundApplyRequest> applyList);

    /**
     * 订单物流查看
     */
    Object expressOrder(String orderId);

    /**
     * 获取待评价商品信息
     * @param getProductReply 订单详情参数
     * @return 待评价
     */
    OrderProductReplyResponse getReplyProduct(GetProductReply getProductReply);

    /**
     * 获取申请订单退款信息
     * @param orderId 订单编号
     * @return ApplyRefundOrderInfoResponse
     */
    ApplyRefundOrderInfoResponse applyRefundOrderInfo(String orderId);

    /**
     * 订单预下单
     * @param request 预下单请求参数
     * @return PreOrderResponse
     */
    MyRecord preOrder(PreOrderRequest request);

    /**
     * 加载预下单信息
     * @param preOrderNo 预下单号
     * @return 预下单信息
     */
    PreOrderResponse loadPreOrder(String preOrderNo);

    /**
     * 计算订单价格
     * @param request 计算订单价格请求对象
     * @return ComputedOrderPriceResponse
     */
    ComputedOrderPriceResponse computedOrderPrice(OrderComputedPriceRequest request);

    /**
     * 创建订单
     * @param orderRequest 创建订单请求参数
     * @return MyRecord 订单编号
     */
    MyRecord createOrder(CreateOrderRequest orderRequest);

}
