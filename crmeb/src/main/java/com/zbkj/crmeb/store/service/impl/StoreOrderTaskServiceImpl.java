package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.constants.*;
import com.exception.CrmebException;
import com.utils.DateUtil;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.combination.model.StorePink;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.marketing.model.StoreCouponUser;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.seckill.service.StoreSeckillService;
import com.zbkj.crmeb.sms.service.SmsService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.OrderUtils;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.service.SystemAdminService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.*;
import com.zbkj.crmeb.user.service.*;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private SystemConfigService systemConfigService;

    @Autowired
    private StoreSeckillService storeSeckillService;

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private UserBillService userBillService;

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

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private StoreCouponUserService couponUserService;

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
     * 回滚库存
     * @param storeOrder 订单信息
     */
    private Boolean rollbackStock(StoreOrder storeOrder) {
        try{
            // 查找出商品详情
            List<StoreOrderInfoOldVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
            if(null == orderInfoVoList || orderInfoVoList.size() < 1){
                return true;
            }

            // 兼容处理秒杀数据退款
            // 秒杀商品回滚库存和销量
            if(null != storeOrder.getSeckillId() && storeOrder.getSeckillId() > 0){
                for (StoreOrderInfoOldVo orderInfoVo : orderInfoVoList) {
                    StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                    stockRequest.setSeckillId(storeOrder.getSeckillId());
                    stockRequest.setProductId(orderInfoVo.getProductId());
                    stockRequest.setAttrId(orderInfoVo.getInfo().getAttrValueId());
                    stockRequest.setOperationType("add");
                    stockRequest.setType(1);
                    stockRequest.setSuk(orderInfoVo.getInfo().getSku());
                    if(orderInfoVo.getInfo().getPayNum() < 1){
                        //如果取不到值，则跳过
                        continue;
                    }
                    stockRequest.setNum(orderInfoVo.getInfo().getPayNum());
                    storeSeckillService.stockAddRedis(stockRequest);
                }

            } else if (ObjectUtil.isNotNull(storeOrder.getBargainId()) && storeOrder.getBargainId() > 0) { // 砍价商品回滚销量库存
                for (StoreOrderInfoOldVo orderInfoVo : orderInfoVoList) {
                    StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                    stockRequest.setBargainId(storeOrder.getBargainId());
                    stockRequest.setProductId(orderInfoVo.getProductId());
                    stockRequest.setAttrId(orderInfoVo.getInfo().getAttrValueId());
                    stockRequest.setOperationType("add");
                    stockRequest.setType(2);
                    stockRequest.setSuk(orderInfoVo.getInfo().getSku());
                    if(orderInfoVo.getInfo().getPayNum() < 1){
                        //如果取不到值，则跳过
                        continue;
                    }
                    stockRequest.setNum(orderInfoVo.getInfo().getPayNum());
                    storeBargainService.stockAddRedis(stockRequest);
                }
            } else if (ObjectUtil.isNotNull(storeOrder.getCombinationId()) && storeOrder.getCombinationId() > 0) { // 拼团商品回滚销量库存
                for (StoreOrderInfoOldVo orderInfoVo : orderInfoVoList) {
                    StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                    stockRequest.setCombinationId(storeOrder.getCombinationId());
                    stockRequest.setProductId(orderInfoVo.getProductId());
                    stockRequest.setAttrId(orderInfoVo.getInfo().getAttrValueId());
                    stockRequest.setOperationType("add");
                    stockRequest.setType(3);
                    stockRequest.setSuk(orderInfoVo.getInfo().getSku());
                    if(orderInfoVo.getInfo().getPayNum() < 1){
                        //如果取不到值，则跳过
                        continue;
                    }
                    stockRequest.setNum(orderInfoVo.getInfo().getPayNum());
                    storeCombinationService.stockAddRedis(stockRequest);
                }
            } else { // 正常商品回滚销量库存
                for (StoreOrderInfoOldVo orderInfoVo : orderInfoVoList) {
                    StoreProductStockRequest stockRequest = new StoreProductStockRequest();
                    stockRequest.setProductId(orderInfoVo.getProductId());
                    stockRequest.setAttrId(orderInfoVo.getInfo().getAttrValueId());
                    stockRequest.setOperationType("add");
                    stockRequest.setType(0);
                    stockRequest.setSuk(orderInfoVo.getInfo().getSku());
                    if(orderInfoVo.getInfo().getPayNum() < 1){
                        //如果取不到值，则跳过
                        continue;
                    }
                    stockRequest.setNum(orderInfoVo.getInfo().getPayNum());
                    storeProductService.stockAddRedis(stockRequest);
                }
            }
        }catch (Exception e){
            throw new CrmebException(e.getMessage());
        }

        return true;
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

        // 回滚积分  消耗和获得
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
            userBill.setNumber(bill.getNumber());
            userBill.setBalance(new BigDecimal(user.getExperience()).setScale(2));
            userBill.setMark(StrUtil.format("订单退款，扣除{}赠送经验", bill.getNumber().intValue()));
            userBill.setStatus(1);
            userBillList.add(userBill);
        });

        // 回滚积分
        List<UserIntegralRecord> integralRecordList = userIntegralRecordService.findListByOrderIdAndUid(storeOrder.getOrderId(), storeOrder.getUid());
        integralRecordList.forEach(record -> {
            if (record.getType().equals(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB)) {// 订单抵扣部分
                user.setIntegral(user.getIntegral() + record.getIntegral());
                record.setId(null);
                record.setTitle(IntegralRecordConstants.BROKERAGE_RECORD_TITLE_REFUND);
                record.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
                record.setBalance(user.getIntegral());
                record.setMark(StrUtil.format("订单退款，返还支付扣除得{}积分", record.getIntegral()));
                record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
            }
            if (record.getType().equals(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD)) {// 冻结积分部分
                record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_INVALIDATION);
            }
        });
        List<UserIntegralRecord> addIntegralList = integralRecordList.stream().filter(e -> ObjectUtil.isNull(e.getId())).collect(Collectors.toList());
        List<UserIntegralRecord> updateIntegralList = integralRecordList.stream().filter(e -> ObjectUtil.isNotNull(e.getId())).collect(Collectors.toList());

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

            // 积分部分
            if (CollUtil.isNotEmpty(addIntegralList)) {
                userIntegralRecordService.saveBatch(addIntegralList);
            }
            if (CollUtil.isNotEmpty(updateIntegralList)) {
                userIntegralRecordService.updateBatchById(updateIntegralList);
            }

            // 佣金处理
            if (CollUtil.isNotEmpty(brokerageRecordList)) {
                userBrokerageRecordService.updateBatchById(brokerageRecordList);
            }

            // 经验处理
            userLevelService.upLevel(user);

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

            // 退优惠券
            if (storeOrder.getCouponId() > 0 ) {
                StoreCouponUser couponUser = couponUserService.getById(storeOrder.getCouponId());
                couponUser.setStatus(CouponConstants.STORE_COUPON_USER_STATUS_USABLE);
                couponUserService.updateById(couponUser);
            }
            return Boolean.TRUE;
        });

        return execute;
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
        if (storeOrder.getIsDel() || storeOrder.getIsSystemDel()) {
            return Boolean.TRUE;
        }
        // 获取过期时间
        String cancelStr;
        DateTime cancelTime;
        if (storeOrder.getBargainId() > 0 || storeOrder.getSeckillId() > 0 || storeOrder.getCombinationId() > 0) {
            cancelStr = systemConfigService.getValueByKey("order_activity_time");
        } else {
            cancelStr = systemConfigService.getValueByKey("order_cancel_time");
        }
        if (StrUtil.isBlank(cancelStr)) {
            cancelStr = "1";
        }
        cancelTime = cn.hutool.core.date.DateUtil.offset(storeOrder.getCreateTime(), DateField.HOUR_OF_DAY, Integer.parseInt(cancelStr));
        long between = cn.hutool.core.date.DateUtil.between(cancelTime, cn.hutool.core.date.DateUtil.date(), DateUnit.SECOND, false);
        if (between < 0) {// 未到过期时间继续循环
            return Boolean.FALSE;
        }
        storeOrder.setIsDel(true).setIsSystemDel(true);
        Boolean execute = false;

        execute = transactionTemplate.execute(e -> {
            storeOrderService.updateById(storeOrder);
            //写订单日志
            storeOrderStatusService.createLog(storeOrder.getId(), "cancel", "到期未支付系统自动取消");
            return Boolean.TRUE;
        });
        if (execute) {
            // 回滚库存
            rollbackStock(storeOrder);
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
        logger.info("收货处理佣金条数：" + recordList.size());
        for (UserBrokerageRecord record : recordList) {
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
        }

        // 获取积分记录
        List<UserIntegralRecord> integralRecordList = userIntegralRecordService.findListByOrderIdAndUid(storeOrder.getOrderId(), storeOrder.getUid());
        logger.info("收货处理积分条数：" + integralRecordList.size());
        for (UserIntegralRecord record : integralRecordList) {
            if (!record.getStatus().equals(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_CREATE)) {
                throw new CrmebException(StrUtil.format("订单收货task处理，订单积分记录不是创建状态，id={}", orderId));
            }
            // 佣金进入冻结期
            record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_FROZEN);
            // 计算解冻时间
            Long thawTime = cn.hutool.core.date.DateUtil.current(false);
            if (record.getFrozenTime() > 0) {
                DateTime dateTime = cn.hutool.core.date.DateUtil.offsetDay(new Date(), record.getFrozenTime());
                thawTime = dateTime.getTime();
            }
            record.setThawTime(thawTime);
        }

        Boolean execute = transactionTemplate.execute(e -> {
            // 日志
            storeOrderStatusService.createLog(storeOrder.getId(), "user_take_delivery", Constants.ORDER_STATUS_STR_TAKE);
            // 分佣-佣金进入冻结期
            if (CollUtil.isNotEmpty(recordList)) {
                userBrokerageRecordService.updateBatchById(recordList);
            }
            // 积分进入冻结期
            if (CollUtil.isNotEmpty(integralRecordList)) {
                userIntegralRecordService.updateBatchById(integralRecordList);
            }
            return Boolean.TRUE;
        });
        if (execute) {
            // 发送用户确认收货管理员提醒短信
            String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_ADMIN_REFUND_SWITCH);
            if (StrUtil.isNotBlank(smsSwitch) && smsSwitch.equals("1")) {
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
        if (storeOrder.getIsChannel().equals(2)) {
            return;
        }
        if (!storeOrder.getPayType().equals(Constants.PAY_TYPE_WE_CHAT)) {
            return;
        }
        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        // 公众号
        if (storeOrder.getIsChannel().equals(Constants.ORDER_PAY_CHANNEL_PUBLIC)) {
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
}
