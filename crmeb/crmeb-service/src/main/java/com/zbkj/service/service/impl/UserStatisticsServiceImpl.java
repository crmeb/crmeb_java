package com.zbkj.service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.user.User;
import com.zbkj.common.response.*;
import com.zbkj.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserStatisticsService 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class UserStatisticsServiceImpl implements UserStatisticsService {

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRechargeService userRechargeService;

    @Autowired
    private UserBrokerageRecordService brokerageRecordService;

    @Autowired
    private UserVisitRecordService userVisitRecordService;


    /**
     * 用户概览数据
     * @param dateLimit 时间参数
     * @return UserOverviewResponse
     */
    @Override
    public UserOverviewResponse getOverview(String dateLimit) {
        if (StrUtil.isBlank(dateLimit)) {
            throw new CrmebException("日期参数不能为空");
        }
        // 判断时间类型
        if (Constants.SEARCH_DATE_DAY.equals(dateLimit)) {
            // 获取今天的数据(所有数据现查)
            UserOverviewResponse nowDayData = getDataByDate(DateUtil.date().toString("yyyy-MM-dd"));
            // 获取昨天的数据
            DateTime yesterday = DateUtil.yesterday();
            String yesterdayStr = yesterday.toString("yyyy-MM-dd");
            UserOverviewResponse yesterdayData = getSimpleDataByDate(yesterdayStr);
            // 计算环比，返回对象
            return getCommonResponse(nowDayData, yesterdayData);
        }
        if (Constants.SEARCH_DATE_YESTERDAY.equals(dateLimit)) {
            // 获取昨天的数据
            DateTime yesterday = DateUtil.yesterday();
            String yesterdayStr = yesterday.toString("yyyy-MM-dd");
            UserOverviewResponse yesterdayData = getDataByDate(yesterdayStr);
            // 获取更前一天的数据
            String beforeDay = DateUtil.offsetDay(yesterday, -1).toString("yyyy-MM-dd");
            UserOverviewResponse beforeDayDta = getSimpleDataByDate(beforeDay);
            // 计算环比，返回对象
            return getCommonResponse(yesterdayData, beforeDayDta);
        }
        // 时间段数据查询(包含7天、30天、周、月、年)
        if (Constants.SEARCH_DATE_LATELY_7.equals(dateLimit)) {// 7天
            // 分别获取今天、前6天的数据，数据相加
            DateTime nowDate = DateUtil.date();
            // 获取今天的数据(所有数据现查)
            UserOverviewResponse nowDayData = getDataByDate(nowDate.toString("yyyy-MM-dd"));
            // 获取往前推七天的日期跟昨天的日期
            String beforeSevenDateStr = DateUtil.offsetDay(nowDate, -6).toString("yyyy-MM-dd");
            String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
            UserOverviewResponse intervalDate = getDataByPeriod(beforeSevenDateStr, yesterdayStr);
            intervalDate.setRechargeUserNum(intervalDate.getRechargeUserNum() + nowDayData.getRechargeUserNum());
            intervalDate.setPageviews(intervalDate.getPageviews() + nowDayData.getPageviews());
            intervalDate.setActiveUserNum(intervalDate.getActiveUserNum() + nowDayData.getActiveUserNum());
            intervalDate.setOrderUserNum(intervalDate.getOrderUserNum() + nowDayData.getOrderUserNum());
            intervalDate.setRechargeUserNum(intervalDate.getRechargeUserNum() + nowDayData.getRechargeUserNum());
            intervalDate.setOrderPayUserNum(intervalDate.getOrderPayUserNum() + nowDayData.getOrderPayUserNum());
            intervalDate.setPayOrderAmount(intervalDate.getPayOrderAmount().add(nowDayData.getPayOrderAmount()));
            intervalDate.setCustomerPrice(commonCustomerPrice(intervalDate.getPayOrderAmount(), intervalDate.getOrderPayUserNum()));

            // 获取前14天-前8天这个区间的数据
            String beforeFourteenDateStr = DateUtil.offsetDay(nowDate, -13).toString("yyyy-MM-dd");
            String beforeEightDateStr = DateUtil.offsetDay(nowDate, -7).toString("yyyy-MM-dd");
            UserOverviewResponse intervalDate2 = getSimpleDataByPeriod(beforeFourteenDateStr, beforeEightDateStr);
            // 计算环比，返回对象
            return getCommonResponse(intervalDate, intervalDate2);
        }
        if (Constants.SEARCH_DATE_LATELY_30.equals(dateLimit)) {// 30天
            DateTime nowDate = DateUtil.date();
            // 获取今天的数据(所有数据现查)
            UserOverviewResponse nowDayData = getDataByDate(nowDate.toString("yyyy-MM-dd"));
            // 获取往前推30天的日期跟昨天的日期
            String beforeSevenDateStr = DateUtil.offsetDay(nowDate, -29).toString("yyyy-MM-dd");
            String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
            UserOverviewResponse intervalDate = getDataByPeriod(beforeSevenDateStr, yesterdayStr);
            intervalDate.setRechargeUserNum(intervalDate.getRechargeUserNum() + nowDayData.getRechargeUserNum());
            intervalDate.setPageviews(intervalDate.getPageviews() + nowDayData.getPageviews());
            intervalDate.setActiveUserNum(intervalDate.getActiveUserNum() + nowDayData.getActiveUserNum());
            intervalDate.setOrderUserNum(intervalDate.getOrderUserNum() + nowDayData.getOrderUserNum());
            intervalDate.setRechargeUserNum(intervalDate.getRechargeUserNum() + nowDayData.getRechargeUserNum());
            intervalDate.setOrderPayUserNum(intervalDate.getOrderPayUserNum() + nowDayData.getOrderPayUserNum());
            intervalDate.setPayOrderAmount(intervalDate.getPayOrderAmount().add(nowDayData.getPayOrderAmount()));
            intervalDate.setCustomerPrice(commonCustomerPrice(intervalDate.getPayOrderAmount(), intervalDate.getOrderPayUserNum()));

            // 获取前14天-前8天这个区间的数据
            String beforeFourteenDateStr = DateUtil.offsetDay(nowDate, -59).toString("yyyy-MM-dd");
            String beforeEightDateStr = DateUtil.offsetDay(nowDate, -30).toString("yyyy-MM-dd");
            UserOverviewResponse intervalDate2 = getSimpleDataByPeriod(beforeFourteenDateStr, beforeEightDateStr);
            // 计算环比，返回对象
            return getCommonResponse(intervalDate, intervalDate2);
        }
        if (Constants.SEARCH_DATE_WEEK.equals(dateLimit)) {// 周
            // 判断今天是否是本周第一天
            int dayOfWeek = DateUtil.thisDayOfWeek();
            DateTime nowDate = DateUtil.date();
            UserOverviewResponse intervalDate = new UserOverviewResponse();
            if (dayOfWeek == 2) {// 周一
                // 获取今天的数据(所有数据现查)
                intervalDate = getDataByDate(nowDate.toString("yyyy-MM-dd"));
            } else if (dayOfWeek == 3) {// 周二
                UserOverviewResponse nowDayData = getDataByDate(nowDate.toString("yyyy-MM-dd"));
                // 获取昨天的数据
                String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
                UserOverviewResponse yesterdayData = getDataByDate(yesterdayStr);
                intervalDate.setRechargeUserNum(yesterdayData.getRechargeUserNum() + nowDayData.getRechargeUserNum());
                intervalDate.setPageviews(yesterdayData.getPageviews() + nowDayData.getPageviews());
                intervalDate.setActiveUserNum(yesterdayData.getActiveUserNum() + nowDayData.getActiveUserNum());
                intervalDate.setOrderUserNum(yesterdayData.getOrderUserNum() + nowDayData.getOrderUserNum());
                intervalDate.setRechargeUserNum(yesterdayData.getRechargeUserNum() + nowDayData.getRechargeUserNum());
                intervalDate.setOrderPayUserNum(yesterdayData.getOrderPayUserNum() + nowDayData.getOrderPayUserNum());
                intervalDate.setPayOrderAmount(yesterdayData.getPayOrderAmount().add(nowDayData.getPayOrderAmount()));
                intervalDate.setCustomerPrice(commonCustomerPrice(intervalDate.getPayOrderAmount(), intervalDate.getOrderPayUserNum()));
            } else {
                UserOverviewResponse nowDayData = getDataByDate(nowDate.toString("yyyy-MM-dd"));
                // 获取周一到现在的数据
                String weekStartDateStr = DateUtil.beginOfWeek(nowDate).toString("yyyy-MM-dd");
                String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
                intervalDate = getDataByPeriod(weekStartDateStr, yesterdayStr);
                intervalDate.setRechargeUserNum(intervalDate.getRechargeUserNum() + nowDayData.getRechargeUserNum());
                intervalDate.setPageviews(intervalDate.getPageviews() + nowDayData.getPageviews());
                intervalDate.setActiveUserNum(intervalDate.getActiveUserNum() + nowDayData.getActiveUserNum());
                intervalDate.setOrderUserNum(intervalDate.getOrderUserNum() + nowDayData.getOrderUserNum());
                intervalDate.setRechargeUserNum(intervalDate.getRechargeUserNum() + nowDayData.getRechargeUserNum());
                intervalDate.setOrderPayUserNum(intervalDate.getOrderPayUserNum() + nowDayData.getOrderPayUserNum());
                intervalDate.setPayOrderAmount(intervalDate.getPayOrderAmount().add(nowDayData.getPayOrderAmount()));
                intervalDate.setCustomerPrice(commonCustomerPrice(intervalDate.getPayOrderAmount(), intervalDate.getOrderPayUserNum()));
            }

            // 获取上一周的数据
            DateTime offsetWeek = DateUtil.offsetWeek(nowDate, -1);
            String beforeWeekStartDateStr = DateUtil.beginOfWeek(offsetWeek).toString("yyyy-MM-dd");
            String beforeWeekEndDateStr = DateUtil.endOfWeek(offsetWeek).toString("yyyy-MM-dd");
            UserOverviewResponse intervalDate2 = getSimpleDataByPeriod(beforeWeekStartDateStr, beforeWeekEndDateStr);
            // 计算环比，返回对象
            return getCommonResponse(intervalDate, intervalDate2);
        }
        if (Constants.SEARCH_DATE_MONTH.equals(dateLimit)) {// 月
            // 判断今天是否是本月第一天
            int dayOfMonth = DateUtil.thisDayOfMonth();
            DateTime nowDate = DateUtil.date();
            UserOverviewResponse intervalDate = new UserOverviewResponse();
            if (dayOfMonth == 1) {// 一号
                // 获取今天的数据(所有数据现查)
                intervalDate = getDataByDate(nowDate.toString("yyyy-MM-dd"));
            } else if (dayOfMonth == 2) {// 二号
                UserOverviewResponse nowDayData = getDataByDate(nowDate.toString("yyyy-MM-dd"));
                // 获取昨天的数据
                String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
                UserOverviewResponse yesterdayData = getDataByDate(yesterdayStr);
                intervalDate.setRechargeUserNum(yesterdayData.getRechargeUserNum() + nowDayData.getRechargeUserNum());
                intervalDate.setPageviews(yesterdayData.getPageviews() + nowDayData.getPageviews());
                intervalDate.setActiveUserNum(yesterdayData.getActiveUserNum() + nowDayData.getActiveUserNum());
                intervalDate.setOrderUserNum(yesterdayData.getOrderUserNum() + nowDayData.getOrderUserNum());
                intervalDate.setRechargeUserNum(yesterdayData.getRechargeUserNum() + nowDayData.getRechargeUserNum());
                intervalDate.setOrderPayUserNum(yesterdayData.getOrderPayUserNum() + nowDayData.getOrderPayUserNum());
                intervalDate.setPayOrderAmount(yesterdayData.getPayOrderAmount().add(nowDayData.getPayOrderAmount()));
                intervalDate.setCustomerPrice(commonCustomerPrice(intervalDate.getPayOrderAmount(), intervalDate.getOrderPayUserNum()));
            } else {
                UserOverviewResponse nowDayData = getDataByDate(nowDate.toString("yyyy-MM-dd"));
                // 获取月初到现在的数据
                String monthStartDateStr = DateUtil.beginOfMonth(nowDate).toString("yyyy-MM-dd");
                String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
                intervalDate = getDataByPeriod(monthStartDateStr, yesterdayStr);
                intervalDate.setRechargeUserNum(intervalDate.getRechargeUserNum() + nowDayData.getRechargeUserNum());
                intervalDate.setPageviews(intervalDate.getPageviews() + nowDayData.getPageviews());
                intervalDate.setActiveUserNum(intervalDate.getActiveUserNum() + nowDayData.getActiveUserNum());
                intervalDate.setOrderUserNum(intervalDate.getOrderUserNum() + nowDayData.getOrderUserNum());
                intervalDate.setRechargeUserNum(intervalDate.getRechargeUserNum() + nowDayData.getRechargeUserNum());
                intervalDate.setOrderPayUserNum(intervalDate.getOrderPayUserNum() + nowDayData.getOrderPayUserNum());
                intervalDate.setPayOrderAmount(intervalDate.getPayOrderAmount().add(nowDayData.getPayOrderAmount()));
                intervalDate.setCustomerPrice(commonCustomerPrice(intervalDate.getPayOrderAmount(), intervalDate.getOrderPayUserNum()));
            }

            // 获取上一月的数据
            DateTime offsetMonth = DateUtil.offsetMonth(nowDate, -1);
            String beforeMonthStartDateStr = DateUtil.beginOfMonth(offsetMonth).toString("yyyy-MM-dd");
            String beforeMonthEndDateStr = DateUtil.endOfMonth(offsetMonth).toString("yyyy-MM-dd");
            UserOverviewResponse intervalDate2 = getSimpleDataByPeriod(beforeMonthStartDateStr, beforeMonthEndDateStr);
            // 计算环比，返回对象
            return getCommonResponse(intervalDate, intervalDate2);
        }
        if (Constants.SEARCH_DATE_YEAR.equals(dateLimit)) {// 年
            // 判断今天是否是本年第一天
            DateTime nowDate = DateUtil.date();
            UserOverviewResponse intervalDate = new UserOverviewResponse();
            // 获取当年的开始日期
            String beginOfYear = DateUtil.beginOfYear(nowDate).toString("yyyy-MM-dd");
            String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
            if (beginOfYear.equals(nowDate.toString("yyyy-MM-dd"))) {// 第一天
                // 获取今天的数据(所有数据现查)
                intervalDate = getDataByDate(nowDate.toString("yyyy-MM-dd"));
            } else if (beginOfYear.equals(yesterdayStr)) {// 第二天
                UserOverviewResponse nowDayData = getDataByDate(nowDate.toString("yyyy-MM-dd"));
                // 获取昨天的数据
                UserOverviewResponse yesterdayData = getDataByDate(yesterdayStr);
                intervalDate.setRechargeUserNum(yesterdayData.getRechargeUserNum() + nowDayData.getRechargeUserNum());
                intervalDate.setPageviews(yesterdayData.getPageviews() + nowDayData.getPageviews());
                intervalDate.setActiveUserNum(yesterdayData.getActiveUserNum() + nowDayData.getActiveUserNum());
                intervalDate.setOrderUserNum(yesterdayData.getOrderUserNum() + nowDayData.getOrderUserNum());
                intervalDate.setRechargeUserNum(yesterdayData.getRechargeUserNum() + nowDayData.getRechargeUserNum());
                intervalDate.setOrderPayUserNum(yesterdayData.getOrderPayUserNum() + nowDayData.getOrderPayUserNum());
                intervalDate.setPayOrderAmount(yesterdayData.getPayOrderAmount().add(nowDayData.getPayOrderAmount()));
                intervalDate.setCustomerPrice(commonCustomerPrice(intervalDate.getPayOrderAmount(), intervalDate.getOrderPayUserNum()));
            } else {
                UserOverviewResponse nowDayData = getDataByDate(nowDate.toString("yyyy-MM-dd"));
                // 获取年初到现在的数据
                intervalDate = getDataByPeriod(beginOfYear, yesterdayStr);
                intervalDate.setRechargeUserNum(intervalDate.getRechargeUserNum() + nowDayData.getRechargeUserNum());
                intervalDate.setPageviews(intervalDate.getPageviews() + nowDayData.getPageviews());
                intervalDate.setActiveUserNum(intervalDate.getActiveUserNum() + nowDayData.getActiveUserNum());
                intervalDate.setOrderUserNum(intervalDate.getOrderUserNum() + nowDayData.getOrderUserNum());
                intervalDate.setRechargeUserNum(intervalDate.getRechargeUserNum() + nowDayData.getRechargeUserNum());
                intervalDate.setOrderPayUserNum(intervalDate.getOrderPayUserNum() + nowDayData.getOrderPayUserNum());
                intervalDate.setPayOrderAmount(intervalDate.getPayOrderAmount().add(nowDayData.getPayOrderAmount()));
                intervalDate.setCustomerPrice(commonCustomerPrice(intervalDate.getPayOrderAmount(), intervalDate.getOrderPayUserNum()));
            }

            // 获取上一年的数据
            DateTime offsetYear = DateUtil.offsetDay(DateUtil.beginOfYear(nowDate), -1);
            String beforeYearStartDateStr = DateUtil.beginOfYear(offsetYear).toString("yyyy-MM-dd");
            String beforeYearEndDateStr = DateUtil.endOfYear(offsetYear).toString("yyyy-MM-dd");
            UserOverviewResponse intervalDate2 = getSimpleDataByPeriod(beforeYearStartDateStr, beforeYearEndDateStr);
            // 计算环比，返回对象
            return getCommonResponse(intervalDate, intervalDate2);
        }
        // 自定义时间,切割时间
        String[] split = dateLimit.split(",");
        if (split.length < 2) {
            throw new CrmebException("请选择正确的时间范围");
        }
        DateTime startDate = DateUtil.parseDate(split[0]);
        DateTime endDate = DateUtil.parseDate(split[1]);
        // 是否是同一天
        if (DateUtil.isSameDay(startDate, endDate)) {
            String dateStr = startDate.toString("yyyy-MM-dd");
            // 判断时间是否是今天
            if (DateUtil.isSameDay(startDate, DateUtil.date())) {
                // 获取今天的数据(所有数据现查)
                UserOverviewResponse nowDayData = getDataByDate(DateUtil.date().toString("yyyy-MM-dd"));
                // 获取昨天的数据
                DateTime yesterday = DateUtil.yesterday();
                String yesterdayStr = yesterday.toString("yyyy-MM-dd");
                UserOverviewResponse yesterdayData = getSimpleDataByDate(yesterdayStr);
                // 计算环比，返回对象
                return getCommonResponse(nowDayData, yesterdayData);
            }
            // 不是今天
            UserOverviewResponse dayData = getDataByDate(dateStr);
            String beforeDateStr = DateUtil.offsetDay(startDate, -1).toString("yyyy-MM-dd");
            UserOverviewResponse beforeDayData = getSimpleDataByDate(beforeDateStr);
            // 计算环比，返回对象
            return getCommonResponse(dayData, beforeDayData);
        }
        // 判断开始时间是否大于结束时间
        if (startDate.compareTo(endDate) >= 0) {
            throw new CrmebException("请选择正确的时间范围");
        }
        // 获取两个日期间隔了多少天
        long between = DateUtil.between(startDate, endDate, DateUnit.DAY);
        UserOverviewResponse intervalDate = new UserOverviewResponse();
        // 判断结束时间是否是今天
        if (DateUtil.isSameDay(startDate, DateUtil.date())) {
            // 获取今天的数据(所有数据现查)
            UserOverviewResponse nowDayData = getDataByDate(DateUtil.date().toString("yyyy-MM-dd"));
            String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
            intervalDate = getDataByPeriod(startDate.toString("yyyy-MM-dd"), yesterdayStr);
            intervalDate.setRechargeUserNum(intervalDate.getRechargeUserNum() + nowDayData.getRechargeUserNum());
            intervalDate.setPageviews(intervalDate.getPageviews() + nowDayData.getPageviews());
            intervalDate.setActiveUserNum(intervalDate.getActiveUserNum() + nowDayData.getActiveUserNum());
            intervalDate.setOrderUserNum(intervalDate.getOrderUserNum() + nowDayData.getOrderUserNum());
            intervalDate.setRechargeUserNum(intervalDate.getRechargeUserNum() + nowDayData.getRechargeUserNum());
            intervalDate.setOrderPayUserNum(intervalDate.getOrderPayUserNum() + nowDayData.getOrderPayUserNum());
            intervalDate.setPayOrderAmount(intervalDate.getPayOrderAmount().add(nowDayData.getPayOrderAmount()));
            intervalDate.setCustomerPrice(commonCustomerPrice(intervalDate.getPayOrderAmount(), intervalDate.getOrderPayUserNum()));
        } else {
            intervalDate = getDataByPeriod(startDate.toString("yyyy-MM-dd"), endDate.toString("yyyy-MM-dd"));
        }
        // 获取前一个区间的数据
        String beforeDateStr = DateUtil.offsetDay(startDate, -((int) between + 1)).toString("yyyy-MM-dd");
        String beforeEndDateStr = DateUtil.offsetDay(startDate, -1).toString("yyyy-MM-dd");
        UserOverviewResponse intervalDate2 = getSimpleDataByPeriod(beforeDateStr, beforeEndDateStr);
        // 计算环比，返回对象
        return getCommonResponse(intervalDate, intervalDate2);
    }


    /**
     * 获取用户渠道数据
     * @return List
     */
    @Override
    public List<UserChannelDataResponse> getChannelData() {
        List<User> userList = userService.getChannelData();
        return userList.stream().map(e -> {
            UserChannelDataResponse response = new UserChannelDataResponse();
            response.setChannel(e.getUserType());
            response.setNum(e.getPayCount());
            return response;
        }).collect(Collectors.toList());
    }


    /**
     * 用户概览数据列表（导出使用）
     * @param dateLimit 时间参数
     * @return UserOverviewResponse
     */
    @Override
    public List<UserOverviewDateResponse> getOverviewList(String dateLimit) {
        if (StrUtil.isBlank(dateLimit)) {
            throw new CrmebException("日期参数不能为空");
        }
        List<UserOverviewDateResponse> responsesList = CollUtil.newArrayList();
        // 判断时间类型
        if (Constants.SEARCH_DATE_DAY.equals(dateLimit)) {
            // 获取今天的数据(所有数据现查)
            UserOverviewResponse nowDayData = getDataByDate(DateUtil.date().toString("yyyy-MM-dd"));
            UserOverviewDateResponse response = new UserOverviewDateResponse();
            BeanUtil.copyProperties(nowDayData, response);
            responsesList.add(response);
            return responsesList;
        }
        if (Constants.SEARCH_DATE_YESTERDAY.equals(dateLimit)) {
            // 获取昨天的数据
            DateTime yesterday = DateUtil.yesterday();
            String yesterdayStr = yesterday.toString("yyyy-MM-dd");
            UserOverviewResponse yesterdayData = getDataByDate(yesterdayStr);
            UserOverviewDateResponse response = new UserOverviewDateResponse();
            BeanUtil.copyProperties(yesterdayData, response);
            responsesList.add(response);
            return responsesList;
        }
        // 时间段数据查询(包含7天、30天、周、月、年)
        if (Constants.SEARCH_DATE_LATELY_7.equals(dateLimit)) {// 7天
            // 分别获取今天、前6天的数据，数据相加
            DateTime nowDate = DateUtil.date();
            String beforeSevenDateStr = DateUtil.offsetDay(nowDate, -6).toString("yyyy-MM-dd");
            List<String> rangeDateList = getRangeDateList(beforeSevenDateStr, nowDate.toString("yyyy-MM-dd"));
            rangeDateList.forEach(e -> {
                UserOverviewResponse dataByDate = getDataByDate(e);
                UserOverviewDateResponse response = new UserOverviewDateResponse();
                BeanUtil.copyProperties(dataByDate, response);
                responsesList.add(response);
            });
            return responsesList;
        }
        if (Constants.SEARCH_DATE_LATELY_30.equals(dateLimit)) {// 30天
            DateTime nowDate = DateUtil.date();
            String beforeSevenDateStr = DateUtil.offsetDay(nowDate, -29).toString("yyyy-MM-dd");
            List<String> rangeDateList = getRangeDateList(beforeSevenDateStr, nowDate.toString("yyyy-MM-dd"));
            rangeDateList.forEach(e -> {
                UserOverviewResponse dataByDate = getDataByDate(e);
                UserOverviewDateResponse response = new UserOverviewDateResponse();
                BeanUtil.copyProperties(dataByDate, response);
                responsesList.add(response);
            });
            return responsesList;
        }
        if (Constants.SEARCH_DATE_WEEK.equals(dateLimit)) {// 周
            // 判断今天是否是本周第一天
            int dayOfWeek = DateUtil.thisDayOfWeek();
            DateTime nowDate = DateUtil.date();
            if (dayOfWeek == 2) {// 周一
                UserOverviewResponse nowDayData = getDataByDate(nowDate.toString("yyyy-MM-dd"));
                UserOverviewDateResponse response = new UserOverviewDateResponse();
                BeanUtil.copyProperties(nowDayData, response);
                responsesList.add(response);
                return responsesList;
            } else if (dayOfWeek == 3) {// 周二
                // 获取昨天的数据
                String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
                List<String> rangeDateList = getRangeDateList(yesterdayStr, nowDate.toString("yyyy-MM-dd"));
                rangeDateList.forEach(e -> {
                    UserOverviewResponse dataByDate = getDataByDate(e);
                    UserOverviewDateResponse response = new UserOverviewDateResponse();
                    BeanUtil.copyProperties(dataByDate, response);
                    responsesList.add(response);
                });
                return responsesList;
            } else {
                // 获取周一到现在的数据
                String weekStartDateStr = DateUtil.beginOfWeek(nowDate).toString("yyyy-MM-dd");
                List<String> rangeDateList = getRangeDateList(weekStartDateStr, nowDate.toString("yyyy-MM-dd"));
                rangeDateList.forEach(e -> {
                    UserOverviewResponse dataByDate = getDataByDate(e);
                    UserOverviewDateResponse response = new UserOverviewDateResponse();
                    BeanUtil.copyProperties(dataByDate, response);
                    responsesList.add(response);
                });
                return responsesList;
            }
        }
        if (Constants.SEARCH_DATE_MONTH.equals(dateLimit)) {// 月
            // 判断今天是否是本月第一天
            int dayOfMonth = DateUtil.thisDayOfMonth();
            DateTime nowDate = DateUtil.date();
            if (dayOfMonth == 1) {// 一号
                UserOverviewResponse nowDayData = getDataByDate(nowDate.toString("yyyy-MM-dd"));
                UserOverviewDateResponse response = new UserOverviewDateResponse();
                BeanUtil.copyProperties(nowDayData, response);
                responsesList.add(response);
                return responsesList;
            } else if (dayOfMonth == 2) {// 二号
                // 获取昨天的数据
                String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
                List<String> rangeDateList = getRangeDateList(yesterdayStr, nowDate.toString("yyyy-MM-dd"));
                rangeDateList.forEach(e -> {
                    UserOverviewResponse dataByDate = getDataByDate(e);
                    UserOverviewDateResponse response = new UserOverviewDateResponse();
                    BeanUtil.copyProperties(dataByDate, response);
                    responsesList.add(response);
                });
                return responsesList;
            } else {
                // 获取月初到现在的数据
                String monthStartDateStr = DateUtil.beginOfMonth(nowDate).toString("yyyy-MM-dd");
                List<String> rangeDateList = getRangeDateList(monthStartDateStr, nowDate.toString("yyyy-MM-dd"));
                rangeDateList.forEach(e -> {
                    UserOverviewResponse dataByDate = getDataByDate(e);
                    UserOverviewDateResponse response = new UserOverviewDateResponse();
                    BeanUtil.copyProperties(dataByDate, response);
                    responsesList.add(response);
                });
                return responsesList;
            }
        }
        if (Constants.SEARCH_DATE_YEAR.equals(dateLimit)) {// 年
            // 判断今天是否是本年第一天
            DateTime nowDate = DateUtil.date();
            // 获取当年的开始日期
            String beginOfYear = DateUtil.beginOfYear(nowDate).toString("yyyy-MM-dd");
            String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
            if (beginOfYear.equals(nowDate.toString("yyyy-MM-dd"))) {// 第一天
                UserOverviewResponse nowDayData = getDataByDate(nowDate.toString("yyyy-MM-dd"));
                UserOverviewDateResponse response = new UserOverviewDateResponse();
                BeanUtil.copyProperties(nowDayData, response);
                responsesList.add(response);
                return responsesList;
            } else if (beginOfYear.equals(yesterdayStr)) {// 第二天
                List<String> rangeDateList = getRangeDateList(yesterdayStr, nowDate.toString("yyyy-MM-dd"));
                rangeDateList.forEach(e -> {
                    UserOverviewResponse dataByDate = getDataByDate(e);
                    UserOverviewDateResponse response = new UserOverviewDateResponse();
                    BeanUtil.copyProperties(dataByDate, response);
                    responsesList.add(response);
                });
                return responsesList;
            } else {
                List<String> rangeDateList = getRangeDateList(beginOfYear, nowDate.toString("yyyy-MM-dd"));
                rangeDateList.forEach(e -> {
                    UserOverviewResponse dataByDate = getDataByDate(e);
                    UserOverviewDateResponse response = new UserOverviewDateResponse();
                    BeanUtil.copyProperties(dataByDate, response);
                    responsesList.add(response);
                });
                return responsesList;
            }
        }
        // 自定义时间,切割时间
        String[] split = dateLimit.split(",");
        if (split.length < 2) {
            throw new CrmebException("请选择正确的时间范围");
        }
        DateTime startDate = DateUtil.parseDate(split[0]);
        DateTime endDate = DateUtil.parseDate(split[1]);
        // 是否是同一天
        if (DateUtil.isSameDay(startDate, endDate)) {
            String dateStr = startDate.toString("yyyy-MM-dd");
            // 判断时间是否是今天
            if (DateUtil.isSameDay(startDate, DateUtil.date())) {
                UserOverviewResponse nowDayData = getDataByDate(DateUtil.date().toString("yyyy-MM-dd"));
                UserOverviewDateResponse response = new UserOverviewDateResponse();
                BeanUtil.copyProperties(nowDayData, response);
                responsesList.add(response);
                return responsesList;
            }
            // 不是今天
            UserOverviewResponse dayData = getDataByDate(dateStr);
            UserOverviewDateResponse response = new UserOverviewDateResponse();
            BeanUtil.copyProperties(dayData, response);
            responsesList.add(response);
            return responsesList;
        }
        // 判断开始时间是否大于结束时间
        if (startDate.compareTo(endDate) >= 0) {
            throw new CrmebException("请选择正确的时间范围");
        }
        // 获取两个日期间隔了多少天
        List<String> rangeDateList = getRangeDateList(startDate.toString("yyyy-MM-dd"), endDate.toString("yyyy-MM-dd"));
        rangeDateList.forEach(e -> {
            UserOverviewResponse dataByDate = getDataByDate(e);
            UserOverviewDateResponse response = new UserOverviewDateResponse();
            BeanUtil.copyProperties(dataByDate, response);
            responsesList.add(response);
        });
        return responsesList;
    }

    /**
     * 根据日期获取数据
     * @param date 日期
     * @return UserOverviewResponse
     */
    private UserOverviewResponse getDataByDate(String date) {
        // 注册用户数
        Integer registerNum = userService.getRegisterNumByDate(date);
        // 浏览量
        Integer pageviews = userVisitRecordService.getPageviewsByDate(date);
        // 活跃用户数
        Integer activeUserNum = userVisitRecordService.getActiveUserNumByDate(date);
        // 下单用户数
        Integer orderUserNum = storeOrderService.getOrderUserNumByDate(date);
        // 充值用户数
        Integer rechargeUserNum = userRechargeService.getRechargeUserNumByDate(date);
        // 成交用户数(下单且支付)
        Integer orderPayUserNum = storeOrderService.getOrderPayUserNumByDate(date);
        // 成交金额
        BigDecimal payOrderAmount = storeOrderService.getPayOrderAmountByDate(date);
        // 客单价
        BigDecimal customerPrice = commonCustomerPrice(payOrderAmount, orderPayUserNum);

        UserOverviewResponse response = new UserOverviewResponse();
        response.setRegisterNum(registerNum);
        response.setPageviews(pageviews);
        response.setActiveUserNum(activeUserNum);
        response.setOrderUserNum(orderUserNum);
        response.setRechargeUserNum(rechargeUserNum);
        response.setOrderPayUserNum(orderPayUserNum);
        response.setPayOrderAmount(payOrderAmount);
        response.setCustomerPrice(customerPrice);
        return response;
    }

    /**
     * 根据日期获取数据（简版）
     * @param date 日期
     * @return UserOverviewResponse
     */
    private UserOverviewResponse getSimpleDataByDate(String date) {
        // 注册用户数
        Integer registerNum = userService.getRegisterNumByDate(date);
        // 活跃用户数
        Integer activeUserNum = userVisitRecordService.getActiveUserNumByDate(date);
        // 充值用户数
        Integer rechargeUserNum = userRechargeService.getRechargeUserNumByDate(date);

        UserOverviewResponse response = new UserOverviewResponse();
        response.setRegisterNum(registerNum);
        response.setActiveUserNum(activeUserNum);
        response.setRechargeUserNum(rechargeUserNum);
        return response;
    }

    /**
     * 根据日期段获取数据
     * @param startDate 日期
     * @param endDate 日期
     * @return UserOverviewResponse
     */
    private UserOverviewResponse getDataByPeriod(String startDate, String endDate) {
        // 注册用户数
        Integer registerNum = userService.getRegisterNumByPeriod(startDate, endDate);
        // 浏览量
        Integer pageviews = userVisitRecordService.getPageviewsByPeriod(startDate, endDate);
        // 活跃用户数
        Integer activeUserNum = userVisitRecordService.getActiveUserNumByPeriod(startDate, endDate);
        // 下单用户数
        Integer orderUserNum = storeOrderService.getOrderUserNumByPeriod(startDate, endDate);
        // 充值用户数
        Integer rechargeUserNum = userRechargeService.getRechargeUserNumByPeriod(startDate, endDate);
        // 成交用户数(下单且支付)
        Integer orderPayUserNum = storeOrderService.getOrderPayUserNumByPeriod(startDate, endDate);
        // 成交金额
        BigDecimal payOrderAmount = storeOrderService.getPayOrderAmountByPeriod(startDate, endDate);
        // 客单价
        BigDecimal customerPrice = commonCustomerPrice(payOrderAmount, orderPayUserNum);

        UserOverviewResponse response = new UserOverviewResponse();
        response.setRegisterNum(registerNum);
        response.setPageviews(pageviews);
        response.setActiveUserNum(activeUserNum);
        response.setOrderUserNum(orderUserNum);
        response.setRechargeUserNum(rechargeUserNum);
        response.setOrderPayUserNum(orderPayUserNum);
        response.setPayOrderAmount(payOrderAmount);
        response.setCustomerPrice(customerPrice);
        return response;
    }

    /**
     * 根据日期段获取数据（简版）
     * @param startDate 日期
     * @param endDate 日期
     * @return UserOverviewResponse
     */
    private UserOverviewResponse getSimpleDataByPeriod(String startDate, String endDate) {
        // 注册用户数
        Integer registerNum = userService.getRegisterNumByPeriod(startDate, endDate);
        // 活跃用户数
        Integer activeUserNum = userVisitRecordService.getActiveUserNumByPeriod(startDate, endDate);
        // 充值用户数
        Integer rechargeUserNum = userRechargeService.getRechargeUserNumByPeriod(startDate, endDate);

        UserOverviewResponse response = new UserOverviewResponse();
        response.setRegisterNum(registerNum);
        response.setActiveUserNum(activeUserNum);
        response.setRechargeUserNum(rechargeUserNum);
        return response;
    }

    /**
     * 客单价计算
     * @param amount 支付总金额
     * @param peopleNum 成交用户数（去重）
     * @return BigDecimal
     */
    private BigDecimal commonCustomerPrice(BigDecimal amount, Integer peopleNum) {
        if (peopleNum.equals(0)) {
            return BigDecimal.ZERO;
        }
        if (BigDecimal.ZERO.compareTo(amount) == 0) {
            return BigDecimal.ZERO;
        }
        return amount.divide(BigDecimal.valueOf(peopleNum), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 计算环比并返回对象
     * @param response 主对象
     * @param tempResponse 临时对象
     * @return UserOverviewResponse
     */
    private UserOverviewResponse getCommonResponse(UserOverviewResponse response, UserOverviewResponse tempResponse) {
        response.setRegisterNumRatio(calculateRatio(response.getRegisterNum(), tempResponse.getRegisterNum()));
        response.setActiveUserNumRatio(calculateRatio(response.getActiveUserNum(), tempResponse.getActiveUserNum()));
        response.setRechargeUserNumRatio(calculateRatio(response.getRechargeUserNum(), tempResponse.getRechargeUserNum()));
        return response;
    }

    /**
     * 计算环比
     * @param data 数值
     * @param beforeData 上一期数值
     * @return 环比率
     * （本期数 - 上期数）/上期数量*100%
     */
    private String calculateRatio(Integer data, Integer beforeData) {
        if (data - beforeData == 0) {
            return "0%";
        }
        if (beforeData.equals(0)) {
            return "100%";
        }
        BigDecimal bigDecimal = new BigDecimal(data);
        BigDecimal beforeBigDecimal = new BigDecimal(beforeData);
        BigDecimal ratio = bigDecimal.subtract(beforeBigDecimal).divide(beforeBigDecimal, 2, BigDecimal.ROUND_HALF_UP);
        int intValue = ratio.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue();
        return intValue + "%";
    }

    /**
     * 获取区域列表(省份)
     * @return List
     */
    private List<String> getAreaList() {
        List<String> list = CollUtil.newArrayList();
        list.add("北京");
        list.add("天津");
        list.add("河北");
        list.add("山西");
        list.add("内蒙古");
        list.add("辽宁");
        list.add("吉林");
        list.add("黑龙江");
        list.add("上海");
        list.add("江苏");
        list.add("浙江");
        list.add("安徽");
        list.add("福建");
        list.add("江西");
        list.add("山东");
        list.add("河南");
        list.add("湖北");
        list.add("湖南");
        list.add("广东");
        list.add("广西");
        list.add("海南");
        list.add("重庆");
        list.add("四川");
        list.add("贵州");
        list.add("云南");
        list.add("西藏");
        list.add("陕西");
        list.add("甘肃");
        list.add("青海");
        list.add("宁夏");
        list.add("新疆");
        list.add("香港");
        list.add("澳门");
        list.add("台湾");
        return list;
    }

    /**
     * 获取时间区间列表
     */
    private List<String> getRangeDateList(String startDate, String endDate) {
        List<String> dateList = CollUtil.newArrayList();
        if (startDate.equals(endDate)) {
            dateList.add(startDate);
            return dateList;
        }
        List<DateTime> dateTimeList = DateUtil.rangeToList(DateUtil.parse(startDate), DateUtil.parse(endDate), DateField.DAY_OF_YEAR);
        dateTimeList.forEach(e -> {
            dateList.add(e.toString("yyyy-MM-dd"));
        });
        return dateList;
    }
}
