package com.zbkj.crmeb.store.dao;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.crmeb.store.request.StoreDateRangeSqlPram;
import com.zbkj.crmeb.store.request.StoreOrderStaticsticsRequest;
import com.zbkj.crmeb.store.response.StoreOrderStatisticsChartItemResponse;
import com.zbkj.crmeb.store.response.StoreStaffDetail;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-28
 */
public interface StoreOrderDao extends BaseMapper<StoreOrder> {

    BigDecimal getTotalPrice(String where);

    BigDecimal getRefundPrice(String where);

    Integer getRefundTotal(String where);

    /**
     * 核销详情 月数据
     * @param request 分页和日期
     * @return 月数据
     */
    List<StoreStaffDetail> getOrderVerificationDetail(StoreOrderStaticsticsRequest request);

    /**
     * 订单统计详情 price
     * @param pram 时间区间参数
     * @return 月数据
     */
    List<StoreOrderStatisticsChartItemResponse> getOrderStatisticsPriceDetail(StoreDateRangeSqlPram pram);

    /**
     * 订单统计详情 订单量
     * @param pram 时间区间参数
     * @return 月数据
     */
    List<StoreOrderStatisticsChartItemResponse> getOrderStatisticsOrderCountDetail(StoreDateRangeSqlPram pram);

}
