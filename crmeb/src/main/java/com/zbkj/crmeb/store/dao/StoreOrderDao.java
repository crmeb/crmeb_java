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
 * 订单表 Mapper 接口
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
