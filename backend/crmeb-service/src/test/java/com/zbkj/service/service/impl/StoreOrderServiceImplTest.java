package com.zbkj.service.service.impl;

import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.StoreOrderRefundRequest;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.service.dao.StoreOrderDao;
import com.zbkj.service.service.UserBillService;
import com.zbkj.service.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

public class StoreOrderServiceImplTest {

    @Test
    public void refundRejectsOrderWhileRefundIsProcessing() {
        assertRefundRejected(3, "订单退款处理中，请勿重复操作");
    }

    @Test
    public void refundRejectsAlreadyRefundedOrder() {
        assertRefundRejected(2, "订单已退款，请勿重复操作");
    }

    @Test
    public void balanceRefundCompletesImmediately() {
        StoreOrder order = new StoreOrder();
        order.setId(1);
        order.setOrderId("balance-order");
        order.setUid(1);
        order.setPaid(true);
        order.setPayType(Constants.PAY_TYPE_YUE);
        order.setRefundStatus(0);
        order.setRefundPrice(BigDecimal.ZERO);
        order.setPayPrice(BigDecimal.TEN);

        User user = new User();
        user.setUid(order.getUid());
        user.setNowMoney(BigDecimal.TEN);

        StoreOrderDao dao = (StoreOrderDao) Proxy.newProxyInstance(
                StoreOrderDao.class.getClassLoader(),
                new Class<?>[]{StoreOrderDao.class},
                (proxy, method, args) -> {
                    if ("selectOne".equals(method.getName())) {
                        return order;
                    }
                    if ("updateById".equals(method.getName())) {
                        return 1;
                    }
                    return null;
                }
        );

        AtomicBoolean balanceUpdated = new AtomicBoolean(false);
        UserService userService = (UserService) Proxy.newProxyInstance(
                UserService.class.getClassLoader(),
                new Class<?>[]{UserService.class},
                (proxy, method, args) -> {
                    if ("getById".equals(method.getName())) {
                        return user;
                    }
                    if ("operationNowMoney".equals(method.getName())) {
                        balanceUpdated.set(true);
                        return Boolean.TRUE;
                    }
                    return null;
                }
        );
        UserBillService userBillService = (UserBillService) Proxy.newProxyInstance(
                UserBillService.class.getClassLoader(),
                new Class<?>[]{UserBillService.class},
                (proxy, method, args) -> "saveRefundBill".equals(method.getName()) ? Boolean.TRUE : null
        );

        AtomicBoolean refundTaskQueued = new AtomicBoolean(false);
        RedisUtil redisUtil = new RedisUtil(null, null) {
            @Override
            public boolean lPush(String key, Object value) {
                refundTaskQueued.set(true);
                return true;
            }
        };

        TransactionTemplate transactionTemplate = new TransactionTemplate(new PlatformTransactionManager() {
            @Override
            public TransactionStatus getTransaction(TransactionDefinition definition) {
                return new SimpleTransactionStatus();
            }

            @Override
            public void commit(TransactionStatus status) {
            }

            @Override
            public void rollback(TransactionStatus status) {
            }
        });

        StoreOrderServiceImpl service = new StoreOrderServiceImpl();
        ReflectionTestUtils.setField(service, "dao", dao);
        ReflectionTestUtils.setField(service, "baseMapper", dao);
        ReflectionTestUtils.setField(service, "userService", userService);
        ReflectionTestUtils.setField(service, "userBillService", userBillService);
        ReflectionTestUtils.setField(service, "redisUtil", redisUtil);
        ReflectionTestUtils.setField(service, "transactionTemplate", transactionTemplate);

        StoreOrderRefundRequest request = new StoreOrderRefundRequest();
        request.setOrderNo(order.getOrderId());
        request.setAmount(BigDecimal.ONE);

        Assert.assertTrue(service.refund(request));
        Assert.assertEquals(Integer.valueOf(2), order.getRefundStatus());
        Assert.assertTrue(balanceUpdated.get());
        Assert.assertTrue(refundTaskQueued.get());
    }

    private void assertRefundRejected(Integer refundStatus, String expectedMessage) {
        StoreOrder order = new StoreOrder();
        order.setOrderId("order-test");
        order.setPaid(true);
        order.setRefundStatus(refundStatus);
        order.setRefundPrice(BigDecimal.ZERO);
        order.setPayPrice(BigDecimal.TEN);

        StoreOrderDao dao = (StoreOrderDao) Proxy.newProxyInstance(
                StoreOrderDao.class.getClassLoader(),
                new Class<?>[]{StoreOrderDao.class},
                (proxy, method, args) -> "selectOne".equals(method.getName()) ? order : null
        );

        StoreOrderServiceImpl service = new StoreOrderServiceImpl();
        ReflectionTestUtils.setField(service, "dao", dao);

        StoreOrderRefundRequest request = new StoreOrderRefundRequest();
        request.setOrderNo(order.getOrderId());
        request.setAmount(BigDecimal.ONE);

        try {
            service.refund(request);
            Assert.fail("Expected duplicate refund request to be rejected");
        } catch (CrmebException exception) {
            Assert.assertEquals(expectedMessage, exception.getMessage());
        }
    }
}
