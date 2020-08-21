package com.zbkj.crmeb.store.service.impl;

import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.task.order.OrderRefundByUser;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.request.UserOperateFundsRequest;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author Mr.Zhang
* @description StoreOrderServiceImpl 接口实现
* @date 2020-05-28
*/
@Service
public class StoreOrderTaskServiceImpl implements StoreOrderTaskService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderRefundByUser.class);

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreOrderStatusService storeOrderStatusService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreCouponUserService couponUserService;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 用户取消订单
     * @author Mr.Zhang
     * @since 2020-07-09
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public Boolean cancelByUser(StoreOrder storeOrder) {
        try{
            /*
            * 1、修改订单状态 （用户操作的时候已处理）
            * 2、写订单日志
            * 3、回滚库存
            * 4、回滚优惠券
            * 5、回滚积分
            * */

            //写订单日志
            storeOrderStatusService.createLog(storeOrder.getId(), "cancel_order", "取消订单");

            //回滚优惠券
            rollbackCoupon(storeOrder);

            // 回滚积分
            rollbackIntegral(storeOrder);
            setIntegral(storeOrder, 1); //单独回滚使用积分

            return rollbackStock(storeOrder);
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 用户退款申请
     * @author Mr.Zhang
     * @since 2020-07-09
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public Boolean refundApply(StoreOrder storeOrder) {
        /*
         * 1、修改订单状态 （用户操作的时候已处理）
         * 2、写订单日志
         * 3、回滚消耗积分
         * 4、回滚获得积分
         * 5、回滚冻结期佣金
         * 6、回滚经验
         * */
        try{
            //写订单日志
            storeOrderStatusService.createLog(storeOrder.getId(), "order_refund_apply", "申请退款");

            //回滚积分  消耗和获得
            rollbackIntegral(storeOrder);

            //回滚冻结期佣金
            rollbackBrokeragePrice(storeOrder);

            //回滚经验
            rollbackExp(storeOrder);
            rollbackStock(storeOrder);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 完成订单
     * @author Mr.Zhang
     * @since 2020-07-09
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public Boolean complete(StoreOrder storeOrder) {
        /*
         * 1、修改订单状态 （用户操作的时候已处理）
         * 2、写订单日志
         * */
        try{
            storeOrderStatusService.createLog(storeOrder.getId(), "check_order_over", "用户评价");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 用户已收货
     * @author Mr.Zhang
     * @since 2020-07-09
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public Boolean takeByUser(StoreOrder storeOrder) {
        /*
         * 1、修改订单状态 （用户操作的时候已处理）
         * 2、写订单日志
         * 3、获得佣金
         * 4、获得赠送积分
         * 5、获得赠送经验
         * */
        try{
            //日志
            storeOrderStatusService.createLog(storeOrder.getId(), "user_take_delivery", Constants.ORDER_STATUS_STR_TAKE);

            //获得佣金
            setBrokeragePrice(storeOrder, 1);

            //获得赠送积分
            setIntegral(storeOrder, 1);

            // 获取商品额外赠送的积分
            setGiveIntegral(storeOrder);

            //获得赠送经验
            setExp(storeOrder, 1);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 获得经验
     * @param storeOrder StoreOrder 订单信息
     * @param type int 类型 1 增加，2 减少
     * @author Mr.Zhang
     * @since 2020-07-09
     */
    private void setExp(StoreOrder storeOrder, int type) {
        try{
            UserOperateFundsRequest userOperateFundsRequest = new UserOperateFundsRequest();
            userOperateFundsRequest.setValue(getIntegralByRate(storeOrder.getPayPrice()));
            userOperateFundsRequest.setFoundsType(Constants.ORDER_STATUS_STR_BARGAIN);
            userOperateFundsRequest.setUid(storeOrder.getUid());
            userOperateFundsRequest.setTitle(Constants.ORDER_STATUS_STR_TAKE);
            userOperateFundsRequest.setFoundsCategory(Constants.USER_BILL_CATEGORY_EXPERIENCE);
            userOperateFundsRequest.setType(type);
            userService.updateFounds(userOperateFundsRequest, false);
        }catch (Exception e){
            throw new CrmebException("更新经验失败" + e.getMessage());
        }
    }

    /**
     * 获得积分
     * @param storeOrder StoreOrder 订单信息
     * @param type int 类型 1 增加，2 减少
     * @author Mr.Zhang
     * @since 2020-07-09
     */
    private void setIntegral(StoreOrder storeOrder, int type) {
        try {
            if(storeOrder.getUseIntegral() < 1){
                return;
            }
            UserOperateFundsRequest userOperateFundsRequest = new UserOperateFundsRequest();
            userOperateFundsRequest.setValue(BigDecimal.valueOf(storeOrder.getUseIntegral()));
            userOperateFundsRequest.setFoundsType(Constants.ORDER_STATUS_STR_BARGAIN);
            userOperateFundsRequest.setUid(storeOrder.getUid());
            userOperateFundsRequest.setTitle(Constants.ORDER_STATUS_STR_TAKE);
            userOperateFundsRequest.setFoundsCategory(Constants.USER_BILL_CATEGORY_INTEGRAL);
            userOperateFundsRequest.setType(type);
            userService.updateFounds(userOperateFundsRequest, true);
        }catch (Exception e){
            throw new CrmebException("更新积分失败" + e.getMessage());
        }
    }

    // 获取额外赠送积分
    private void setGiveIntegral(StoreOrder storeOrder){
        // 获取商品额外赠送积分
        List<StoreOrderInfoVo> orderInfoList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
        List<Integer> productIds = orderInfoList.stream().map(StoreOrderInfoVo::getProductId).collect(Collectors.toList());
        if(productIds.size() > 0){
            List<StoreProduct> products = storeProductService.getListInIds(productIds);
            int sumIntegral = products.stream().mapToInt(e -> e.getGiveIntegral().intValue()).sum();
            UserOperateFundsRequest userOperateFundsRequest = new UserOperateFundsRequest();
            userOperateFundsRequest.setValue(BigDecimal.valueOf(sumIntegral));
            userOperateFundsRequest.setFoundsType(Constants.ORDER_STATUS_STR_BARGAIN);
            userOperateFundsRequest.setUid(storeOrder.getUid());
            userOperateFundsRequest.setTitle(Constants.ORDER_STATUS_STR_TAKE);
            userOperateFundsRequest.setFoundsCategory(Constants.USER_BILL_CATEGORY_INTEGRAL);
            userOperateFundsRequest.setType(1);
            userService.updateFounds(userOperateFundsRequest, true);
        }
    }

    /**
     * 计算积分
     * @author Mr.Zhang
     * @since 2020-07-09
     */
    private BigDecimal getIntegralByRate(BigDecimal payPrice) {
        try{
            String rate = systemConfigService.getValueByKey(Constants.CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE);
            if(StringUtils.isBlank(rate)){
                rate = "1";
            }
            BigDecimal rateBigDecimal = CrmebUtil.getBigDecimalRate(rate);
            return rateBigDecimal.multiply(payPrice).setScale(BigDecimal.ROUND_HALF_DOWN);
        }catch (Exception e){
            throw new CrmebException("计算积分失败" + e.getMessage());
        }
    }


    /**
     * 分销员获得佣金
     * @param storeOrder StoreOrder 订单信息
     * @param type int 类型 1 增加，2 减少
     * @author Mr.Zhang
     * @since 2020-07-09
     */
    private void setBrokeragePrice(StoreOrder storeOrder, int type) {
        try{
            //检测商城是否开启分销功能
            String isOpen = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_IS_OPEN);
            if(StringUtils.isBlank(isOpen) || isOpen.equals("0")){
                return;
            }

            //营销产品不参与
            if(storeOrder.getCombinationId() > 0 || storeOrder.getSeckillId() > 0 || storeOrder.getBargainId() > 0){
                return;
            }

            //查找订单所属人信息
            User user = userService.getById(storeOrder.getUid());

            //当前用户不存在 没有上级 或者 当用用户上级时自己  直接返回
            if(user.getSpreadUid() < 1 || null == user.getSpreadUid() || user.getSpreadUid().equals(storeOrder.getUid())){
                return;
            }

            //计算分销人
            List<Integer> spreadList = getSpreadList(user.getPath());
            if(null == spreadList || spreadList.size() < 1){
                return;
            }

            //资金明细加一条数据
            UserOperateFundsRequest userOperateFundsRequest = new UserOperateFundsRequest();
            userOperateFundsRequest.setLinkId(storeOrder.getId().toString());
            userOperateFundsRequest.setTitle(Constants.ORDER_STATUS_STR_TAKE);
            userOperateFundsRequest.setFoundsCategory(Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE);
            userOperateFundsRequest.setFoundsType(Constants.USER_BILL_TYPE_BROKERAGE);
            userOperateFundsRequest.setType(type);
            userOperateFundsRequest.setTitle("推广佣金");


            //计算每个人的佣金
            int i = 1;
            for (Integer spreadId: spreadList) {
                if(spreadId < 1){
                    continue;
                }

                //大于0的用户开始计算佣金, 并更新数据库
                BigDecimal brokeragePrice = getBrokeragePriceByOrder(i, storeOrder.getId());
                User userInfo = userService.getById(spreadId);
                userInfo.setBrokeragePrice(userInfo.getBrokeragePrice().add(brokeragePrice));
                userService.updateById(userInfo);

                //获得推广佣金
                userOperateFundsRequest.setValue(brokeragePrice);
                userService.updateFounds(userOperateFundsRequest, true);
            }

        }catch (Exception e){
            throw new CrmebException("佣金处理失败 " + e.getMessage());
        }

    }

    /**
     * 计算佣金
     * @param i Integer 分销员等级
     * @param id Integer 订单id
     * @author Mr.Zhang
     * @since 2020-07-09
     * @return BigDecimal
     */
    private BigDecimal getBrokeragePriceByOrder(Integer i, Integer id) {
        BigDecimal brokeragePrice = BigDecimal.ZERO;

        try{
            //先看商品是否有固定分佣
            List<StoreOrderInfoVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(id);
            if(null == orderInfoVoList  || orderInfoVoList.size() < 1){
                return brokeragePrice;
            }


            //查询对应等级的分销比例
            String rate = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_LEVEL.replace("num", i.toString()));
            if(StringUtils.isBlank(rate)){
                rate = "1";
            }
            //佣金比例整数存储， 例如80， 所以计算的时候要除以 10*10
            BigDecimal rateBigDecimal = brokeragePrice;
            if(StringUtils.isNotBlank(rate)){
                rateBigDecimal = new BigDecimal(rate).divide(BigDecimal.TEN.multiply(BigDecimal.TEN));
            }

            BigDecimal totalBrokerPrice = BigDecimal.ZERO;
            for (StoreOrderInfoVo orderInfoVo : orderInfoVoList) {
                if(i == 1){
//                    brokeragePrice = orderInfoVo.getInfo().getBigDecimal("brokerage");
                    brokeragePrice = orderInfoVo.getInfo().getBrokerage();
                }else if(i == 2){
//                    brokeragePrice = orderInfoVo.getInfo().getBigDecimal("brokerage_two");
                    brokeragePrice = orderInfoVo.getInfo().getBrokerageTwo();
                }

                if(brokeragePrice.equals(BigDecimal.ZERO) && !rateBigDecimal.equals(BigDecimal.ZERO)){
                    //商品没有分销金额, 并且有设置对应等级的分佣比例
//                    brokeragePrice = orderInfoVo.getInfo().getBigDecimal("price").multiply(rateBigDecimal);
                    brokeragePrice = orderInfoVo.getInfo().getTruePrice().multiply(rateBigDecimal);
                }

                totalBrokerPrice.add(brokeragePrice);
            }
            return totalBrokerPrice;
        }catch (Exception e){
            return brokeragePrice;
        }
    }

    /**
     * 通过分佣等级关系计算需要拿到分佣的用户数据
     * @param path String 推广人关系  /0/1/2/3/4/
     * @author Mr.Zhang
     * @since 2020-07-09
     * @return BigDecimal
     */
    private List<Integer> getSpreadList(String path) {
        try{
            List<Integer> spreadList = CrmebUtil.stringToArray(path);
            if(spreadList.size() < 1){
                return null;
            }

            //去拿所有是分销员的信息
            HashMap<Integer, User> mapListInUidList = userService.getMapListInUid(spreadList);
            if(null == mapListInUidList){
                return null;
            }

            Collections.reverse(spreadList); // 倒序

            //分佣级别
            int level = 2;
            int i = 0;

            //超过返佣等级去除掉
            for (Integer spreadId : spreadList) {
                if(level <= i){
                    spreadList.remove(i);
                }

                //不是推广员，没有找到用户数据去，把推广人id设置为0
                if(!mapListInUidList.containsKey(spreadId) || !mapListInUidList.get(spreadId).getIsPromoter()){
                    spreadList.set(i, 0);
                }
                i++;
            }

            return spreadList;
        }catch (Exception e){
            return null;
        }
    }


    /**
     * 用户删除订单
     * @author Mr.Zhang
     * @since 2020-07-09
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public Boolean deleteByUser(StoreOrder storeOrder) {
        //日志
        storeOrderStatusService.createLog(storeOrder.getId(), "remove_order", "删除订单");
        return rollback(storeOrder);
    }

    private Boolean rollback(StoreOrder storeOrder) {
        try{
            Map<String, String> statusMap = storeOrderService.getStatus(storeOrder);
            String orderStatus = statusMap.get("key");

            //未支付和已退款的状态下才可以退积分退库存退优惠券
            String [] rollbackStatus = {
                    Constants.ORDER_STATUS_REFUNDED,
                    Constants.ORDER_STATUS_UNPAID
            };
            if(Arrays.asList(rollbackStatus).contains(orderStatus)){
                //回滚分
                rollbackIntegral(storeOrder);
                //回滚库存
                rollbackStock(storeOrder);
                //回滚经验
                rollbackExp(storeOrder);
                //佣金
                rollbackBrokeragePrice(storeOrder);
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }
    /**
     * 回滚佣金
     * @param storeOrder 订单信息
     */
    private void rollbackBrokeragePrice(StoreOrder storeOrder) {
        try{
            //查看是否在冻结期之内， 如果在是需要回滚的，如果不在则不需要回滚
            String time = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_EXTRACT_TIME);
            if(StringUtils.isBlank(time)){
                return;
            }

            String endTime = DateUtil.addDay(storeOrder.getPayTime(), Integer.parseInt(time), Constants.DATE_FORMAT);
            if(endTime.compareTo(DateUtil.nowDateTimeStr()) < 1){
                return;
            }

            setBrokeragePrice(storeOrder, 2);
        }catch (Exception e){
            throw new CrmebException("回滚佣金失败" + e.getMessage());
        }
    }

    /**
     * 回滚经验
     * @param storeOrder 订单信息
     */
    private void rollbackExp(StoreOrder storeOrder) {
        setExp(storeOrder, 2);
    }

    /**
     * 回滚库存
     * @param storeOrder 订单信息
     */
    private Boolean rollbackStock(StoreOrder storeOrder) {
        try{
            //拿出商品详情
            List<StoreOrderInfoVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
            if(null == orderInfoVoList || orderInfoVoList.size() < 1){
                return true;
            }

            for (StoreOrderInfoVo orderInfoVo : orderInfoVoList) {
                StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                stockRequest.setProductId(orderInfoVo.getProductId());
                stockRequest.setAttrId(Integer.valueOf(orderInfoVo.getInfo().getProductAttrUnique()));
                stockRequest.setType("add");
//                if(orderInfoVo.getInfo().getInteger("cart_num") < 1){
                if(orderInfoVo.getInfo().getCartNum() < 1){
                    //如果取不到值，则跳过
                    continue;
                }

                //TODO 看库存是否这样更新
//                stockRequest.setNum(orderInfoVo.getInfo().getInteger("cart_num"));
                stockRequest.setNum(orderInfoVo.getInfo().getCartNum());
                storeProductService.stockAddRedis(stockRequest);
            }
        }catch (Exception e){
            throw new CrmebException(e.getMessage());
        }

        return true;
    }

    /**
     * 回滚积分
     * @param storeOrder 订单信息
     */
    private void rollbackIntegral(StoreOrder storeOrder) {

        //回滚使用积分
        if(storeOrder.getUseIntegral() > 0){
            //有退积分操作， 那么用户使用的积分会减少
//            storeOrder.setUseIntegral(storeOrder.getUseIntegral()- (storeOrder.getBackIntegral()));
            storeOrder.setBackIntegral(storeOrder.getUseIntegral());
        }
        setIntegral(storeOrder, 1);


        //回滚获得积分
        storeOrder.setUseIntegral(storeOrder.getGainIntegral()); //因为setIntegral里用使用积分参数来当做值，所以此处需要修改
        setIntegral(storeOrder, 2);

    }

    /**
     * 回滚优惠券
     * @param storeOrder 订单信息
     */
    private void rollbackCoupon(StoreOrder storeOrder){
        //回滚用户已使用的优惠券
        couponUserService.rollbackByCancelOrder(storeOrder);
    }
}
