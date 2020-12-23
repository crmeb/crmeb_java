package com.zbkj.crmeb.store.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.constants.Constants;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.service.OrderTaskService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.store.service.StoreOrderTaskService;
import com.zbkj.crmeb.store.utilService.OrderUtils;
import com.zbkj.crmeb.task.order.OrderRefundByUser;
import com.zbkj.crmeb.wechat.service.impl.WechatSendMessageForMinService;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForGetPackage;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForOrderCancel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * StoreOrderServiceImpl 接口实现
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
public class OrderTaskServiceImpl implements OrderTaskService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderRefundByUser.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StoreOrderTaskService storeOrderTaskService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private WechatSendMessageForMinService wechatSendMessageForMinService;

    @Autowired
    private OrderUtils orderUtils;

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
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }
            try{
//                StoreOrder storeOrder = getJavaBeanStoreOrder(data);
                StoreOrder storeOrder = storeOrderService.getById(Integer.valueOf(data.toString()));
                boolean result = storeOrderTaskService.cancelByUser(storeOrder);
                if(!result){
                    redisUtil.lPush(redisKey, data);
                }else{
                    WechatSendMessageForOrderCancel orderCancel = new WechatSendMessageForOrderCancel(
                            "暂无",DateUtil.nowDateTimeStr(),"","暂无",orderUtils.getPayTypeStrByOrder(storeOrder),
                            orderUtils.getStoreNameAndCarNumString(storeOrder.getId()),storeOrder.getOrderId(),
                            storeOrder.getStatus()+"",storeOrder.getPayPrice()+"",storeOrder.getCreateTime()+"",
                            "CRMEB","暂无",storeOrder.getOrderId(),"CRMEB","暂无"
                    );
                    wechatSendMessageForMinService.sendOrderCancelMessage(orderCancel,storeOrder.getUid());
                }
            }catch (Exception e){
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
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object orderId = redisUtil.getRightPop(redisKey, 10L);
            if(null == orderId){
                continue;
            }
            try{
                StoreOrder storeOrder = storeOrderService.getById(Integer.valueOf(orderId.toString()));
                boolean result = storeOrderTaskService.refundApply(storeOrder);
                if(!result){
                    redisUtil.lPush(redisKey, orderId);
                }
            }catch (Exception e){
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
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }
            try{
                StoreOrder storeOrder = getJavaBeanStoreOrder(data);
                boolean result = storeOrderTaskService.complete(storeOrder);
                if(!result){
                    redisUtil.lPush(redisKey, data);
                }
            }catch (Exception e){
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 用户已收货
     * @author Mr.Zhang
     * @since 2020-07-09
     */
    @Override
    public void takeByUser() {
        String redisKey = Constants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.takeByUser | size:" + size);
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object id = redisUtil.getRightPop(redisKey, 10L);
            if(null == id){
                continue;
            }
            try{
                StoreOrder storeOrder = storeOrderService.getByEntityOne(new StoreOrder().setId(Integer.valueOf(id.toString())));
                boolean result = storeOrderTaskService.takeByUser(storeOrder);
                if(!result){
                    redisUtil.lPush(redisKey, id);
                }else{
                    // 微信小程序订阅消息通知 确认收货
                    WechatSendMessageForGetPackage getPackage = new WechatSendMessageForGetPackage(
                            orderUtils.getPayTypeStrByOrder(storeOrder),orderUtils.getStoreNameAndCarNumString(storeOrder.getId()),
                            "CRMEB",storeOrder.getUserAddress(), DateUtil.nowDateTimeStr(),storeOrder.getOrderId()
                    );
                    wechatSendMessageForMinService.sendGetPackageMessage(getPackage, storeOrder.getUid());
                }
            }catch (Exception e){
                redisUtil.lPush(redisKey, id);
            }
        }
    }

    /**
     * 用户删除订单
     * @author Mr.Zhang
     * @since 2020-07-09
     */
    @Override
    public void deleteByUser() {
        String redisKey = Constants.ORDER_TASK_REDIS_KEY_AFTER_DELETE_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.deleteByUser | size:" + size);
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }
            try{
                StoreOrder storeOrder = getJavaBeanStoreOrder(data);
                boolean result = storeOrderTaskService.deleteByUser(storeOrder);
                if(!result){
                    redisUtil.lPush(redisKey, data);
                }
            }catch (Exception e){
                redisUtil.lPush(redisKey, data);
            }
        }
    }
}
