package com.zbkj.service.service.impl;

import com.zbkj.common.constants.TaskConstants;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.service.event.OrderPaySuccessEvent;
import com.zbkj.service.service.OrderPayService;
import com.zbkj.service.service.StoreOrderService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class OrderPaySuccessImmediateProcessTest {

    @Test
    public void enqueuePublishesImmediateProcessEvent() {
        AtomicReference<Object> publishedEvent = new AtomicReference<>();
        RedisUtil redisUtil = new RedisUtil(null, null) {
            @Override
            public boolean lPush(String key, Object value) {
                return TaskConstants.ORDER_TASK_PAY_SUCCESS_AFTER.equals(key)
                        && "order-test".equals(value);
            }
        };
        ApplicationEventPublisher eventPublisher = publishedEvent::set;

        OrderPaySuccessQueueServiceImpl service = new OrderPaySuccessQueueServiceImpl();
        ReflectionTestUtils.setField(service, "redisUtil", redisUtil);
        ReflectionTestUtils.setField(service, "eventPublisher", eventPublisher);

        Assert.assertTrue(service.enqueue("order-test"));
        Assert.assertTrue(publishedEvent.get() instanceof OrderPaySuccessEvent);
        Assert.assertEquals("order-test", ((OrderPaySuccessEvent) publishedEvent.get()).getOrderNo());
    }

    @Test
    public void immediateProcessClaimsQueueItemBeforeHandlingOrder() {
        StoreOrder order = new StoreOrder();
        order.setOrderId("order-test");

        AtomicBoolean processed = new AtomicBoolean(false);
        AtomicBoolean requeued = new AtomicBoolean(false);
        RedisUtil redisUtil = new RedisUtil(null, null) {
            @Override
            public Long lRemove(String key, long count, Object value) {
                return 1L;
            }

            @Override
            public boolean lPush(String key, Object value) {
                requeued.set(true);
                return true;
            }
        };
        StoreOrderService storeOrderService = proxy(StoreOrderService.class,
                (method, args) -> "getByOderId".equals(method) ? order : null);
        OrderPayService orderPayService = proxy(OrderPayService.class, (method, args) -> {
            if ("paySuccess".equals(method)) {
                processed.set(true);
                return Boolean.TRUE;
            }
            return null;
        });

        OrderTaskServiceImpl service = new OrderTaskServiceImpl();
        ReflectionTestUtils.setField(service, "redisUtil", redisUtil);
        ReflectionTestUtils.setField(service, "storeOrderService", storeOrderService);
        ReflectionTestUtils.setField(service, "orderPayService", orderPayService);

        service.orderPaySuccessAfter(order.getOrderId());

        Assert.assertTrue(processed.get());
        Assert.assertFalse(requeued.get());
    }

    @Test
    public void failedImmediateProcessReturnsOrderToFallbackQueue() {
        StoreOrder order = new StoreOrder();
        order.setOrderId("order-test");

        AtomicBoolean requeued = new AtomicBoolean(false);
        RedisUtil redisUtil = new RedisUtil(null, null) {
            @Override
            public Long lRemove(String key, long count, Object value) {
                return 1L;
            }

            @Override
            public boolean lPush(String key, Object value) {
                requeued.set(true);
                return true;
            }
        };
        StoreOrderService storeOrderService = proxy(StoreOrderService.class,
                (method, args) -> "getByOderId".equals(method) ? order : null);
        OrderPayService orderPayService = proxy(OrderPayService.class,
                (method, args) -> "paySuccess".equals(method) ? Boolean.FALSE : null);

        OrderTaskServiceImpl service = new OrderTaskServiceImpl();
        ReflectionTestUtils.setField(service, "redisUtil", redisUtil);
        ReflectionTestUtils.setField(service, "storeOrderService", storeOrderService);
        ReflectionTestUtils.setField(service, "orderPayService", orderPayService);

        service.orderPaySuccessAfter(order.getOrderId());

        Assert.assertTrue(requeued.get());
    }

    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> type, Invocation invocation) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (proxy, method, args) -> invocation.invoke(method.getName(), args));
    }

    private interface Invocation {
        Object invoke(String method, Object[] args);
    }
}
