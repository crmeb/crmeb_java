package com.zbkj.service.service;

import com.zbkj.common.response.TradeDataResponse;
import com.zbkj.common.response.TradingDataResponse;
import com.zbkj.common.response.TrandeTrendDateResponse;

import java.util.List;

/**
 * TradeStatisticsService 接口
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
public interface TradeStatisticsService {

    /**
     * 交易统计数据
     * @return TradeDataResponse
     */
    TradeDataResponse getData();

    /**
     * 交易统计概览
     * @param dateLimit 时间参数
     * @return TradingDataResponse
     */
    TradingDataResponse getOverview(String dateLimit);

    /**
     * 交易趋势数据
     * @param dateLimit 时间参数
     * @return List
     */
    List<TrandeTrendDateResponse> getTrendDataByDate(String dateLimit);
}
