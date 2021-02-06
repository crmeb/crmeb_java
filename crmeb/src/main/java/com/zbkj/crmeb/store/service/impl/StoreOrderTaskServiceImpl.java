package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.common.MyRecord;
import com.constants.BrokerageRecordConstants;
import com.constants.Constants;
import com.constants.SmsConstants;
import com.constants.UserConstants;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.combination.model.StorePink;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.payment.service.OrderPayService;
import com.zbkj.crmeb.seckill.service.StoreSeckillService;
import com.zbkj.crmeb.sms.service.SmsService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.OrderUtils;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.service.SystemAdminService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.model.UserBrokerageRecord;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.request.UserOperateFundsRequest;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserBrokerageRecordService;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.user.service.UserTokenService;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForPaySuccess;
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
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private TemplateMessageService templateMessageService;

    @Autowired
    private OrderUtils orderUtils;

    @Autowired
    private StorePinkService storePinkService;

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
//            rollbackCoupon(storeOrder);

            // 回滚积分
//            rollbackIntegral(storeOrder);
//            setIntegral(storeOrder, 1); //单独回滚使用积分

            return rollbackStock(storeOrder);
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
        // 佣金处理：只处理冻结期佣金
        // 查询佣金记录
        List<UserBrokerageRecord> brokerageRecordList = CollUtil.newArrayList();
        List<UserBrokerageRecord> recordList = userBrokerageRecordService.findListByLinkIdAndLinkType(storeOrder.getOrderId(), BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        if (CollUtil.isNotEmpty(recordList)) {
            recordList.forEach(r -> {
                //创建、冻结期佣金置为失效状态
                if (r.getStatus() < BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE) {
                    r.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_INVALIDATION);
                    brokerageRecordList.add(r);
                }
            });
        }

        Boolean execute = transactionTemplate.execute(e -> {
            //写订单日志
            storeOrderStatusService.saveRefund(storeOrder.getId(), storeOrder.getRefundPrice(), "成功");

            // 更新用户数据
            userService.updateById(user);

            // 用户账单记录处理
            userBillService.saveBatch(userBillList);

            // 佣金处理
            if (CollUtil.isNotEmpty(brokerageRecordList)) {
                userBrokerageRecordService.updateBatchById(brokerageRecordList);
            }
//            rollbackBrokeragePrice(storeOrder);

            // 回滚库存
            rollbackStock(storeOrder);

            storeOrderService.updateById(tempOrder);

            // 拼团状态处理
            if (storeOrder.getCombinationId() > 0) {
                StorePink storePink = storePinkService.getByOrderId(storeOrder.getOrderId());
                storePink.setStatus(3);
                storePink.setIsRefund(true);
                storePinkService.updateById(storePink);
            }
            return Boolean.TRUE;
        });

        if (execute) {
            // 发送通知
        }

        return execute;
    }

    /**
     * 订单支付成功后置处理
     */
    @Override
    public Boolean paySuccessAfter(StoreOrder storeOrder) {
        return orderPayService.paySuccess(storeOrder);
    }

    /**
     * 超时未支付系统自动取消
     */
    @Override
    public Boolean autoCancel(StoreOrder storeOrder) {
        // 判断订单是否支付
        if (storeOrder.getPaid()) {
            return Boolean.TRUE;
        }
        // 获取过期时间
        String cancelStr = "";
        if (storeOrder.getBargainId() > 0 || storeOrder.getSeckillId() > 0 || storeOrder.getCombinationId() > 0) {
            cancelStr = systemConfigService.getValueByKey("order_activity_time");
        } else {
            cancelStr = systemConfigService.getValueByKey("order_cancel_time");
        }
        if (StrUtil.isBlank(cancelStr)) {
            cancelStr = "1";
        }
        DateTime cancelTime = cn.hutool.core.date.DateUtil.offset(storeOrder.getCreateTime(), DateField.HOUR_OF_DAY, Integer.parseInt(cancelStr));
        long between = cn.hutool.core.date.DateUtil.between(cancelTime, cn.hutool.core.date.DateUtil.date(), DateUnit.SECOND, false);
        if (between < 0) {// 未到过期时间继续循环
            return Boolean.FALSE;
        }
        storeOrder.setIsDel(true).setIsSystemDel(true);

        Boolean execute = transactionTemplate.execute(e -> {
            storeOrderService.updateById(storeOrder);
            //写订单日志
            storeOrderStatusService.createLog(storeOrder.getId(), "cancel", "到期未支付系统自动取消");
            // TODO 回滚库存

            return Boolean.TRUE;
        });
        if (execute) {
            // 回滚库存
            rollbackStock(storeOrder);
            /*// 查找出商品详情
            List<StoreOrderInfoVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
            if(null == orderInfoVoList || orderInfoVoList.size() < 1){
                logger.error("自动取消未付款订单式，未找到对应的订单商品详情,orderNo===" + storeOrder.getOrderId());
                return true;
            }
            if (storeOrder.getSeckillId() > 0) {// 秒杀回滚
                StoreOrderInfoVo storeOrderInfoVo = orderInfoVoList.get(0);
                // 秒杀商品回滚库存
                storeSeckillService.operationStock(storeOrderInfoVo.getInfo().getSeckillId(), storeOrderInfoVo.getInfo().getCartNum(), "add");
                // 秒杀商品规格回滚库存
                storeProductAttrValueService.operationStock(Integer.valueOf(storeOrderInfoVo.getInfo().getProductAttrUnique()), storeOrderInfoVo.getInfo().getCartNum(), "add", Constants.PRODUCT_TYPE_SECKILL);
                // 普通商品回滚库存
                storeProductService.operationStock(storeOrderInfoVo.getProductId(), storeOrderInfoVo.getInfo().getCartNum(), "add");
                // 普通商品规格回滚库存
                storeProductAttrValueService.operationStock(skuRecord.getInt("attrValueId"), storeOrderInfoVo.getInfo().getCartNum(), "add", Constants.PRODUCT_TYPE_NORMAL);
            }
            if (storeOrder.getBargainId() > 0) {// 砍价回滚

            }
            if (storeOrder.getCombinationId() > 0) {// 拼团回滚

            }
            // 普通回滚*/
        }
        return execute;
    }

    /**
     * 订单收货task处理
     * @param orderId 订单id
     * @return Boolean
     * 1.写订单日志
     * 2.分佣-佣金进入冻结期
     */
    @Override
    public Boolean orderReceiving(Integer orderId) {
        StoreOrder storeOrder = storeOrderService.getById(orderId);
        if (ObjectUtil.isNull(storeOrder)) {
            throw new CrmebException(StrUtil.format("订单收货task处理，未找到订单，id={}", orderId));
        }
        User user = userService.getById(storeOrder.getUid());

        // 获取佣金记录
        List<UserBrokerageRecord> recordList = userBrokerageRecordService.findListByLinkIdAndLinkType(storeOrder.getOrderId(), BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        recordList.forEach(record -> {
            if (!record.getStatus().equals(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE)) {
                throw new CrmebException(StrUtil.format("订单收货task处理，订单佣金记录不是创建状态，id={}", orderId));
            }
            // 佣金进入冻结期
            record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_FROZEN);
            // 计算解冻时间
            Long thawTime = cn.hutool.core.date.DateUtil.current(false);
            if (record.getFrozenTime() > 0) {
                DateTime dateTime = cn.hutool.core.date.DateUtil.offsetDay(new Date(), record.getFrozenTime());
                thawTime = dateTime.getTime();
            }
            record.setThawTime(thawTime);
        });

        Boolean execute = transactionTemplate.execute(e -> {
            // 日志
            storeOrderStatusService.createLog(storeOrder.getId(), "user_take_delivery", Constants.ORDER_STATUS_STR_TAKE);
            // 分佣-佣金进入冻结期
            if (CollUtil.isNotEmpty(recordList)) {
                userBrokerageRecordService.updateBatchById(recordList);
            }
            return Boolean.TRUE;
        });
        if (execute) {
            // 发送用户确认收货管理员提醒短信
            String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_ADMIN_REFUND_SWITCH);
            if (StrUtil.isNotBlank(smsSwitch) || smsSwitch.equals("1")) {
                // 查询可已发送短信的管理员
                List<SystemAdmin> systemAdminList = systemAdminService.findIsSmsList();
                if (CollUtil.isNotEmpty(systemAdminList)) {
                    // 发送短信
                    systemAdminList.forEach(admin -> {
                        smsService.sendOrderReceiptNotice(admin.getPhone(), storeOrder.getOrderId(), admin.getRealName());
                    });
                }
            }

            // 发送消息通知
            pushMessageOrder(storeOrder, user);
        }

        return execute;
    }

    /**
     * 发送消息通知
     * 根据用户类型发送
     * 公众号模板消息
     * 小程序订阅消息
     */
    private void pushMessageOrder(StoreOrder storeOrder, User user) {
        if (user.getUserType().equals(UserConstants.USER_TYPE_H5)) {
            return;
        }
        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        // 公众号
        if (user.getUserType().equals(UserConstants.USER_TYPE_WECHAT)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // 发送微信模板消息
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "您购买的商品已确认收货！");
            temMap.put("keyword1", storeOrder.getOrderId());
            temMap.put("keyword2", "已收货");
            temMap.put("keyword3", DateUtil.nowDateTimeStr());
            temMap.put("keyword4", "详情请进入订单查看");
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "感谢你的使用。");
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_ORDER_RECEIVING, temMap, userToken.getToken());
            return;
        }
        // 小程序发送订阅消息
        userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            return ;
        }
        // 组装数据
        // 获取商品名称
        String storeNameAndCarNumString = orderUtils.getStoreNameAndCarNumString(storeOrder.getId());
        if (StrUtil.isBlank(storeNameAndCarNumString)) {
            return ;
        }
        if (storeNameAndCarNumString.length() > 20) {
            storeNameAndCarNumString = storeNameAndCarNumString.substring(0, 15) + "***";
        }
        temMap.put("character_string6", storeOrder.getOrderId());
        temMap.put("phrase4", "已收货");
        temMap.put("time7", DateUtil.nowDateTimeStr());
        temMap.put("thing1", storeNameAndCarNumString);
        temMap.put("thing5", "您购买的商品已确认收货！");
        templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_ORDER_RECEIVING, temMap, userToken.getToken());
    }

    /**
     * 分配佣金
     * @param storeOrder
     * @return
     */
    private List<MyRecord> assignCommission(StoreOrder storeOrder) {
        // 检测商城是否开启分销功能
        String isOpen = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_IS_OPEN);
        if(StrUtil.isBlank(isOpen) || isOpen.equals("0")){
            return CollUtil.newArrayList();
        }
        // 营销产品不参与
        if(storeOrder.getCombinationId() > 0 || storeOrder.getSeckillId() > 0 || storeOrder.getBargainId() > 0){
            return CollUtil.newArrayList();
        }
        // 查找订单所属人信息
        User user = userService.getById(storeOrder.getUid());
        // 当前用户不存在 没有上级 或者 当用用户上级时自己  直接返回
        if(null == user.getSpreadUid() || user.getSpreadUid() < 1 || user.getSpreadUid().equals(storeOrder.getUid())){
            return CollUtil.newArrayList();
        }
        // 获取参与分佣的人（两级）
        List<MyRecord> spreadRecordList = getSpreadRecordList(user.getSpreadUid());
        if (CollUtil.isEmpty(spreadRecordList)) {
            return CollUtil.newArrayList();
        }
        spreadRecordList.forEach(record -> {
            BigDecimal brokerage = calculateCommission(record, storeOrder.getId());
            record.set("brokerage", brokerage);
        });
        return spreadRecordList;
    }

    /**
     * 计算佣金
     * @param record index-分销级数，spreadUid-分销人
     * @param orderId 订单id
     * @return
     */
    private BigDecimal calculateCommission(MyRecord record, Integer orderId) {
        BigDecimal brokeragePrice = BigDecimal.ZERO;
        //先看商品是否有固定分佣
        List<StoreOrderInfoVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(orderId);
        if(null == orderInfoVoList  || orderInfoVoList.size() < 1){
            return brokeragePrice;
        }

        //查询对应等级的分销比例
        Integer index = record.getInt("index");
        String key = "";
        if (index == 1) {
            key = Constants.CONFIG_KEY_STORE_BROKERAGE_RATE_ONE;
        }
        if (index == 2) {
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
            if(index == 1){
                brokeragePrice = orderInfoVo.getInfo().getProductInfo().getAttrInfo().getBrokerage();
            }
            if(index == 2){
                brokeragePrice = orderInfoVo.getInfo().getProductInfo().getAttrInfo().getBrokerageTwo();
            }

            if(brokeragePrice.compareTo(BigDecimal.ZERO) == 0 && !rateBigDecimal.equals(BigDecimal.ZERO)){
                // 商品没有分销金额, 并且有设置对应等级的分佣比例
                // 舍入模式向零舍入。
                brokeragePrice = orderInfoVo.getInfo().getTruePrice().multiply(rateBigDecimal).setScale(2, BigDecimal.ROUND_DOWN);
            }

            totalBrokerPrice = totalBrokerPrice.add(brokeragePrice);
        }
        return totalBrokerPrice;


    }

    /**
     * 获取参与奋勇人员（两级）
     * @param spreadUid 一级奋分佣人Uid
     * @return
     */
    private List<MyRecord> getSpreadRecordList(Integer spreadUid) {
        List<MyRecord> recordList = CollUtil.newArrayList();

        // 第一级
        User spreadUser = userService.getById(spreadUid);
        if (ObjectUtil.isNull(spreadUser)) {
            return recordList;
        }
        // 判断分销模式
        String model = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_MODEL);
        if (StrUtil.isNotBlank(model) && model.equals("1") && !spreadUser.getIsPromoter()) {
            // 指定分销模式下：不是推广员不参与分销
            return recordList;
        }
        MyRecord firstRecord = new MyRecord();
        firstRecord.set("index", 1);
        firstRecord.set("spreadUid", spreadUid);

        // 第二级
        User spreadSpreadUser = userService.getById(spreadUser.getSpreadUid());
        if (ObjectUtil.isNull(spreadSpreadUser)) {
            return recordList;
        }
        if (StrUtil.isNotBlank(model) && model.equals("1") && !spreadSpreadUser.getIsPromoter()) {
            // 指定分销模式下：不是推广员不参与分销
            return recordList;
        }
        MyRecord secondRecord = new MyRecord();
        secondRecord.set("index", 2);
        secondRecord.set("spreadUid", spreadSpreadUser.getUid());
        return recordList;
    }

}
