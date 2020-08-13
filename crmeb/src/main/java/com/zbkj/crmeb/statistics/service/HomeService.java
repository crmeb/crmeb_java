package com.zbkj.crmeb.statistics.service;

import com.zbkj.crmeb.statistics.response.HomeRateResponse;

import java.util.Map;

/**
 * <p>
 * 首页统计
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-16
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
