package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.RedisConstatns;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.model.record.ProductDayRecord;
import com.zbkj.common.model.record.ShoppingProductDayRecord;
import com.zbkj.common.model.record.TradingDayRecord;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StatisticsTaskService 接口实现
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
public class StatisticsTaskServiceImpl implements StatisticsTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsTaskServiceImpl.class);

    @Autowired
    private ShoppingProductDayRecordService shoppingProductDayRecordService;

    @Autowired
    private ProductDayRecordService productDayRecordService;

    @Autowired
    private TradingDayRecordService tradingDayRecordService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreProductRelationService productRelationService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private StoreOrderStatusService storeOrderStatusService;

    @Autowired
    private UserRechargeService userRechargeService;

    @Autowired
    private UserBrokerageRecordService brokerageRecordService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 每天零点的自动统计前一天的数据
     */
    @Override
    public void autoStatistics() {
        // 获取昨天的日期
        String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");


       //  ========================================
       //  商品商城部分
       //  ========================================

        // 商品新增量
        Integer newProductNum = storeProductService.getNewProductByDate(yesterdayStr);

        // 浏览量
        int pageViewNum = 0;
        Object pageViewObject = redisUtil.get(RedisConstatns.PRO_PAGE_VIEW_KEY + yesterdayStr);
        if (ObjectUtil.isNotNull(pageViewObject)) {
            pageViewNum = (Integer) pageViewObject;
        }

        // 收藏量
        Integer collectNum = productRelationService.getCountByDate(yesterdayStr);

        // 加购量
        int addCartNum = 0;
        Object addCartObject = redisUtil.get(RedisConstatns.PRO_ADD_CART_KEY + yesterdayStr);
        if (ObjectUtil.isNotNull(addCartObject)) {
            addCartNum = (Integer) addCartObject;
        }

        // 交易总件数
        Integer orderProductNum = storeOrderService.getOrderProductNumByDate(yesterdayStr);

        // 交易成功件数
        Integer orderSuccessProductNum = storeOrderService.getOrderSuccessProductNumByDate(yesterdayStr);

        // 保存商品统计数据（商城每日）
        ShoppingProductDayRecord shoppingProductDayRecord = new ShoppingProductDayRecord();
        shoppingProductDayRecord.setDate(yesterdayStr);
        shoppingProductDayRecord.setAddProductNum(newProductNum);
        shoppingProductDayRecord.setPageView(pageViewNum);
        shoppingProductDayRecord.setCollectNum(collectNum);
        shoppingProductDayRecord.setAddCartNum(addCartNum);
        shoppingProductDayRecord.setOrderProductNum(orderProductNum);
        shoppingProductDayRecord.setOrderSuccessProductNum(orderSuccessProductNum);

        //  ========================================
        //  商品个体部分
        //  ========================================

        // 获取所有未删除的商品
        List<StoreProduct> allProduct = storeProductService.findAllProductByNotDelte();
        List<ProductDayRecord> productDayRecordList = allProduct.stream().map(e -> {
            ProductDayRecord productDayRecord = new ProductDayRecord();
            productDayRecord.setProductId(e.getId());
            productDayRecord.setDate(yesterdayStr);
            return productDayRecord;
        }).collect(Collectors.toList());

        productDayRecordList.forEach(e -> {
            // 浏览量
            int pageViewDetailNum = 0;
            Object pageViewObject1 = redisUtil.get(StrUtil.format(RedisConstatns.PRO_PRO_PAGE_VIEW_KEY, yesterdayStr, e.getProductId()));
            if (ObjectUtil.isNotNull(pageViewObject1)) {
                pageViewDetailNum = (Integer) pageViewObject1;
            }
            e.setPageView(pageViewDetailNum);
            // 收藏量
            Integer collectDetailNum = productRelationService.getCountByDateAndProId(yesterdayStr, e.getProductId());
            e.setCollectNum(collectDetailNum);
            // 加购件数
            int addCartDetailNum = 0;
            Object addCartObject1 = redisUtil.get(StrUtil.format(RedisConstatns.PRO_PRO_ADD_CART_KEY, yesterdayStr, e.getProductId()));
            if (ObjectUtil.isNotNull(addCartObject1)) {
                addCartDetailNum = (Integer) addCartObject1;
            }
            e.setAddCartNum(addCartDetailNum);
            // 销量
            e.setOrderProductNum(storeOrderInfoService.getSalesNumByDateAndProductId(yesterdayStr, e.getProductId()));
            // 销售额
            e.setOrderSuccessProductFee(storeOrderInfoService.getSalesByDateAndProductId(yesterdayStr, e.getProductId()));
        });

        //  ========================================
        //  交易记录/日
        //  ========================================

        // 订单数量
        Integer orderNum = storeOrderService.getOrderNumByDate(yesterdayStr);

        // 订单支付数量
        Integer payOrderNum = storeOrderService.getPayOrderNumByDate(yesterdayStr);

        // 订单支付金额(是否包含余额支付金额)
        BigDecimal payOrderAmount = storeOrderService.getPayOrderAmountByDate(yesterdayStr);

        // 订单退款数量
        Integer refundOrderNum = storeOrderStatusService.getRefundOrderNumByDate(yesterdayStr);

        // 订单退款金额
        BigDecimal refundOrderAmount = storeOrderStatusService.getRefundOrderAmountByDate(yesterdayStr);

        // 充值订单数量
        Integer rechargeOrderNum = userRechargeService.getRechargeOrderNumByDate(yesterdayStr);

        // 充值订单金额
        BigDecimal rechargeOrderAmount = userRechargeService.getRechargeOrderAmountByDate(yesterdayStr);

        // 余额支付金额
        BigDecimal yuePayOrderAmount = storeOrderService.getYuePayOrderAmountByDate(yesterdayStr);

        // 支付佣金金额（用户确认到账佣金）
        BigDecimal brokerageAmount = brokerageRecordService.getBrokerageAmountByDate(yesterdayStr);

        TradingDayRecord tradingDayRecord = new TradingDayRecord();
        tradingDayRecord.setDate(yesterdayStr);
        tradingDayRecord.setProductOrderNum(orderNum);
        tradingDayRecord.setProductOrderPayNum(payOrderNum);
        tradingDayRecord.setProductOrderPayFee(payOrderAmount);
        tradingDayRecord.setProductOrderRefundNum(refundOrderNum);
        tradingDayRecord.setProductOrderRefundFee(refundOrderAmount);
        tradingDayRecord.setRechargeOrderNum(rechargeOrderNum);
        tradingDayRecord.setRechargeOrderFee(rechargeOrderAmount);
        tradingDayRecord.setBalancePayFee(yuePayOrderAmount);
        tradingDayRecord.setBrokerageFee(brokerageAmount);

        Boolean execute = transactionTemplate.execute(e -> {
            shoppingProductDayRecordService.save(shoppingProductDayRecord);
            productDayRecordService.saveBatch(productDayRecordList, 100);
            tradingDayRecordService.save(tradingDayRecord);
            return Boolean.TRUE;
        });
        if (!execute) {
            LOGGER.error("每日统计任务保存数据库失败");
            throw new CrmebException("每日统计任务保存数据库失败");
        }
        redisUtil.delete(RedisConstatns.PRO_PAGE_VIEW_KEY + yesterdayStr);
        redisUtil.delete(RedisConstatns.PRO_ADD_CART_KEY + yesterdayStr);
    }
}
