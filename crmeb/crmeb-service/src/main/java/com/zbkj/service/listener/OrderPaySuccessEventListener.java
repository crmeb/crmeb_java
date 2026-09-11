package com.zbkj.service.listener;

import com.zbkj.service.event.OrderPaySuccessEvent;
import com.zbkj.service.service.OrderTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 订单支付成功即时处理监听器
 */
@Component
public class OrderPaySuccessEventListener {

    @Autowired
    private OrderTaskService orderTaskService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handle(OrderPaySuccessEvent event) {
        orderTaskService.orderPaySuccessAfter(event.getOrderNo());
    }
}
