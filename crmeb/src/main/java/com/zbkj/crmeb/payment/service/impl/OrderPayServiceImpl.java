package com.zbkj.crmeb.payment.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.common.MyRecord;
import com.constants.*;
import com.exception.CrmebException;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserService;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.combination.model.StorePink;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.front.request.OrderPayRequest;
import com.zbkj.crmeb.front.response.OrderPayResultResponse;
import com.zbkj.crmeb.front.vo.WxPayJsResultVo;
import com.zbkj.crmeb.marketing.model.StoreCouponUser;
import com.zbkj.crmeb.marketing.service.StoreCouponService;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.payment.service.OrderPayService;
import com.zbkj.crmeb.payment.vo.wechat.PayParamsVo;
import com.zbkj.crmeb.payment.wechat.WeChatPayService;
import com.zbkj.crmeb.sms.service.SmsService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductAttrValue;
import com.zbkj.crmeb.store.model.StoreProductCoupon;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.OrderUtils;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.service.SystemAdminService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.*;
import com.zbkj.crmeb.user.service.*;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import com.zbkj.crmeb.wechat.service.WeChatService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * OrderPayService 实现类
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
@Data
@Service
public class OrderPayServiceImpl implements OrderPayService {
    private static final Logger logger = LoggerFactory.getLogger(OrderPayServiceImpl.class);

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderStatusService storeOrderStatusService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Lazy
    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private TemplateMessageService templateMessageService;

    @Autowired
    private UserBillService userBillService;

    @Lazy
    @Autowired
    private SmsService smsService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductCouponService storeProductCouponService;

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private OrderUtils orderUtils;

    //订单类
    private StoreOrder order;

    //支付类参参数
    private PayParamsVo payParamsVo;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private StoreBargainUserService storeBargainUserService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private StorePinkService storePinkService;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    @Autowired
    private StoreCouponService storeCouponService;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    /**
     * 支付成功处理
     * @param storeOrder 订单
     */
    @Override
    public Boolean paySuccess(StoreOrder storeOrder) {

        User user = userService.getById(storeOrder.getUid());

        List<UserBill> billList = CollUtil.newArrayList();
        List<UserIntegralRecord> integralList = CollUtil.newArrayList();

        // 订单支付记录
        UserBill userBill = userBillInit(storeOrder, user);
        billList.add(userBill);

        // 积分抵扣记录
        if (storeOrder.getUseIntegral() > 0) {
            UserIntegralRecord integralRecordSub = integralRecordSubInit(storeOrder, user);
            integralList.add(integralRecordSub);
        }

        // 经验处理：1.经验添加，2.等级计算
        Integer experience;
        experience = storeOrder.getPayPrice().setScale(0, BigDecimal.ROUND_DOWN).intValue();
        user.setExperience(user.getExperience() + experience);
        // 经验添加记录
        UserBill experienceBill = experienceBillInit(storeOrder, user.getExperience(), experience);
        billList.add(experienceBill);

        // 积分处理：1.下单赠送积分，2.商品赠送积分
        int integral;
        // 下单赠送积分
        //赠送积分比例
        String integralStr = systemConfigService.getValueByKey(Constants.CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE);
        if (StrUtil.isNotBlank(integralStr)) {
            BigDecimal integralBig = new BigDecimal(integralStr);
            integral = integralBig.multiply(storeOrder.getPayPrice()).setScale(0, BigDecimal.ROUND_DOWN).intValue();
            if (integral > 0) {
                // 生成积分记录
                UserIntegralRecord integralRecord = integralRecordInit(storeOrder, user.getIntegral(), integral, "order");
                integralList.add(integralRecord);
            }
        }

        // 商品赠送积分
        // 查询订单详情
        // 获取商品额外赠送积分
        List<StoreOrderInfoOldVo> orderInfoList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
        List<Integer> productIds = orderInfoList.stream().map(StoreOrderInfoOldVo::getProductId).collect(Collectors.toList());
        if(productIds.size() > 0){
            List<StoreProduct> products = storeProductService.getListInIds(productIds);
            int sumIntegral = products.stream().mapToInt(StoreProduct::getGiveIntegral).sum();
            if (sumIntegral > 0) {
                // 生成积分记录
                UserIntegralRecord integralRecord = integralRecordInit(storeOrder, user.getIntegral(), sumIntegral, "product");
                integralList.add(integralRecord);
            }
        }

        // 更新用户下单数量
        user.setPayCount(user.getPayCount() + 1);

        /**
         * 计算佣金，生成佣金记录
         */
        List<UserBrokerageRecord> recordList = assignCommission(storeOrder);

        Boolean execute = transactionTemplate.execute(e -> {
            //订单日志
            storeOrderStatusService.addLog(storeOrder.getId(), Constants.ORDER_LOG_PAY_SUCCESS, Constants.ORDER_LOG_MESSAGE_PAY_SUCCESS);

            // 用户信息变更
            userService.updateById(user);

            //资金变动
            userBillService.saveBatch(billList);

            // 积分记录
            userIntegralRecordService.saveBatch(integralList);

            //经验升级
            userLevelService.upLevel(user);

            // 佣金记录
            if (CollUtil.isNotEmpty(recordList)) {
                recordList.forEach(temp -> {
                    temp.setLinkId(storeOrder.getOrderId());
                });
                userBrokerageRecordService.saveBatch(recordList);
            }

            // 如果是砍价商品，修改砍价状态
            if (storeOrder.getBargainId() > 0) {
//                StoreBargainUser storeBargainUser = storeBargainUserService.getByBargainIdAndUid(storeOrder.getBargainId(), user.getUid());
//                storeBargainUser.setStatus(3);
//                storeBargainUserService.updateById(storeBargainUser);
            }

            // 如果是拼团订单进行拼团后置处理
            if (storeOrder.getCombinationId() > 0) {
                pinkProcessing(storeOrder);
            }
            return Boolean.TRUE;
        });

        if (execute) {
            try {
                // 发送短信
                if (StrUtil.isNotBlank(user.getPhone())) {
                    // 支付成功提醒开关
                    String lowerOrderSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_LOWER_ORDER_SWITCH);
                    if (StrUtil.isNotBlank(lowerOrderSwitch) && lowerOrderSwitch.equals("1")) {
                        smsService.sendPaySuccess(user.getPhone(), storeOrder.getOrderId(), storeOrder.getPayPrice());
                    }
                }

                // 发送用户支付成功管理员提醒短信
                String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_ADMIN_PAY_SUCCESS_SWITCH);
                if (StrUtil.isNotBlank(smsSwitch) && smsSwitch.equals("1")) {
                    // 查询可已发送短信的管理员
                    List<SystemAdmin> systemAdminList = systemAdminService.findIsSmsList();
                    if (CollUtil.isNotEmpty(systemAdminList)) {
                        // 发送短信
                        systemAdminList.forEach(admin -> {
                            smsService.sendOrderPaySuccessNotice(admin.getPhone(), storeOrder.getOrderId(), admin.getRealName());
                        });
                    }
                }

                //下发模板通知
                pushMessageOrder(storeOrder, user);

                // 购买成功后根据配置送优惠券
                autoSendCoupons(storeOrder);

            } catch (Exception e) {
                e.printStackTrace();
                logger.error("短信、模板通知或优惠券异常");
            }
        }
        return execute;
    }

    // 支持成功拼团后置处理
    private Boolean pinkProcessing(StoreOrder storeOrder) {
        // 判断拼团是否成功
        StorePink storePink = storePinkService.getById(storeOrder.getPinkId());

        if (storePink.getKId() <= 0) {
            return true;
        }

        List<StorePink> pinkList = storePinkService.getListByCidAndKid(storePink.getCid(), storePink.getKId());
        StorePink tempPink = storePinkService.getById(storePink.getKId());
        pinkList.add(tempPink);
        if (pinkList.size() < storePink.getPeople()) {// 还未拼团成功
            return true;
        }
        // 1.修改拼团状态
        // 2.给所有拼团人员发送拼团成功通知
        pinkList.forEach(e -> {
            e.setStatus(2);
        });
        boolean update = storePinkService.updateBatchById(pinkList);
        if (!update) {
            logger.error("拼团订单支付成功后更新拼团状态失败,orderNo = " + storeOrder.getOrderId());
            return false;
        }
        pinkList.forEach(i -> {
            StoreOrder order = storeOrderService.getByOderId(i.getOrderId());
            StoreCombination storeCombination = storeCombinationService.getById(i.getCid());
            User tempUser = userService.getById(i.getUid());
            // 发送微信模板消息
            MyRecord record = new MyRecord();
            record.set("orderNo", order.getOrderId());
            record.set("proName", storeCombination.getTitle());
            record.set("payType", order.getPayType());
            record.set("isChannel", order.getIsChannel());
            pushMessagePink(record, tempUser);
        });
        return true;
    }

    /**
     * 发送拼团成功通知
     * @param record 信息参数
     * @param user 用户
     */
    private void pushMessagePink(MyRecord record, User user) {
        if (!record.getStr("payType").equals(Constants.PAY_TYPE_WE_CHAT)) {
            return ;
        }
        if (record.getInt("isChannel").equals(2)) {
            return ;
        }

        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        // 公众号
        if (record.getInt("isChannel").equals(Constants.ORDER_PAY_CHANNEL_PUBLIC)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // 发送微信模板消息
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "恭喜您拼团成功！我们将尽快为您发货。");
            temMap.put("keyword1", record.getStr("orderNo"));
            temMap.put("keyword2", record.getStr("proName"));
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "感谢你的使用！");
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_COMBINATION_SUCCESS, temMap, userToken.getToken());
            return;
        }
        // 小程序发送订阅消息
        userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            return ;
        }
        // 组装数据
        temMap.put("character_string1",  record.getStr("orderNo"));
        temMap.put("thing2", record.getStr("proName"));
        temMap.put("thing5", "恭喜您拼团成功！我们将尽快为您发货。");
        templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_COMBINATION_SUCCESS, temMap, userToken.getToken());

    }

    /**
     * 分配佣金
     * @param storeOrder 订单
     * @return List<UserBrokerageRecord>
     */
    private List<UserBrokerageRecord> assignCommission(StoreOrder storeOrder) {
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
        // 获取佣金冻结期
        String fronzenTime = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_EXTRACT_TIME);

        // 生成佣金记录
        List<UserBrokerageRecord> brokerageRecordList = spreadRecordList.stream().map(record -> {
            BigDecimal brokerage = calculateCommission(record, storeOrder.getId());
            UserBrokerageRecord brokerageRecord = new UserBrokerageRecord();
            brokerageRecord.setUid(record.getInt("spreadUid"));
            brokerageRecord.setLinkType(BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
            brokerageRecord.setType(BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD);
            brokerageRecord.setTitle(BrokerageRecordConstants.BROKERAGE_RECORD_TITLE_ORDER);
            brokerageRecord.setPrice(brokerage);
            brokerageRecord.setMark(StrUtil.format("获得推广佣金，分佣{}", brokerage));
            brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_CREATE);
            brokerageRecord.setFrozenTime(Integer.valueOf(Optional.ofNullable(fronzenTime).orElse("0")));
            brokerageRecord.setCreateTime(DateUtil.nowDateTime());
            return brokerageRecord;
        }).collect(Collectors.toList());

        return brokerageRecordList;
    }

    /**
     * 计算佣金
     * @param record index-分销级数，spreadUid-分销人
     * @param orderId 订单id
     * @return BigDecimal
     */
    private BigDecimal calculateCommission(MyRecord record, Integer orderId) {
        BigDecimal brokeragePrice = BigDecimal.ZERO;
        // 查询订单详情
        List<StoreOrderInfoOldVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(orderId);
        if (CollUtil.isEmpty(orderInfoVoList)) {
            return brokeragePrice;
        }
        BigDecimal totalBrokerPrice = BigDecimal.ZERO;
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

        for (StoreOrderInfoOldVo orderInfoVo : orderInfoVoList) {
            // 先看商品是否有固定分佣
            StoreProductAttrValue attrValue = storeProductAttrValueService.getById(orderInfoVo.getInfo().getAttrValueId());
            if (orderInfoVo.getInfo().getIsSub()) {// 有固定分佣
                if(index == 1){
                    brokeragePrice = Optional.ofNullable(attrValue.getBrokerage()).orElse(BigDecimal.ZERO);
                }
                if(index == 2){
                    brokeragePrice = Optional.ofNullable(attrValue.getBrokerageTwo()).orElse(BigDecimal.ZERO);
                }
            } else {// 系统分佣
                if(!rateBigDecimal.equals(BigDecimal.ZERO)){
                    // 商品没有分销金额, 并且有设置对应等级的分佣比例
                    // 舍入模式向零舍入。
                    brokeragePrice = orderInfoVo.getInfo().getPrice().multiply(rateBigDecimal).setScale(2, BigDecimal.ROUND_DOWN);
                } else {
                    brokeragePrice = BigDecimal.ZERO;
                }
            }
            // 同规格商品可能有多件
            if (brokeragePrice.compareTo(BigDecimal.ZERO) > 0 && orderInfoVo.getInfo().getPayNum() > 1) {
                brokeragePrice = brokeragePrice.multiply(new BigDecimal(orderInfoVo.getInfo().getPayNum()));
            }
            totalBrokerPrice = totalBrokerPrice.add(brokeragePrice);
        }

        return totalBrokerPrice;
    }

    /**
     * 获取参与分佣人员（两级）
     * @param spreadUid 一级分佣人Uid
     * @return List<MyRecord>
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
        recordList.add(firstRecord);

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
        recordList.add(secondRecord);
        return recordList;
    }

    /**
     * 余额支付
     * @param storeOrder 订单
     * @return Boolean Boolean
     */
    @Override
    public Boolean yuePay(StoreOrder storeOrder) {

        // 用户余额扣除
        User user = userService.getById(storeOrder.getUid());
        if (ObjectUtil.isNull(user)) throw new CrmebException("用户不存在");
        if (user.getNowMoney().compareTo(storeOrder.getPayPrice()) < 0) {
            throw new CrmebException("用户余额不足");
        }
        if (user.getIntegral() < storeOrder.getUseIntegral()) {
            throw new CrmebException("用户积分不足");
        }
        storeOrder.setPaid(true);
        storeOrder.setPayTime(DateUtil.nowDateTime());
        Boolean execute = transactionTemplate.execute(e -> {
            // 订单修改
            storeOrderService.updateById(storeOrder);
            // 这里只扣除金额，账单记录在task中处理
            userService.updateNowMoney(user, storeOrder.getPayPrice(), "sub");
            // 扣除积分
            if (storeOrder.getUseIntegral() > 0) {
                userService.updateIntegral(user, storeOrder.getUseIntegral(), "sub");
            }
            // 添加支付成功redis队列
            redisUtil.lPush(Constants.ORDER_TASK_PAY_SUCCESS_AFTER, storeOrder.getOrderId());

            // 处理拼团
            if (storeOrder.getCombinationId() > 0) {
                // 判断拼团团长是否存在
                StorePink headPink = new StorePink();
                Integer pinkId = storeOrder.getPinkId();
                if (pinkId > 0) {
                    headPink = storePinkService.getById(pinkId);
                    if (ObjectUtil.isNull(headPink) || headPink.getIsRefund().equals(true) || headPink.getStatus() == 3) {
                        pinkId = 0;
                    }
                }
                StoreCombination storeCombination = storeCombinationService.getById(storeOrder.getCombinationId());
                // 如果拼团人数已满，重新开团
                if (pinkId > 0) {
                    Integer count = storePinkService.getCountByKid(pinkId);
                    if (count >= storeCombination.getPeople()) {
                        pinkId = 0;
                    }
                }
                // 生成拼团表数据
                StorePink storePink = new StorePink();
                storePink.setUid(user.getUid());
                storePink.setAvatar(user.getAvatar());
                storePink.setNickname(user.getNickname());
                storePink.setOrderId(storeOrder.getOrderId());
                storePink.setOrderIdKey(storeOrder.getId());
                storePink.setTotalNum(storeOrder.getTotalNum());
                storePink.setTotalPrice(storeOrder.getTotalPrice());
                storePink.setCid(storeCombination.getId());
                storePink.setPid(storeCombination.getProductId());
                storePink.setPeople(storeCombination.getPeople());
                storePink.setPrice(storeCombination.getPrice());
                Integer effectiveTime = storeCombination.getEffectiveTime();// 有效小时数
                DateTime dateTime = cn.hutool.core.date.DateUtil.date();
                storePink.setAddTime(dateTime.getTime());
                if (pinkId > 0) {
                    storePink.setStopTime(headPink.getStopTime());
                } else {
                    DateTime hourTime = cn.hutool.core.date.DateUtil.offsetHour(dateTime, effectiveTime);
                    long stopTime =  hourTime.getTime();
                    if (stopTime > storeCombination.getStopTime()) {
                        stopTime = storeCombination.getStopTime();
                    }
                    storePink.setStopTime(stopTime);
                }
                storePink.setKId(pinkId);
                storePink.setIsTpl(false);
                storePink.setIsRefund(false);
                storePink.setStatus(1);
                storePinkService.save(storePink);
                // 如果是开团，需要更新订单数据
                storeOrder.setPinkId(storePink.getId());
                storeOrderService.updateById(storeOrder);
            }

            return Boolean.TRUE;
        });
        if (!execute) throw new CrmebException("余额支付订单失败");
        return execute;
    }

    /**
     * 订单支付
     * @param orderPayRequest   支付参数
     * @param ip                ip
     * @return OrderPayResultResponse
     * 1.微信支付拉起微信预支付，返回前端调用微信支付参数，在之后需要调用微信支付查询接口
     * 2.余额支付，更改对应信息后，加入支付成功处理task
     */
    @Override
    public OrderPayResultResponse payment(OrderPayRequest orderPayRequest, String ip) {
        StoreOrder storeOrder = storeOrderService.getByOderId(orderPayRequest.getOrderNo());
        if (ObjectUtil.isNull(storeOrder)) {
            throw new CrmebException("订单不存在");
        }
        if (storeOrder.getIsDel()) {
            throw new CrmebException("订单已被删除");
        }
        if (storeOrder.getPaid()) {
            throw new CrmebException("订单已支付");
        }
        User user = userService.getById(storeOrder.getUid());
        if (ObjectUtil.isNull(user)) throw new CrmebException("用户不存在");

        // 判断订单是否还是之前的支付类型
        if (!storeOrder.getPayType().equals(orderPayRequest.getPayType())) {
            // 根据支付类型进行校验,更换支付类型
            storeOrder.setPayType(orderPayRequest.getPayType());
            // 余额支付
            if (orderPayRequest.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
                if (user.getNowMoney().compareTo(storeOrder.getPayPrice()) < 0) {
                    throw new CrmebException("用户余额不足");
                }
                storeOrder.setIsChannel(3);
            }
            if (orderPayRequest.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
                switch (orderPayRequest.getPayChannel()){
                    case PayConstants.PAY_CHANNEL_WE_CHAT_H5:// H5
                        storeOrder.setIsChannel(2);
                        break;
                    case PayConstants.PAY_CHANNEL_WE_CHAT_PUBLIC:// 公众号
                        storeOrder.setIsChannel(0);
                        break;
                    case PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM:// 小程序
                        storeOrder.setIsChannel(1);
                        break;
                }
            }

            boolean changePayType = storeOrderService.updateById(storeOrder);
            if (!changePayType) {
                throw new CrmebException("变更订单支付类型失败!");
            }
        }

        if (user.getIntegral() < storeOrder.getUseIntegral()) {
            throw new CrmebException("用户积分不足");
        }

        OrderPayResultResponse response = new OrderPayResultResponse();
        response.setOrderNo(storeOrder.getOrderId());
        response.setPayType(storeOrder.getPayType());
        // 0元付
        if (storeOrder.getPayPrice().compareTo(BigDecimal.ZERO) <= 0) {
            Boolean aBoolean = yuePay(storeOrder);
            response.setPayType(PayConstants.PAY_TYPE_YUE);
            response.setStatus(aBoolean);
            return response;
        }

        // 微信支付，调用微信预下单，返回拉起微信支付需要的信息
        if (storeOrder.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            Map<String, String> unifiedorder = weChatPayService.unifiedorder(storeOrder, ip);
            response.setStatus(true);
            WxPayJsResultVo vo = new WxPayJsResultVo();
            vo.setAppId(unifiedorder.get("appId"));
            vo.setNonceStr(unifiedorder.get("nonceStr"));
            vo.setPackages(unifiedorder.get("package"));
            vo.setSignType(unifiedorder.get("signType"));
            vo.setTimeStamp(unifiedorder.get("timeStamp"));
            vo.setPaySign(unifiedorder.get("paySign"));
            if (storeOrder.getIsChannel() == 2) {
                vo.setMwebUrl(unifiedorder.get("mweb_url"));
                response.setPayType(PayConstants.PAY_CHANNEL_WE_CHAT_H5);
            }
            response.setJsConfig(vo);
            return response;
        }
        // 余额支付
        if (storeOrder.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
            Boolean yueBoolean = yuePay(storeOrder);
            response.setStatus(yueBoolean);
            return response;
        }
        if (storeOrder.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY)) {
            throw new CrmebException("暂时不支持支付宝支付");
        }
        if (storeOrder.getPayType().equals(PayConstants.PAY_TYPE_OFFLINE)) {
            throw new CrmebException("暂时不支持线下支付");
        }
        response.setStatus(false);
        return response;
    }


    private UserIntegralRecord integralRecordSubInit(StoreOrder storeOrder, User user) {
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(storeOrder.getUid());
        integralRecord.setLinkId(storeOrder.getOrderId());
        integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB);
        integralRecord.setTitle(IntegralRecordConstants.BROKERAGE_RECORD_TITLE_ORDER);
        integralRecord.setIntegral(storeOrder.getUseIntegral());
        integralRecord.setBalance(user.getIntegral());
        integralRecord.setMark(StrUtil.format("订单支付抵扣{}积分购买商品", storeOrder.getUseIntegral()));
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        return integralRecord;
    }

    private UserBill userBillInit(StoreOrder order, User user) {
        UserBill userBill = new UserBill();
        userBill.setPm(0);
        userBill.setUid(order.getUid());
        userBill.setLinkId(order.getId().toString());
        userBill.setTitle("购买商品");
        userBill.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
        userBill.setType(Constants.USER_BILL_TYPE_PAY_ORDER);
        userBill.setNumber(order.getPayPrice());
        userBill.setBalance(user.getNowMoney());
        userBill.setMark("支付" + order.getPayPrice() + "元购买商品");
        return userBill;
    }

    /**
     * 经验添加记录
     */
    private UserBill experienceBillInit(StoreOrder storeOrder, Integer balance, Integer experience) {
        UserBill userBill = new UserBill();
        userBill.setPm(1);
        userBill.setUid(storeOrder.getUid());
        userBill.setLinkId(storeOrder.getId().toString());
        userBill.setTitle(Constants.ORDER_LOG_MESSAGE_PAY_SUCCESS);
        userBill.setCategory(Constants.USER_BILL_CATEGORY_EXPERIENCE);
        userBill.setType(Constants.USER_BILL_TYPE_PAY_ORDER);
        userBill.setNumber(new BigDecimal(experience));
        userBill.setBalance(new BigDecimal(balance));
        userBill.setMark("用户付款成功增加" + experience + "经验");
        return userBill;
    }

    /**
     * 积分添加记录
     * @return UserIntegralRecord
     */
    private UserIntegralRecord integralRecordInit(StoreOrder storeOrder, Integer balance, Integer integral, String type) {
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(storeOrder.getUid());
        integralRecord.setLinkId(storeOrder.getOrderId());
        integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        integralRecord.setTitle(IntegralRecordConstants.BROKERAGE_RECORD_TITLE_ORDER);
        integralRecord.setIntegral(integral);
        integralRecord.setBalance(balance);
        if (type.equals("order")){
            integralRecord.setMark(StrUtil.format("用户付款成功,订单增加{}积分", integral));
        }
        if (type.equals("product")) {
            integralRecord.setMark(StrUtil.format("用户付款成功,商品增加{}积分", integral));
        }
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_CREATE);
        // 获取积分冻结期
        String fronzenTime = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_INTEGRAL_EXTRACT_TIME);
        integralRecord.setFrozenTime(Integer.valueOf(Optional.ofNullable(fronzenTime).orElse("0")));
        integralRecord.setCreateTime(DateUtil.nowDateTime());
        return integralRecord;
    }

    /**
     * 发送消息通知
     * 根据用户类型发送
     * 公众号模板消息
     * 小程序订阅消息
     */
    private void pushMessageOrder(StoreOrder storeOrder, User user) {
        if (storeOrder.getIsChannel().equals(2)) {// H5
            return;
        }
        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        if (!storeOrder.getPayType().equals(Constants.PAY_TYPE_WE_CHAT)) {
            return;
        }
        // 公众号
        if (storeOrder.getIsChannel().equals(Constants.ORDER_PAY_CHANNEL_PUBLIC)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // 发送微信模板消息
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "您的订单已支付成功！");
            temMap.put("keyword1", storeOrder.getOrderId());
            temMap.put("keyword2", storeOrder.getPayPrice().toString());
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "欢迎下次再来！");
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_ORDER_PAY, temMap, userToken.getToken());
            return;
        }
        if (storeOrder.getIsChannel().equals(Constants.ORDER_PAY_CHANNEL_PROGRAM)) {
            // 小程序发送订阅消息
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // 组装数据
            temMap.put("character_string1", storeOrder.getOrderId());
            temMap.put("amount2", storeOrder.getPayPrice().toString() + "元");
            temMap.put("thing7", "您的订单已支付成功");
            templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_ORDER_PAY, temMap, userToken.getToken());
        }
    }

    /**
     * 商品购买后根据配置送券
     */
    private void autoSendCoupons(StoreOrder storeOrder){
        // 根据订单详情获取商品信息
        List<StoreOrderInfoOldVo> orders = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
        if(null == orders){
            return;
        }
        List<StoreCouponUser> couponUserList = CollUtil.newArrayList();
        Map<Integer, Boolean> couponMap = CollUtil.newHashMap();
        for (StoreOrderInfoOldVo order : orders) {
            List<StoreProductCoupon> couponsForGiveUser = storeProductCouponService.getListByProductId(order.getProductId());
            for (int i = 0; i < couponsForGiveUser.size();) {
                StoreProductCoupon storeProductCoupon = couponsForGiveUser.get(i);
                MyRecord record = storeCouponUserService.paySuccessGiveAway(storeProductCoupon.getIssueCouponId(), storeOrder.getUid());
                if (record.getStr("status").equals("fail")) {
                    logger.error(StrUtil.format("支付成功领取优惠券失败，失败原因：{}", record.getStr("errMsg")));
                    couponsForGiveUser.remove(i);
                    continue;
                }

                StoreCouponUser storeCouponUser = record.get("storeCouponUser");
                couponUserList.add(storeCouponUser);
                couponMap.put(storeCouponUser.getCouponId(), record.getBoolean("isLimited"));
                i++;
            }
        }

        Boolean execute = transactionTemplate.execute(e -> {
            if (CollUtil.isNotEmpty(couponUserList)) {
                storeCouponUserService.saveBatch(couponUserList);
                couponUserList.forEach(i -> storeCouponService.deduction(i.getCouponId(), 1, couponMap.get(i.getCouponId())));
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("支付成功领取优惠券，更新数据库失败，订单编号：{}", storeOrder.getOrderId()));
        }
    }
}
