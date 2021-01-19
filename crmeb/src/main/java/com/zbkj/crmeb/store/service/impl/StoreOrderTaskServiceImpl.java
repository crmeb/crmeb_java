package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.payment.service.OrderPayService;
import com.zbkj.crmeb.seckill.service.StoreSeckillService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.request.UserOperateFundsRequest;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * StoreOrderTaskService实现类
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
@Service
public class StoreOrderTaskServiceImpl implements StoreOrderTaskService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(StoreOrderTaskServiceImpl.class);

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

    @Autowired
    private StoreSeckillService storeSeckillService;

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private OrderPayService orderPayService;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private TransactionTemplate transactionTemplate;

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
            storeOrderStatusService.saveRefund(storeOrder.getId(), storeOrder.getRefundPrice(), "成功");

            //回滚积分  消耗和获得
            rollbackIntegral(storeOrder);

            //回滚冻结期佣金
            rollbackBrokeragePrice(storeOrder);

            //回滚经验
            rollbackExp(storeOrder);

            // 回滚库存
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
         * 1、写订单日志
         * 2、获得佣金
         * */
        try{
            //日志
            storeOrderStatusService.createLog(storeOrder.getId(), "user_take_delivery", Constants.ORDER_STATUS_STR_TAKE);

            //获得佣金
            setBrokeragePrice(storeOrder, 1);

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
        UserOperateFundsRequest userOperateFundsRequest = new UserOperateFundsRequest();
        userOperateFundsRequest.setValue(BigDecimal.valueOf(storeOrder.getUseIntegral()));
        userOperateFundsRequest.setFoundsType(Constants.ORDER_STATUS_STR_BARGAIN);
        userOperateFundsRequest.setUid(storeOrder.getUid());
        userOperateFundsRequest.setTitle(Constants.ORDER_STATUS_STR_TAKE);
        userOperateFundsRequest.setFoundsCategory(Constants.USER_BILL_CATEGORY_INTEGRAL);
        userOperateFundsRequest.setType(type);
        userService.updateFounds(userOperateFundsRequest, true);
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
            if(null == user.getSpreadUid() || user.getSpreadUid() < 1 || user.getSpreadUid().equals(storeOrder.getUid())){
                return;
            }

            //计算分销人
//            List<Integer> spreadList = getSpread List(user.getPath());
            List<Integer> spreadList = getSpreadList(user.getSpreadUid());
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
//                userService.updateById(userInfo); 重复加了佣金

                //获得推广佣金
                userOperateFundsRequest.setUid(spreadId);
                userOperateFundsRequest.setValue(brokeragePrice);
                userService.updateFounds(userOperateFundsRequest, true);
                i++;
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
            String key = Constants.CONFIG_KEY_STORE_BROKERAGE_LEVEL.replace("num", i.toString());
            if (i == 1) {
                key = Constants.CONFIG_KEY_STORE_BROKERAGE_RATE_ONE;
            }
            if (i == 2) {
                key = Constants.CONFIG_KEY_STORE_BROKERAGE_RATE_TWO;
            }
            String rate = systemConfigService.getValueByKey(key);
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
                    brokeragePrice = orderInfoVo.getInfo().getProductInfo().getAttrInfo().getBrokerage();
                }else if(i == 2){
//                    brokeragePrice = orderInfoVo.getInfo().getBigDecimal("brokerage_two");
                    brokeragePrice = orderInfoVo.getInfo().getProductInfo().getAttrInfo().getBrokerageTwo();
                }

                if(brokeragePrice.compareTo(BigDecimal.ZERO) == 0 && !rateBigDecimal.equals(BigDecimal.ZERO)){
                    //商品没有分销金额, 并且有设置对应等级的分佣比例
//                    brokeragePrice = orderInfoVo.getInfo().getBigDecimal("price").multiply(rateBigDecimal);
                    brokeragePrice = orderInfoVo.getInfo().getTruePrice().multiply(rateBigDecimal).setScale(2, BigDecimal.ROUND_HALF_UP);
                }

                totalBrokerPrice = totalBrokerPrice.add(brokeragePrice);
            }
            return totalBrokerPrice;
        }catch (Exception e){
            return brokeragePrice;
        }
    }

    /**
     * 通过分佣等级关系计算需要拿到分佣的用户数据
     * @param spreadId String 推广人ID
     */
    private List<Integer> getSpreadList(Integer spreadId) {
        List<Integer> spreadList = CollUtil.newArrayList();
        User spreadUser = userService.getById(spreadId);
        if (ObjectUtil.isEmpty(spreadUser)) {
            return spreadList;
        }
        if (!spreadUser.getIsPromoter()) {
            spreadList.add(0);
        } else {
            spreadList.add(spreadId);
        }
        if (ObjectUtil.isNull(spreadUser.getSpreadUid()) || spreadUser.getSpreadUid() == 0) {
            return spreadList;
        }

        User spreadSpreadUser = userService.getById(spreadUser.getSpreadUid());
        if (ObjectUtil.isNotNull(spreadSpreadUser)) {
            if (!spreadSpreadUser.getIsPromoter()) {
                spreadList.add(0);
            } else {
                spreadList.add(spreadSpreadUser.getUid());
            }
        }
        return spreadList;
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
            // 查找出商品详情
            List<StoreOrderInfoVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
            if(null == orderInfoVoList || orderInfoVoList.size() < 1){
                return true;
            }

            // 兼容处理秒杀数据退款
            // 秒杀商品回滚库存和销量
            if(null != storeOrder.getSeckillId() && storeOrder.getSeckillId() > 0){
                for (StoreOrderInfoVo orderInfoVo : orderInfoVoList) {
                    StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                    stockRequest.setSeckillId(orderInfoVo.getInfo().getSeckillId());
                    stockRequest.setProductId(orderInfoVo.getProductId());
                    stockRequest.setAttrId(Integer.valueOf(orderInfoVo.getInfo().getProductAttrUnique()));
                    stockRequest.setOperationType("add");
                    stockRequest.setType(orderInfoVo.getInfo().getProductInfo().getAttrInfo().getType());
                    stockRequest.setSuk(orderInfoVo.getInfo().getProductInfo().getAttrInfo().getSuk());
                    if(orderInfoVo.getInfo().getCartNum() < 1){
                        //如果取不到值，则跳过
                        continue;
                    }
                    stockRequest.setNum(orderInfoVo.getInfo().getCartNum());
                    storeSeckillService.stockAddRedis(stockRequest);
                }

            }else if (ObjectUtil.isNotNull(storeOrder.getBargainId()) && storeOrder.getBargainId() > 0) { // 砍价商品回滚销量库存
                for (StoreOrderInfoVo orderInfoVo : orderInfoVoList) {
                    StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                    stockRequest.setBargainId(orderInfoVo.getInfo().getBargainId());
                    stockRequest.setProductId(orderInfoVo.getProductId());
                    stockRequest.setAttrId(Integer.valueOf(orderInfoVo.getInfo().getProductAttrUnique()));
                    stockRequest.setOperationType("add");
                    stockRequest.setType(orderInfoVo.getInfo().getProductInfo().getAttrInfo().getType());
                    stockRequest.setSuk(orderInfoVo.getInfo().getProductInfo().getAttrInfo().getSuk());
                    if(orderInfoVo.getInfo().getCartNum() < 1){
                        //如果取不到值，则跳过
                        continue;
                    }
                    stockRequest.setNum(orderInfoVo.getInfo().getCartNum());
                    storeBargainService.stockAddRedis(stockRequest);
                }
            } if (ObjectUtil.isNotNull(storeOrder.getCombinationId()) && storeOrder.getCombinationId() > 0) { // 拼团商品回滚销量库存
                for (StoreOrderInfoVo orderInfoVo : orderInfoVoList) {
                    StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                    stockRequest.setCombinationId(orderInfoVo.getInfo().getCombinationId());
                    stockRequest.setProductId(orderInfoVo.getProductId());
                    stockRequest.setAttrId(Integer.valueOf(orderInfoVo.getInfo().getProductAttrUnique()));
                    stockRequest.setOperationType("add");
                    stockRequest.setType(orderInfoVo.getInfo().getProductInfo().getAttrInfo().getType());
                    stockRequest.setSuk(orderInfoVo.getInfo().getProductInfo().getAttrInfo().getSuk());
                    if(orderInfoVo.getInfo().getCartNum() < 1){
                        //如果取不到值，则跳过
                        continue;
                    }
                    stockRequest.setNum(orderInfoVo.getInfo().getCartNum());
                    storeCombinationService.stockAddRedis(stockRequest);
                }
            } else { // 正常商品回滚销量库存
                for (StoreOrderInfoVo orderInfoVo : orderInfoVoList) {
                    StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                    stockRequest.setProductId(orderInfoVo.getProductId());
                    stockRequest.setAttrId(Integer.valueOf(orderInfoVo.getInfo().getProductAttrUnique()));
                    stockRequest.setOperationType("add");
                    stockRequest.setType(orderInfoVo.getInfo().getProductInfo().getAttrInfo().getType());
                    stockRequest.setSuk(orderInfoVo.getInfo().getProductInfo().getAttrInfo().getSuk());
                    if(orderInfoVo.getInfo().getCartNum() < 1){
                        //如果取不到值，则跳过
                        continue;
                    }
                    stockRequest.setNum(orderInfoVo.getInfo().getCartNum());
                    storeProductService.stockAddRedis(stockRequest);
                }
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

    /**
     * 订单退款处理
     * 退款得时候根据userBill 来进行回滚
     */
    @Override
    public Boolean refundOrder(StoreOrder storeOrder) {
        /**
         * 1、写订单日志
         * 2、回滚消耗积分
         * 3、回滚获得积分
         * 4、回滚冻结期佣金
         * 5、回滚经验
         * 6、回滚库存
         * 7、发送通知
         * 实际上2-5就是user数据的处理+userBill的记录
         */
        // 获取用户对象
        User user = userService.getById(storeOrder.getUid());
        if (ObjectUtil.isNull(user)) {
            logger.error("订单退款处理，对应的用户不存在,storeOrder===>" + storeOrder);
            return Boolean.FALSE;
        }

        List<UserBill> userBillList = CollUtil.newArrayList();

        //回滚积分  消耗和获得
        // 通过userBill记录获取订单之前处理的记录
        List<UserBill> userBills = userBillService.findListByOrderIdAndUid(storeOrder.getId(), storeOrder.getUid());

        // 回滚经验
        List<UserBill> experienceList = userBills.stream().filter(e -> e.getCategory().equals(Constants.USER_BILL_CATEGORY_EXPERIENCE)).collect(Collectors.toList());
        experienceList.forEach(bill -> {
            user.setExperience(user.getExperience() - bill.getNumber().intValue());

            UserBill userBill = new UserBill();
            BeanUtils.copyProperties(bill, userBill);
            userBill.setId(null);
            userBill.setTitle(Constants.ORDER_STATUS_STR_REFUNDED);
            userBill.setPm(0);
            userBill.setType(Constants.USER_BILL_TYPE_PAY_PRODUCT_REFUND);
            userBill.setBalance(new BigDecimal(user.getExperience()));
            userBill.setMark(StrUtil.format("订单退款，扣除{}赠送经验", bill.getNumber().intValue()));
            userBill.setStatus(1);
            userBillList.add(userBill);
        });

        // 回滚积分
        List<UserBill> integralList = userBills.stream().filter(e -> e.getCategory().equals(Constants.USER_BILL_CATEGORY_INTEGRAL)).collect(Collectors.toList());
        integralList.forEach(bill -> {
            UserBill userBill = new UserBill();
            BeanUtils.copyProperties(bill, userBill);
            if (bill.getPm() == 0) {// 返还用户使用的积分
                user.setIntegral(user.getIntegral() + bill.getNumber().intValue());
                userBill.setId(null);
                userBill.setTitle(Constants.ORDER_STATUS_STR_REFUNDED);
                userBill.setPm(1);
                userBill.setType(Constants.USER_BILL_TYPE_PAY_PRODUCT_REFUND);
                userBill.setBalance(new BigDecimal(user.getIntegral()));
                userBill.setMark(StrUtil.format("订单退款，返还{}支付扣除积分", bill.getNumber().intValue()));
                userBill.setStatus(1);
                userBillList.add(userBill);
            }
            if (bill.getPm() == 1) {// 回滚之前获得的积分
                user.setIntegral(user.getIntegral() - bill.getNumber().intValue());
                userBill.setId(null);
                userBill.setTitle(Constants.ORDER_STATUS_STR_REFUNDED);
                userBill.setPm(0);
                userBill.setType(Constants.USER_BILL_TYPE_PAY_PRODUCT_REFUND);
                userBill.setBalance(new BigDecimal(user.getIntegral()));
                userBill.setMark(StrUtil.format("订单退款，扣除{}订单赠送积分", bill.getNumber().intValue()));
                userBill.setStatus(1);
                userBillList.add(userBill);
            }
        });

        StoreOrder tempOrder = new StoreOrder();
        tempOrder.setId(storeOrder.getId());
        tempOrder.setRefundStatus(2);
        // 回滚佣金 TODO 这里之后处理

        Boolean execute = transactionTemplate.execute(e -> {
            //写订单日志
            storeOrderStatusService.saveRefund(storeOrder.getId(), storeOrder.getRefundPrice(), "成功");

            // 更新用户数据
            userService.updateById(user);

            // 用户账单记录处理
            userBillService.saveBatch(userBillList);

            //回滚冻结期佣金
            rollbackBrokeragePrice(storeOrder);

            // 回滚库存
            rollbackStock(storeOrder);

            storeOrderService.updateById(tempOrder);
            return Boolean.TRUE;
        });

        if (execute) {
            // 发送通知
        }

        return execute;
    }

    /**
     * 订单支付成功后置处理
     * @param storeOrder
     * @return
     */
    @Override
    public Boolean paySuccessAfter(StoreOrder storeOrder) {
        return orderPayService.paySuccess(storeOrder);
    }

    /**
     * 回滚佣金
     * @param storeOrder 订单信息
     * @param user 订单用户
     */
    private void rollbackBrokeragePrice(StoreOrder storeOrder, User user) {
        //计算分销人
        List<Integer> spreadList = getSpreadList(user.getSpreadUid());
        if(null == spreadList || spreadList.size() < 1){
            return;
        }
        //资金明细加一条数据
        UserOperateFundsRequest userOperateFundsRequest = new UserOperateFundsRequest();
        userOperateFundsRequest.setLinkId(storeOrder.getId().toString());
        userOperateFundsRequest.setTitle("推广佣金");
        userOperateFundsRequest.setFoundsCategory(Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE);
        userOperateFundsRequest.setFoundsType(Constants.USER_BILL_TYPE_BROKERAGE);
        userOperateFundsRequest.setType(0);


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

            //获得推广佣金
            userOperateFundsRequest.setUid(spreadId);
            userOperateFundsRequest.setValue(brokeragePrice);
            userService.updateFounds(userOperateFundsRequest, true);
            i++;
        }
    }

    /**
     * 检查是否需要回滚佣金
     * @param storeOrder 订单
     * @param user
     */
    private Boolean checkRollbackBrokeragePrice(StoreOrder storeOrder, User user) {
        //营销产品不参与
        if(storeOrder.getCombinationId() > 0 || storeOrder.getSeckillId() > 0 || storeOrder.getBargainId() > 0){
            return Boolean.FALSE;
        }

        //当前用户不存在 没有上级 或者 当用用户上级时自己  直接返回
        if(user.getSpreadUid() < 1 || null == user.getSpreadUid() || 0 == user.getSpreadUid() || user.getSpreadUid().equals(storeOrder.getUid())){
            return Boolean.FALSE;
        }

        //检测商城是否开启分销功能
        String isOpen = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_IS_OPEN);
        if(StringUtils.isBlank(isOpen) || isOpen.equals("0")){
            return Boolean.FALSE;
        }

        // 查看是否在冻结期之内， 如果在是需要回滚的，如果不在则不需要回滚
        String time = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_EXTRACT_TIME);
        if(StringUtils.isBlank(time)){
            return Boolean.FALSE;
        }
        DateTime patTime = cn.hutool.core.date.DateUtil.offsetDay(storeOrder.getPayTime(), Integer.parseInt(time));
        long between = cn.hutool.core.date.DateUtil.between(patTime, DateUtil.nowDateTime(), DateUnit.MS, false);
        if (between < 0) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 创建退款UserBill对象（包含积分、佣金、经验）
     * @param storeOrder 订单
     * @param user 用户
     * @param category 分类
     * @param pm 1 = 增加， 0 = 减少
     *
     */
    private UserBill createRefundUserBill(StoreOrder storeOrder, User user, String category, Integer pm) {
        UserBill userBill = new UserBill();
        BigDecimal balance = getUserBalance(user, category);
        userBill.setTitle(Constants.ORDER_STATUS_STR_REFUNDED);
        userBill.setUid(storeOrder.getUid());
        userBill.setCategory(category);
        userBill.setLinkId(storeOrder.getOrderId());  //链接id
        userBill.setPm(pm);
        userBill.setBalance(balance);
        switch (category) {
            case Constants.USER_BILL_CATEGORY_INTEGRAL:// 积分
                userBill.setType(Constants.USER_BILL_TYPE_PAY_PRODUCT_INTEGRAL_BACK);
                if (pm.equals(1)) {// 返还积分
                    userBill.setNumber(new BigDecimal(storeOrder.getUseIntegral()));
                } else {// 扣除奖励积分
                    userBill.setNumber(new BigDecimal(storeOrder.getGainIntegral()));
                }
                userBill.setMark(getMark(userBill));
                break;
            case Constants.USER_BILL_CATEGORY_EXPERIENCE:// 经验
                break;
            case Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE:// 佣金
                break;
            case Constants.USER_BILL_CATEGORY_MONEY:// 余额
                break;
        }
        return userBill;
    }

    /**
     * 获取用户剩余
     * @param user 用户
     * @param category 分类
     * @return BigDecimal
     */
    private BigDecimal getUserBalance(User user, String category) {
        BigDecimal value = BigDecimal.ZERO;
        switch (category) {
            case Constants.USER_BILL_CATEGORY_INTEGRAL:// 积分
                value = new BigDecimal(user.getIntegral());
                break;
            case Constants.USER_BILL_CATEGORY_EXPERIENCE:// 经验
                value = new BigDecimal(user.getExperience());
                break;
            case Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE:// 佣金
                value = user.getBrokeragePrice();
                break;
            case Constants.USER_BILL_CATEGORY_MONEY:// 余额
                value = user.getNowMoney();
                break;
        }
        return value;
    }

    private String getMark(UserBill userBill) {
        String operate = (userBill.getPm() == 1) ? "增加" : "减少";
        String founds = "";
        switch (userBill.getCategory()){
            case Constants.USER_BILL_CATEGORY_INTEGRAL:
                founds = "积分";
                break;
            case Constants.USER_BILL_CATEGORY_MONEY:
                founds = "余额";
                break;
            case Constants.USER_BILL_CATEGORY_EXPERIENCE:
                founds = "经验";
                break;
            case Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE:
                founds = "佣金";
                break;
        }

        return Constants.USER_BILL_OPERATE_LOG_TITLE.replace("{$title}", userBill.getTitle()).replace("{$operate}", operate).replace("{$founds}", founds);
    }

    /**
     * 计算佣金
     * @param i Integer 分销员等级
     * @param id Integer 订单id
     */
    private BigDecimal getBrokeragePrice(Integer i, Integer id) {
        BigDecimal brokeragePrice = BigDecimal.ZERO;

        //先看商品是否有固定分佣
        List<StoreOrderInfoVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(id);
        if(null == orderInfoVoList  || orderInfoVoList.size() < 1){
            return brokeragePrice;
        }

        //查询对应等级的分销比例
        String key = "";
        if (i == 1) {
            key = Constants.CONFIG_KEY_STORE_BROKERAGE_RATE_ONE;
        }
        if (i == 2) {
            key = Constants.CONFIG_KEY_STORE_BROKERAGE_RATE_TWO;
        }
        String rate = systemConfigService.getValueByKey(key);
        if(StrUtil.isBlank(rate)){
            return brokeragePrice;
        }
        //佣金比例整数存储， 例如80， 所以计算的时候要除以 10*10
        BigDecimal rateBigDecimal = new BigDecimal(rate).divide(new BigDecimal(100));

        BigDecimal totalBrokerPrice = BigDecimal.ZERO;
        for (StoreOrderInfoVo orderInfoVo : orderInfoVoList) {
            if(i == 1){
                brokeragePrice = orderInfoVo.getInfo().getProductInfo().getAttrInfo().getBrokerage();
            }else if(i == 2){
                brokeragePrice = orderInfoVo.getInfo().getProductInfo().getAttrInfo().getBrokerageTwo();
            }

            if(brokeragePrice.compareTo(BigDecimal.ZERO) == 0 && !rateBigDecimal.equals(BigDecimal.ZERO)){
                //商品没有分销金额, 并且有设置对应等级的分佣比例
                brokeragePrice = orderInfoVo.getInfo().getTruePrice().multiply(rateBigDecimal).setScale(2, BigDecimal.ROUND_HALF_UP);
            }

            totalBrokerPrice = totalBrokerPrice.add(brokeragePrice);
        }
        return totalBrokerPrice;
    }
}
