package com.zbkj.crmeb.statistics.service;

import com.zbkj.crmeb.statistics.response.HomeRateResponse;

import java.util.Map;

/**
 * 首页统计
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
public interface HomeService{
    HomeRateResponse sales();

    HomeRateResponse user();

    HomeRateResponse views();

    HomeRateResponse order();

    Map<Object, Object> chartUser();

    Map<String, Object> chartOrder();

    Map<String, Integer> chartUserBuy();

    Map<String, Object> chartOrderInWeek();

    Map<String, Object> chartOrderInMonth();

    Map<String, Object> chartOrderInYear();
}
