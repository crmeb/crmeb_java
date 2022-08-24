package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.vo.ExpressSheetVo;
import com.zbkj.common.vo.LogisticsResultVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * StoreOrderService 接口
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
public interface StoreOrderService extends IService<StoreOrder> {

    /**
     * 列表（PC）
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @return CommonPage<StoreOrderDetailResponse>
     */
    CommonPage<StoreOrderDetailResponse> getAdminList(StoreOrderSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 核销列表
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @return List<StoreOrder>
     */
    SystemWriteOffOrderResponse getWriteOffList(SystemWriteOffOrderSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 累计消费
     * @param userId Integer 用户id
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return UserBalanceResponse
     */
    BigDecimal getSumBigDecimal(Integer userId, String date);

    /**
     * H5订单列表
     * @param uid 用户uid
     * @param status 评价等级|0=未支付,1=待发货,2=待收货,3=待评价,4=已完成,-3=售后/退款
     * @param pageParamRequest 分页参数
     * @return 订单结果列表
     */
    List<StoreOrder> getUserOrderList(Integer uid, Integer status, PageParamRequest pageParamRequest);
    /**
     * 创建订单
     * @param storeOrder 订单参数
     * @return 创建结果
     */
    boolean create(StoreOrder storeOrder);

    /**
     * 根据属性仅仅获取一条
     * @param storeOrder 参数
     * @return 当前查询结果
     */
    StoreOrder getByEntityOne(StoreOrder storeOrder);

    /**
     * h5 top data 工具方法
     * @param status 状态参数
     * @return 查询到的订单结果
     */
    Integer getTopDataUtil(Integer status, Integer userId);

    List<StoreOrder> getOrderGroupByDate(String dateLimit, int lefTime);

    boolean refund(StoreOrderRefundRequest request);

    /**
     * 订单详情（PC）
     * @param orderNo 订单编号
     * @return StoreOrderInfoResponse
     */
    StoreOrderInfoResponse info(String orderNo);

    Boolean send(StoreOrderSendRequest request);

    /**
     * 订单备注
     * @param orderNo 订单编号
     * @param mark 备注
     * @return Boolean
     */
    Boolean mark(String orderNo, String mark);

    /**
     * 拒绝退款
     * @param orderNo 订单编号
     * @param reason String 原因
     * @return Boolean
     */
    Boolean refundRefuse(String orderNo, String reason);

    StoreOrder getInfoByEntity(StoreOrder storeOrder);

    /**
     * 获取订单快递信息
     * @param orderNo 订单编号
     * @return LogisticsResultVo
     */
    LogisticsResultVo getLogisticsInfo(String orderNo);

    /**
     * 根据时间参数统计订单价格
     * @param dateLimit 时间区间
     * @param type 1=price 2=订单量
     * @return 统计订单信息
     */
    StoreOrderStatisticsResponse orderStatisticsByTime(String dateLimit, Integer type);

    /**
     * 获取用户当天的秒杀数量
     *
     * @param uid 用户uid
     * @param seckillId 秒杀商品id
     * @return 用户当天的秒杀商品订单数量
     */
    List<StoreOrder> getUserCurrentDaySecKillOrders(Integer uid, Integer seckillId);

    /**
     * 获取用户当前的砍价订单
     * @param uid    用户uid
     * @return  用户当前的砍价订单
     */
    List<StoreOrder> getUserCurrentBargainOrders(Integer uid, Integer bargainId);

    /**
     * 获取用户当前的拼团订单
     * @param uid    用户uid
     * @return  用户当前的拼团订单
     */
    List<StoreOrder> getUserCurrentCombinationOrders(Integer uid, Integer combinationId);

    StoreOrder getByOderId(String orderId);

    /**
     * 获取面单默认配置信息
     * @return ExpressSheetVo
     */
    ExpressSheetVo getDeliveryInfo();

    /**
     * 更新支付结果
     * @param orderNo 订单编号
     */
    Boolean updatePaid(String orderNo);

    /**
     * 跟据订单号列表获取订单列表Map
     * @param orderNoList 订单号列表
     * @return Map
     */
    Map<String, StoreOrder> getMapInOrderNo(List<String> orderNoList);

    /**
     * 获取推广订单总金额
     * @param orderNoList 订单编号列表
     * @return BigDecimal
     */
    BigDecimal getSpreadOrderTotalPriceByOrderList(List<String> orderNoList);

    /**
     * 获取所有收货订单id集合
     * @return List<StoreOrder>
     */
    List<StoreOrder> findIdAndUidListByReceipt();

    /**
     * 根据用户uid查询所有已支付订单
     * @param userId 用户uid
     * @param pageParamRequest 分页参数
     * @return List<StoreOrder>
     */
    List<StoreOrder> findPaidListByUid(Integer userId, PageParamRequest pageParamRequest);

    /**
     * 订单改价
     * @param request 改价请求对象
     * @return 改价是否成功
     */
    Boolean updatePrice(StoreOrderUpdatePriceRequest request);

    /**
     * 获取订单总数量
     * @param uid 用户uid
     * @return Integer
     */
    Integer getOrderCountByUid(Integer uid);

    /**
     * 获取用户总消费金额
     * @param userId 用户uid
     * @return BigDecimal
     */
    BigDecimal getSumPayPriceByUid(Integer userId);

    /**
     * 获取订单数量(时间)
     * @param uid 用户uid
     * @return Integer
     */
    Integer getOrderCountByUidAndDate(Integer uid, String date);

    /**
     * 获取用户消费金额(时间)
     * @param userId 用户uid
     * @return BigDecimal
     */
    BigDecimal getSumPayPriceByUidAndDate(Integer userId, String date);

    /**
     * 获取砍价订单
     * @param bargainId 砍价商品id
     * @param bargainUserId 用户砍价活动id
     * @return StoreOrder
     */
    StoreOrder getByBargainOrder(Integer bargainId, Integer bargainUserId);

    /**
     * 获取订单状态数量
     * @param dateLimit 时间端
     * @param type 订单类型：0普通订单，1-视频号订单, 2-全部订单
     * @return StoreOrderCountItemResponse
     */
    StoreOrderCountItemResponse getOrderStatusNum(String dateLimit, Integer type);

    /**
     * 获取订单统计数据
     * @param dateLimit 时间端
     * @return StoreOrderTopItemResponse
     */
    StoreOrderTopItemResponse getOrderData(String dateLimit);

    /**
     * 订单删除
     * @param orderNo 订单编号
     * @return Boolean
     */
    Boolean delete(String orderNo);

    /**
     * 通过日期获取商品交易件数
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    Integer getOrderProductNumByDate(String date);

    /**
     * 通过日期获取商品交易成功件数
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    Integer getOrderSuccessProductNumByDate(String date);

    /**
     * 通过日期获取订单数量
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    Integer getOrderNumByDate(String date);

    /**
     * 通过日期获取支付订单数量
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    Integer getPayOrderNumByDate(String date);

    /**
     * 通过日期获取支付订单金额
     * @param date 日期，yyyy-MM-dd格式
     * @return BigDecimal
     */
    BigDecimal getPayOrderAmountByDate(String date);

    /**
     * 通过日期获取支付订单金额
     * @param startDate 日期
     * @param endDate 日期
     * @return BigDecimal
     */
    BigDecimal getPayOrderAmountByPeriod(String startDate, String endDate);

    /**
     * 通过日期获取余额支付订单支付金额
     * @param date 日期，yyyy-MM-dd格式
     * @return BigDecimal
     */
    BigDecimal getYuePayOrderAmountByDate(String date);

    /**
     * 获取累计消费金额
     * @return BigDecimal
     */
    BigDecimal getTotalPrice();

    /**
     * 根据日期获取下单用户数量
     * @param date 日期
     * @return Integer
     */
    Integer getOrderUserNumByDate(String date);

    /**
     * 根据日期获取下单用户数量
     * @param startDate 日期
     * @param endDate 日期
     * @return Integer
     */
    Integer getOrderUserNumByPeriod(String startDate, String endDate);

    /**
     * 根据日期获取成交用户数量
     * @param date 日期
     * @return Integer
     */
    Integer getOrderPayUserNumByDate(String date);

    /**
     * 根据日期获取成交用户数量
     * @param startDate 日期
     * @param endDate 日期
     * @return Integer
     */
    Integer getOrderPayUserNumByPeriod(String startDate, String endDate);

    /**
     * 根据用户uid列表获取成交用户数量
     * @param uidList 用户列表
     * @return Integer
     */
    Integer getOrderPayUserNumByUidList(List<Integer> uidList);

    /**
     * 根据用户uid列表获取支付金额
     * @param uidList 用户列表
     * @return BigDecimal
     */
    BigDecimal getPayOrderAmountByUidList(List<Integer> uidList);

    /**
     * 获取待发货订单数量
     * @return Integer
     */
    Integer getNotShippingNum();

    /**
     * 获取退款中订单数量
     */
    Integer getRefundingNum();

    /**
     * 获取待核销订单数量
     */
    Integer getNotWriteOffNum();

    /**
     * 获取佣金相关数据
     * @param uid 用户uid
     * @param spreadId 推广人uid
     */
    OrderBrokerageData getBrokerageData(Integer uid, Integer spreadId);
}
