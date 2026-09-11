package com.zbkj.front.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.common.request.StoreOrderTabsNumRequest;
import com.zbkj.common.response.MobileAdminManageStatisticsResponse;
import com.zbkj.common.response.StoreOrderCountItemResponse;
import com.zbkj.front.service.MobileAdminManageService;
import com.zbkj.front.service.MobileAdminPermissionService;
import com.zbkj.service.service.StoreOrderService;
import com.zbkj.service.service.StoreProductService;
import com.zbkj.service.service.UserVisitRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 移动端商家管理工作台服务实现
 */
@Service
public class MobileAdminManageServiceImpl implements MobileAdminManageService {

    @Autowired
    private MobileAdminPermissionService mobileAdminPermissionService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private UserVisitRecordService userVisitRecordService;

    @Override
    public MobileAdminManageStatisticsResponse getStatistics() {
        mobileAdminPermissionService.requireEnabledStaff();
        String today = DateUtil.today();
        String startTime = today + " 00:00:00";
        String endTime = today + " 23:59:59";

        MobileAdminManageStatisticsResponse response = new MobileAdminManageStatisticsResponse();
        response.setTodayOrderPrice(getTodayOrderPrice(startTime, endTime));
        response.setTodayOrderCount(getTodayOrderCount(startTime, endTime));
        response.setTodayOrderUserCount(getTodayOrderUserCount(startTime, endTime));
        response.setTodayVisitCount(userVisitRecordService.getPageviewsByDate(today));

        StoreOrderTabsNumRequest tabsNumRequest = new StoreOrderTabsNumRequest();
        tabsNumRequest.setType(2);
        StoreOrderCountItemResponse orderCount = storeOrderService.getOrderStatusNum(tabsNumRequest);
        response.setUnDeliveryOrderCount(orderCount.getNotShipped());
        response.setRefundingCount(orderCount.getRefunding());

        response.setOutOfStock(getOutOfStockProductCount());
        response.setPoliceForce(storeProductService.getVigilanceInventoryNum());
        return response;
    }

    private BigDecimal getTodayOrderPrice(String startTime, String endTime) {
        QueryWrapper<StoreOrder> wrapper = new QueryWrapper<>();
        wrapper.select("ifnull(sum(pay_price), 0) as pay_price");
        wrapper.eq("paid", 1);
        wrapper.isNotNull("pay_time");
        wrapper.between("pay_time", startTime, endTime);
        StoreOrder order = storeOrderService.getOne(wrapper, false);
        if (order == null || order.getPayPrice() == null) {
            return BigDecimal.ZERO;
        }
        return order.getPayPrice();
    }

    private Integer getTodayOrderCount(String startTime, String endTime) {
        QueryWrapper<StoreOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("paid", 1);
        wrapper.isNotNull("pay_time");
        wrapper.between("pay_time", startTime, endTime);
        return storeOrderService.count(wrapper);
    }

    private Integer getTodayOrderUserCount(String startTime, String endTime) {
        QueryWrapper<StoreOrder> wrapper = new QueryWrapper<>();
        wrapper.select("uid");
        wrapper.eq("paid", 1);
        wrapper.isNotNull("pay_time");
        wrapper.between("pay_time", startTime, endTime);
        wrapper.groupBy("uid");
        List<StoreOrder> orderList = storeOrderService.list(wrapper);
        return orderList == null ? 0 : orderList.size();
    }

    private Integer getOutOfStockProductCount() {
        QueryWrapper<StoreProduct> wrapper = new QueryWrapper<>();
        wrapper.le("stock", 0);
        wrapper.eq("is_recycle", false);
        wrapper.eq("is_del", false);
        return storeProductService.count(wrapper);
    }
}
