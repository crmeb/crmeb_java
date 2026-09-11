package com.zbkj.service.service;

/**
 * StatisticsTaskService 接口
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
public interface StatisticsTaskService {

    /**
     * 每天零点的自动统计
     */
    void autoStatistics();

    /**
     * 根据日期获取统计数据(商城维度)
     * @param dateLimit 日期参数
     * @return ShoppingProductDataResponse
     */
//    ShoppingProductDataResponse getDataByDate(String dateLimit);
}
