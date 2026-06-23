package com.zbkj.service.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.constants.TaskConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.order.StoreOrderInfo;
import com.zbkj.common.model.order.StoreOrderStatus;
import com.zbkj.common.model.product.StoreProductReply;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.wechat.video.PayComponentOrder;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.ShopOrderCommonVo;
import com.zbkj.common.vo.StoreOrderInfoOldVo;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * StoreOrderServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class OrderTaskServiceImpl implements OrderTaskService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderTaskServiceImpl.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StoreOrderTaskService storeOrderTaskService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderStatusService storeOrderStatusService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductReplyService storeProductReplyService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OrderPayService orderPayService;

    @Autowired
    private PayComponentOrderService componentOrderService;

    @Autowired
    private WechatVideoDeliveryService wechatVideoDeliveryService;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 用户取消订单
     * @author Mr.Zhang
     * @since 2020-07-09
     */
    @Override
    public void cancelByUser() {
        String redisKey = Constants.ORDER_TASK_REDIS_KEY_AFTER_CANCEL_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.cancelByUser | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (null == data) {
                continue;
            }
            try {
//                StoreOrder storeOrder = getJavaBeanStoreOrder(data);
                StoreOrder storeOrder = storeOrderService.getById(Integer.valueOf(data.toString()));
                boolean result = storeOrderTaskService.cancelByUser(storeOrder);
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    private StoreOrder getJavaBeanStoreOrder(Object data) {
        return JSONObject.toJavaObject(JSONObject.parseObject(data.toString()), StoreOrder.class);
    }

    /**
     * 执行 用户退款申请
     * @author Mr.Zhang
     * @since 2020-07-09
     */
    @Override
    public void refundApply() {
        String redisKey = Constants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.refundApply | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object orderId = redisUtil.getRightPop(redisKey, 10L);
            if (null == orderId) {
                continue;
            }
            try {
                StoreOrder storeOrder = storeOrderService.getById(Integer.valueOf(orderId.toString()));
                if (ObjectUtil.isNull(storeOrder)) {
                    throw new CrmebException("订单不存在,orderNo = " + orderId);
                }
//                boolean result = storeOrderTaskService.refundApply(storeOrder);
                boolean result = storeOrderTaskService.refundOrder(storeOrder);
                if (!result) {
                    logger.error("订单退款错误：result = " + result);
                    redisUtil.lPush(redisKey, orderId);
                }
            } catch (Exception e) {
                logger.error("订单退款错误：" + e.getMessage());
                redisUtil.lPush(redisKey, orderId);
            }
        }
    }

    /**
     * 完成订单
     * @author Mr.Zhang
     * @since 2020-07-09
     */
    @Override
    public void complete() {
        String redisKey = Constants.ORDER_TASK_REDIS_KEY_AFTER_COMPLETE_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.complete | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (null == data) {
                continue;
            }
            try {
                StoreOrder storeOrder = storeOrderService.getById(Integer.valueOf(data.toString()));
                if (ObjectUtil.isNull(storeOrder)) {
                    logger.error("OrderTaskServiceImpl.orderPaySuccessAfter | 订单不存在，orderId: " + data);
                    throw new CrmebException("订单不存在，orderId: " + data);
                }
                boolean result = storeOrderTaskService.complete(storeOrder);
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 订单支付成功后置处理
     */
    @Override
    public void orderPaySuccessAfter() {
        String redisKey = TaskConstants.ORDER_TASK_PAY_SUCCESS_AFTER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.orderPaySuccessAfter | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(data)) {
                continue;
            }
            try {
                StoreOrder storeOrder = storeOrderService.getByOderId(String.valueOf(data));
                if (ObjectUtil.isNull(storeOrder)) {
                    logger.error("OrderTaskServiceImpl.orderPaySuccessAfter | 订单不存在，orderNo: " + data);
                    throw new CrmebException("订单不存在，orderNo: " + data);
                }
                boolean result = orderPayService.paySuccess(storeOrder);
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 自动取消未支付订单
     */
    @Override
    public void autoCancel() {
        String redisKey = Constants.ORDER_AUTO_CANCEL_KEY;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.autoCancel | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (null == data) {
                continue;
            }
            try {
                StoreOrder storeOrder = storeOrderService.getByOderId(String.valueOf(data));
                if (ObjectUtil.isNull(storeOrder)) {
                    logger.error("OrderTaskServiceImpl.autoCancel | 订单不存在，orderNo: " + data);
                    throw new CrmebException("订单不存在，orderNo: " + data);
                }
                boolean result = storeOrderTaskService.autoCancel(storeOrder);
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 订单收货
     */
    @Override
    public void orderReceiving() {
        String redisKey = TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.orderReceiving | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object id = redisUtil.getRightPop(redisKey, 10L);
            if (null == id) {
                continue;
            }
            try {
                Boolean result = storeOrderTaskService.orderReceiving(Integer.valueOf(id.toString()));
                if (!result) {
                    redisUtil.lPush(redisKey, id);
                }
            } catch (Exception e) {
                redisUtil.lPush(redisKey, id);
            }
        }
    }

    /**
     * 订单自动完成
     */
    @Override
    public void autoComplete() {
        // 查找所有收获状态订单
        List<StoreOrder> orderList = storeOrderService.findIdAndUidListByReceipt();
        if (CollUtil.isEmpty(orderList)) {
            return ;
        }
        logger.info("OrderTaskServiceImpl.autoComplete | size:0");

        // 根据订单状态表判断订单是否可以自动完成
        for (StoreOrder order : orderList) {
            StoreOrderStatus orderStatus = storeOrderStatusService.getLastByOrderId(order.getId());
            if (!orderStatus.getChangeType().equals("user_take_delivery")) {
                logger.error("订单自动完成：订单记录最后一条不是收货状态，orderId = " + order.getId());
                continue ;
            }
            // 获取默认好评开关、时间、内容；
            ArrayList<String> keyList = new ArrayList<>();
            keyList.add(SysConfigConstants.CONFIG_AUTO_REPLY_SWITCH);
            keyList.add(SysConfigConstants.CONFIG_AUTO_REPLY_TIME);
            keyList.add(SysConfigConstants.CONFIG_AUTO_REPLY_CONTENT);
            MyRecord record = systemConfigService.getValuesByKeyList(keyList);
            if (record.get(SysConfigConstants.CONFIG_AUTO_REPLY_SWITCH).equals("'0'")) {
                logger.error("默认好评开关关闭");
                return;
            }
            String content = record.get(SysConfigConstants.CONFIG_AUTO_REPLY_CONTENT).toString();
            int time = Integer.parseInt(record.get(SysConfigConstants.CONFIG_AUTO_REPLY_TIME));
            // 判断是否到自动完成时间
            String comTime = CrmebDateUtil.addDay(orderStatus.getCreateTime(), time, Constants.DATE_FORMAT);
            int compareDate = CrmebDateUtil.compareDate(comTime, CrmebDateUtil.nowDateTime(Constants.DATE_FORMAT), Constants.DATE_FORMAT);
            if (compareDate > 0) {
                continue ;
            }

            /**
             * ---------------
             * 自动好评转完成
             * ---------------
             */
            // 获取订单详情
            List<StoreOrderInfoOldVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(order.getId());
            if (CollUtil.isEmpty(orderInfoVoList)) {
                logger.error("订单自动完成：无订单详情数据，orderId = " + order.getId());
                continue;
            }
            List<StoreProductReply> replyList = CollUtil.newArrayList();
            List<Integer> orderInfoIds = CollUtil.newArrayList();
            User user = userService.getById(order.getUid());
            // 生成评论
            for (StoreOrderInfoOldVo orderInfo : orderInfoVoList) {
                // 判断是否已评论
                if (orderInfo.getInfo().getIsReply().equals(1)) {
                    continue;
                }
                String replyType = Constants.STORE_REPLY_TYPE_PRODUCT;
//                if (ObjectUtil.isNotNull(orderInfo.getInfo().getSeckillId()) && orderInfo.getInfo().getSeckillId() > 0) {
//                    replyType = Constants.STORE_REPLY_TYPE_SECKILL;
//                }
//                if (ObjectUtil.isNotNull(orderInfo.getInfo().getBargainId()) && orderInfo.getInfo().getBargainId() > 0) {
//                    replyType = Constants.STORE_REPLY_TYPE_BARGAIN;
//                }
//                if (ObjectUtil.isNotNull(orderInfo.getInfo().getCombinationId()) && orderInfo.getInfo().getCombinationId() > 0) {
//                    replyType = Constants.STORE_REPLY_TYPE_PINTUAN;
//                }
                StoreProductReply reply = new StoreProductReply();
                reply.setUid(order.getUid());
                reply.setOid(order.getId());
                reply.setProductId(orderInfo.getProductId());
                reply.setUnique(orderInfo.getUnique());
                reply.setReplyType(replyType);
                reply.setProductScore(5);
                reply.setServiceScore(5);
                reply.setComment(content);
                reply.setPics("");
                reply.setNickname(user.getNickname());
                reply.setAvatar(user.getAvatar());
                reply.setSku(orderInfo.getInfo().getSku());
                reply.setCreateTime(CrmebDateUtil.nowDateTime());
                replyList.add(reply);
                orderInfoIds.add(orderInfo.getId());
            }
            order.setStatus(Constants.ORDER_STATUS_INT_COMPLETE);

            LambdaUpdateWrapper<StoreOrderInfo> update = Wrappers.lambdaUpdate();
            update.in(StoreOrderInfo::getId, orderInfoIds);
            update.set(StoreOrderInfo::getIsReply, 1);
            Boolean execute = transactionTemplate.execute(e -> {
                System.out.println("操作的订单ID：" + order.getId());
                order.setUpdateTime(DateUtil.date());
                storeOrderService.updateById(order);
                storeProductReplyService.saveBatch(replyList);
                storeOrderInfoService.update(update);
                return Boolean.TRUE;
            });
            if (execute) {
                redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_COMPLETE_BY_USER, order.getId());
            } else {
                logger.error("订单自动完成：更新数据库失败，orderId = " + order.getId());
            }
        }
    }

    /**
     * 订单自动收货
     */
    @Override
    public void autoTakeDelivery() {
        int day = 14;
        String autoDay = systemConfigService.getValueByKey("auto_take_delivery_day");
        if (StrUtil.isNotBlank(autoDay) && Integer.parseInt(autoDay) >= 1) {
            day = Integer.parseInt(autoDay);
        }
        DateTime dateTime = cn.hutool.core.date.DateUtil.offsetDay(cn.hutool.core.date.DateUtil.date(), -day);
        List<StoreOrder> storeOrderList = storeOrderService.findAwaitTakeDeliveryOrderList(dateTime.toString());

        storeOrderList.forEach(order -> {
            try {
                if (order.getType().equals(1)) {// 视频号订单
                    PayComponentOrder componentOrder = componentOrderService.getByOrderNo(order.getOrderId());
                    if (ObjectUtil.isNull(componentOrder)) {
                        throw new CrmebException("没有找到视频号订单");
                    }
                    if (!componentOrder.getStatus().equals(30)) {
                        throw new CrmebException("视频号订单不是待收货状态");
                    }
                    // 微信组件部分收货
                    ShopOrderCommonVo shopOrderCommonVo = new ShopOrderCommonVo();
                    shopOrderCommonVo.setOutOrderId(componentOrder.getOrderNo());
                    shopOrderCommonVo.setOpenid(componentOrder.getOpenid());
                    Boolean recieve = wechatVideoDeliveryService.shopDeliveryRecieve(shopOrderCommonVo);
                    if (!recieve) {
                        throw new CrmebException("自定义组件订单收货失败");
                    }
                    componentOrder.setStatus(100);
                    componentOrderService.updateById(componentOrder);
                }
                //已收货，待评价
                order.setStatus(Constants.ORDER_STATUS_INT_BARGAIN);
                order.setUpdateTime(DateUtil.date());
                boolean result = storeOrderService.updateById(order);
                if (result) {
                    //后续操作放入redis
                    redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER, order.getId());
                }
            } catch (Exception e) {
                logger.error("自动收货异常：订单号:{},异常信息={}", order.getOrderId(), e.getMessage());
            }
        });

    }
}
