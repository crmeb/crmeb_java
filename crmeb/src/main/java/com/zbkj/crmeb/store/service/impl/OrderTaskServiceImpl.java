package com.zbkj.crmeb.store.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.constants.Constants;
import com.utils.RedisUtil;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.service.OrderTaskService;
import com.zbkj.crmeb.store.service.StoreOrderTaskService;
import com.zbkj.crmeb.task.order.OrderRefundByUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author Mr.Zhang
* @description StoreOrderServiceImpl 接口实现
* @date 2020-05-28
*/
@Service
public class OrderTaskServiceImpl implements OrderTaskService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderRefundByUser.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StoreOrderTaskService storeOrderTaskService;

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
                StoreOrder storeOrder = getJavaBeanStoreOrder(data);
                boolean result = storeOrderTaskService.cancelByUser(storeOrder);
                if(!result){
                    redisUtil.lPush(redisKey, data);
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
     * 用户退款申请
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
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }
            try{
                StoreOrder storeOrder = getJavaBeanStoreOrder(data);
                boolean result = storeOrderTaskService.refundApply(storeOrder);
                if(!result){
                    redisUtil.lPush(redisKey, data);
                }
            }catch (Exception e){
                redisUtil.lPush(redisKey, data);
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
     * 用户完成订单
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
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }
            try{
                StoreOrder storeOrder = getJavaBeanStoreOrder(data);
                boolean result = storeOrderTaskService.takeByUser(storeOrder);
                if(!result){
                    redisUtil.lPush(redisKey, data);
                }
            }catch (Exception e){
                redisUtil.lPush(redisKey, data);
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