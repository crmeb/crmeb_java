package com.zbkj.service.service;

import com.zbkj.common.response.HomeRateResponse;

import java.util.Map;

/**
 * 首页统计
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface HomeService{

    /**
     * 用户曲线图
     */
    Map<Object, Object> chartUser();

    /**
     * 30天订单量趋势
     */
    Map<String, Object> chartOrder();

    /**
     * 用户购买统计
     */
    Map<String, Integer> chartUserBuy();

    /**
     * 周订单量趋势
     */
    Map<String, Object> chartOrderInWeek();

    /**
     * 月订单量趋势
     */
    Map<String, Object> chartOrderInMonth();

    /**
     * 年订单量趋势
     */
    Map<String, Object> chartOrderInYear();

    /**
     * 首页数据
     * @return HomeRateResponse
     */
    HomeRateResponse indexDate();
}
