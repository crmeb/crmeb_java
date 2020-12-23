package com.zbkj.crmeb.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.express.vo.ExpressSheetVo;
import com.zbkj.crmeb.express.vo.LogisticsResultVo;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.request.RetailShopStairUserRequest;
import com.zbkj.crmeb.store.request.StoreOrderRefundRequest;
import com.zbkj.crmeb.store.request.StoreOrderSearchRequest;
import com.zbkj.crmeb.store.request.StoreOrderSendRequest;
import com.zbkj.crmeb.store.response.*;
import com.zbkj.crmeb.system.request.SystemWriteOffOrderSearchRequest;
import com.zbkj.crmeb.system.response.SystemWriteOffOrderResponse;
import com.zbkj.crmeb.user.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * StoreOrderService 接口
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
public interface StoreOrderService extends IService<StoreOrder> {

    List<StoreOrder> getList(StoreOrderSearchRequest request, PageParamRequest pageParamRequest);

    StoreOrderResponse getAdminList(StoreOrderSearchRequest request, PageParamRequest pageParamRequest);

    SystemWriteOffOrderResponse getWriteOffList(SystemWriteOffOrderSearchRequest request, PageParamRequest pageParamRequest);

    BigDecimal getSumBigDecimal(Integer userId, String date);

    Map<Integer, StoreOrder> getMapInId(List<Integer> orderIdList);

    /**
     * H5订单列表
     * @param storeOrder 查询参数
     * @param pageParamRequest 分页参数
     * @return 订单结果列表
     */
    List<StoreOrder> getUserOrderList(StoreOrder storeOrder, PageParamRequest pageParamRequest);
    /**
     * 创建订单
     * @param storeOrder 订单参数
     * @return 创建结果
     */
    boolean create(StoreOrder storeOrder);

    /**
     *  根据参数直接下单
     * @param userId 用户id
     * @param productId 产品id
     * @param cartNum 商品数量
     * @param productAttrUnique 商品唯一标识
     * @param type 商品默认类型
     * @param isNew isNew
     * @param combinationId 拼团id
     * @param skillId 秒杀id
     * @param bargainId 砍价id
     * @return 是否成功下单
     */
    List<String> addCartAgain(Integer userId, Integer productId, Integer cartNum, String productAttrUnique, String type,
                              boolean isNew, Integer combinationId, Integer skillId, Integer bargainId);

    /**
     * 订单基本查询
     * @param storeOrder 订单参数
     * @return 订单查询结果
     */
    List<StoreOrder> getByEntity(StoreOrder storeOrder);

    /**
     * 根据属性仅仅获取一条
     * @param storeOrder 参数
     * @return 当前查询结果
     */
    StoreOrder getByEntityOne(StoreOrder storeOrder);

    /**
     * 基本更新
     * @param storeOrder 更新参数
     * @return 更新结果
     */
    boolean updateByEntity(StoreOrder storeOrder);

    /**
     * 余额支付
     * @param storeOrder 待支付订单
     * @param currentUser 当前用户
     * @param formId 购买平台标识
     * @return 支付结果
     */
    boolean yuePay(StoreOrder storeOrder, User currentUser, String formId);

    /**
     * h5 top data 工具方法
     * @param status 状态参数
     * @return 查询到的订单结果
     */
    List<StoreOrder> getTopDataUtil(int status, int userId);

    int getOrderCount(Integer userId, String date);

    List<StoreOrder> getOrderGroupByDate(String dateLimit, int lefTime);

    boolean refund(StoreOrderRefundRequest request);

    StoreOrderInfoResponse info(Integer id);

    boolean send(StoreOrderSendRequest request);

    boolean mark(Integer id, String mark);

    boolean refundRefuse(Integer id, String reason);

    RetailShopOrderDataResponse getOrderDataByUserId(Integer userId);

    List<StoreOrder> getOrderByUserIdsForRetailShop(List<Integer> ids);

    StoreOrder getInfoByEntity(StoreOrder storeOrder);

    /**
     * 根据条件获取
     * @param storeOrder 订单条件
     * @return 结果
     */
    StoreOrder getInfoJustOrderInfo(StoreOrder storeOrder);

    LogisticsResultVo getLogisticsInfo(Integer id);

    Map<String, String> getStatus(StoreOrder storeOrder);

    /**
     * 更改订单价格
     * @param orderId 订单id wx开头
     * @param price 待更改价格
     * @return 更改结果
     */
    boolean editPrice(String orderId,BigDecimal price);

    /**
     *  确认付款
     * @param orderId 订单号
     * @return 确认付款结果
     */
    boolean confirmPayed(String orderId);

    /**
     * 线下付款
     * @param orderId 待付款订单id
     * @return 付款结果
     */
    boolean payOrderOffLine(Integer orderId);

    /**
     * 根据时间参数统计订单价格
     * @param dateLimit 时间区间
     * @param type 1=price 2=订单量
     * @return 统计订单信息
     */
    StoreOrderStatisticsResponse orderStatisticsByTime(String dateLimit,Integer type);

    /**
     * 获取用户当天的秒杀数量
     * @param storeOrder    订单查询参数
     * @return  用户当天的秒杀商品订单数量
     */
    List<StoreOrder> getUserCurrentDaySecKillOrders(StoreOrder storeOrder);

    /**
     * 获取用户当前的砍价订单数量
     * @param storeOrder    订单查询参数
     * @return  用户当天的秒杀商品订单数量
     */
    List<StoreOrder> getUserCurrentBargainOrders(StoreOrder storeOrder);

    /**
     * 获取砍价商品订单数量（销量）
     * @param bargainId 砍价商品编号
     * @return
     */
    Integer getCountByBargainId(Integer bargainId);

    /**
     * 获取砍价商品订单数量（销量）
     * @param bargainId 砍价商品编号
     * @return
     */
    Integer getCountByBargainIdAndUid(Integer bargainId, Integer uid);

    StoreOrder getByOderId(String orderId);

    /**
     * 获取面单默认配置信息
     * @return
     */
    ExpressSheetVo getDeliveryInfo();

    PageInfo<StoreOrder> findListByUserIdsForRetailShop(List<Integer> userIds, RetailShopStairUserRequest request, PageParamRequest pageParamRequest);
}
