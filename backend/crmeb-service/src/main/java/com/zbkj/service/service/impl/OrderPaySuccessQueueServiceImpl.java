package com.zbkj.service.service.impl;

import com.zbkj.common.constants.TaskConstants;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.service.event.OrderPaySuccessEvent;
import com.zbkj.service.service.OrderPaySuccessQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 订单支付成功后置任务队列服务实现
 */
@Service
public class OrderPaySuccessQueueServiceImpl implements OrderPaySuccessQueueService {

    private static final Logger logger = LoggerFactory.getLogger(OrderPaySuccessQueueServiceImpl.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Boolean enqueue(String orderNo) {
        boolean queued = redisUtil.lPush(TaskConstants.ORDER_TASK_PAY_SUCCESS_AFTER, orderNo);
        if (!queued) {
            logger.error("订单支付成功后置任务入队失败，订单编号：{}", orderNo);
            return Boolean.FALSE;
        }
        eventPublisher.publishEvent(new OrderPaySuccessEvent(orderNo));
        return Boolean.TRUE;
    }
}
