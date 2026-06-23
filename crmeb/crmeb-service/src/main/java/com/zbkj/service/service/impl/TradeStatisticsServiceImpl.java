package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.record.TradingDayRecord;
import com.zbkj.common.response.TradeDataResponse;
import com.zbkj.common.response.TradingDataResponse;
import com.zbkj.common.response.TrandeTrendDateResponse;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TradeStatisticsService 接口实现
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
public class TradeStatisticsServiceImpl implements TradeStatisticsService {

    @Autowired
    private TradingDayRecordService tradingDayRecordService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderStatusService storeOrderStatusService;

    @Autowired
    private UserRechargeService userRechargeService;

    @Autowired
    private UserBrokerageRecordService brokerageRecordService;

    /**
     * 交易统计数据
     * @return TradeDataResponse
     */
    @Override
    public TradeDataResponse getData() {
        TradeDataResponse response = new TradeDataResponse();

        DateTime date = DateUtil.date();
        String nowDateStr = date.toString("yyyy-MM-dd");
        // 今天订单数量
        Integer todayOrderNum = storeOrderService.getOrderNumByDate(nowDateStr);
        response.setTodayOrderNum(todayOrderNum);

        // 昨日订单数量
        // 昨日交易金额
        String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
        TradingDayRecord yesterdayRecord = tradingDayRecordService.getByDate(yesterdayStr);
        String beforeDayStr = DateUtil.offsetDay(date, -2).toString("yyyy-MM-dd");
        TradingDayRecord beforeDayRecord = tradingDayRecordService.getByDate(beforeDayStr);
        response.setYesterdayOrderNum(yesterdayRecord.getProductOrderNum());
        response.setYesterdayOrderSales(yesterdayRecord.getProductOrderPayFee());
        response.setYesterdayOrderNumRatio(calculateRatio(yesterdayRecord.getProductOrderNum(), beforeDayRecord.getProductOrderNum()));
        response.setYesterdayOrderSalesRatio(calculateRatio(yesterdayRecord.getProductOrderPayFee(), beforeDayRecord.getProductOrderPayFee()));
        // 本月交易数量
        // 本月交易金额
        // 获取本月的开始时间
        DateTime monthStartDate = DateUtil.beginOfMonth(date);
        String monthStartDateStr = monthStartDate.toString("yyyy-MM-dd");
        String monthEndDateStr = DateUtil.endOfMonth(date).toString("yyyy-MM-dd");
        TradingDayRecord monthDateRecord = tradingDayRecordService.getByTimeInterval(monthStartDateStr, monthEndDateStr);
        DateTime beforeMonthEndDate = DateUtil.offsetDay(monthStartDate, -1);
        String beforeMonthStartDateStr = DateUtil.beginOfMonth(beforeMonthEndDate).toString("yyyy-MM-dd");
        String beforeMonthEndDateStr = beforeMonthEndDate.toString("yyyy-MM-dd");
        TradingDayRecord beforeMonthDateRecord = tradingDayRecordService.getByTimeInterval(beforeMonthStartDateStr, beforeMonthEndDateStr);
        response.setMonthOrderNum(monthDateRecord.getProductOrderNum());
        response.setMonthOrderSales(monthDateRecord.getProductOrderPayFee());
        response.setMonthOrderNumRatio(calculateRatio(monthDateRecord.getProductOrderNum(), beforeMonthDateRecord.getProductOrderNum()));
        response.setMonthOrderSalesRatio(calculateRatio(monthDateRecord.getProductOrderPayFee(), beforeMonthDateRecord.getProductOrderPayFee()));
        return response;
    }

    /**
     * 交易统计概览
     * @param dateLimit 时间参数
     * @return TradingDataResponse
     */
    @Override
    public TradingDataResponse getOverview(String dateLimit) {
        if (StrUtil.isBlank(dateLimit)) {
            throw new CrmebException("日期参数不能为空");
        }
        // 判断时间类型
        if (Constants.SEARCH_DATE_DAY.equals(dateLimit)) {
            // 获取今天的数据(所有数据现查)
            TradingDayRecord nowDayData = getTradeNowDate();
            // 获取昨天的数据
            DateTime yesterday = DateUtil.yesterday();
            String yesterdayStr = yesterday.toString("yyyy-MM-dd");
            TradingDayRecord yesterdayData = tradingDayRecordService.getByDate(yesterdayStr);
            // 计算环比，返回对象
            return getCommonResponse(nowDayData, yesterdayData);
        }
        if (Constants.SEARCH_DATE_YESTERDAY.equals(dateLimit)) {
            // 获取昨天的数据
            DateTime yesterday = DateUtil.yesterday();
            String yesterdayStr = yesterday.toString("yyyy-MM-dd");
            TradingDayRecord yesterdayData = tradingDayRecordService.getByDate(yesterdayStr);
            // 获取更前一天的数据
            String beforeDay = DateUtil.offsetDay(yesterday, -1).toString("yyyy-MM-dd");
            TradingDayRecord beforeDayDta = tradingDayRecordService.getByDate(beforeDay);
            // 计算环比，返回对象
            return getCommonResponse(yesterdayData, beforeDayDta);
        }
        // 时间段数据查询(包含7天、30天、周、月、年)
        if (Constants.SEARCH_DATE_LATELY_7.equals(dateLimit)) {// 7天
            // 分别获取今天、前6天的数据，数据相加
            DateTime nowDate = DateUtil.date();
            // 获取今天的数据(所有数据现查)
            TradingDayRecord nowDayData = getTradeNowDate();
            // 获取往前推七天的日期跟昨天的日期
            String beforeSevenDateStr = DateUtil.offsetDay(nowDate, -6).toString("yyyy-MM-dd");
            String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
            TradingDayRecord intervalDate = tradingDayRecordService.getByTimeInterval(beforeSevenDateStr, yesterdayStr);
            intervalDate.setProductOrderNum(intervalDate.getProductOrderNum() + nowDayData.getProductOrderNum());
            intervalDate.setProductOrderPayNum(intervalDate.getProductOrderPayNum() + nowDayData.getProductOrderPayNum());
            intervalDate.setProductOrderPayFee(intervalDate.getProductOrderPayFee().add(nowDayData.getProductOrderPayFee()));
            intervalDate.setProductOrderRefundNum(intervalDate.getProductOrderRefundNum() + nowDayData.getProductOrderRefundNum());
            intervalDate.setProductOrderRefundFee(intervalDate.getProductOrderRefundFee().add(nowDayData.getProductOrderRefundFee()));
            intervalDate.setRechargeOrderNum(intervalDate.getRechargeOrderNum() + nowDayData.getRechargeOrderNum());
            intervalDate.setRechargeOrderFee(intervalDate.getRechargeOrderFee().add(nowDayData.getRechargeOrderFee()));
            intervalDate.setBalancePayFee(intervalDate.getBalancePayFee().add(nowDayData.getBalancePayFee()));
            intervalDate.setBrokerageFee(intervalDate.getBrokerageFee().add(nowDayData.getBrokerageFee()));

            // 获取前14天-前8天这个区间的数据
            String beforeFourteenDateStr = DateUtil.offsetDay(nowDate, -13).toString("yyyy-MM-dd");
            String beforeEightDateStr = DateUtil.offsetDay(nowDate, -7).toString("yyyy-MM-dd");
            TradingDayRecord intervalDate2 = tradingDayRecordService.getByTimeInterval(beforeFourteenDateStr, beforeEightDateStr);
            // 计算环比，返回对象
            return getCommonResponse(intervalDate, intervalDate2);
        }
        if (Constants.SEARCH_DATE_LATELY_30.equals(dateLimit)) {// 30天
            DateTime nowDate = DateUtil.date();
            // 获取今天的数据(所有数据现查)
            TradingDayRecord nowDayData = getTradeNowDate();
            // 获取往前推30天的日期跟昨天的日期
            String beforeSevenDateStr = DateUtil.offsetDay(nowDate, -29).toString("yyyy-MM-dd");
            String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
            TradingDayRecord intervalDate = tradingDayRecordService.getByTimeInterval(beforeSevenDateStr, yesterdayStr);
            intervalDate.setProductOrderNum(intervalDate.getProductOrderNum() + nowDayData.getProductOrderNum());
            intervalDate.setProductOrderPayNum(intervalDate.getProductOrderPayNum() + nowDayData.getProductOrderPayNum());
            intervalDate.setProductOrderPayFee(intervalDate.getProductOrderPayFee().add(nowDayData.getProductOrderPayFee()));
            intervalDate.setProductOrderRefundNum(intervalDate.getProductOrderRefundNum() + nowDayData.getProductOrderRefundNum());
            intervalDate.setProductOrderRefundFee(intervalDate.getProductOrderRefundFee().add(nowDayData.getProductOrderRefundFee()));
            intervalDate.setRechargeOrderNum(intervalDate.getRechargeOrderNum() + nowDayData.getRechargeOrderNum());
            intervalDate.setRechargeOrderFee(intervalDate.getRechargeOrderFee().add(nowDayData.getRechargeOrderFee()));
            intervalDate.setBalancePayFee(intervalDate.getBalancePayFee().add(nowDayData.getBalancePayFee()));
            intervalDate.setBrokerageFee(intervalDate.getBrokerageFee().add(nowDayData.getBrokerageFee()));

            // 获取前14天-前8天这个区间的数据
            String beforeFourteenDateStr = DateUtil.offsetDay(nowDate, -59).toString("yyyy-MM-dd");
            String beforeEightDateStr = DateUtil.offsetDay(nowDate, -30).toString("yyyy-MM-dd");
            TradingDayRecord intervalDate2 = tradingDayRecordService.getByTimeInterval(beforeFourteenDateStr, beforeEightDateStr);
            // 计算环比，返回对象
            return getCommonResponse(intervalDate, intervalDate2);
        }
        if (Constants.SEARCH_DATE_WEEK.equals(dateLimit)) {// 周
            // 判断今天是否是本周第一天
            int dayOfWeek = DateUtil.thisDayOfWeek();
            DateTime nowDate = DateUtil.date();
            TradingDayRecord intervalDate = new TradingDayRecord();
            if (dayOfWeek == 2) {// 周一
                // 获取今天的数据(所有数据现查)
                intervalDate = getTradeNowDate();
            } else if (dayOfWeek == 3) {// 周二
                TradingDayRecord nowDayData = getTradeNowDate();
                // 获取昨天的数据
                String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
                TradingDayRecord yesterdayData = tradingDayRecordService.getByDate(yesterdayStr);
                intervalDate.setProductOrderNum(yesterdayData.getProductOrderNum() + nowDayData.getProductOrderNum());
                intervalDate.setProductOrderPayNum(yesterdayData.getProductOrderPayNum() + nowDayData.getProductOrderPayNum());
                intervalDate.setProductOrderPayFee(yesterdayData.getProductOrderPayFee().add(nowDayData.getProductOrderPayFee()));
                intervalDate.setProductOrderRefundNum(yesterdayData.getProductOrderRefundNum() + nowDayData.getProductOrderRefundNum());
                intervalDate.setProductOrderRefundFee(yesterdayData.getProductOrderRefundFee().add(nowDayData.getProductOrderRefundFee()));
                intervalDate.setRechargeOrderNum(yesterdayData.getRechargeOrderNum() + nowDayData.getRechargeOrderNum());
                intervalDate.setRechargeOrderFee(yesterdayData.getRechargeOrderFee().add(nowDayData.getRechargeOrderFee()));
                intervalDate.setBalancePayFee(yesterdayData.getBalancePayFee().add(nowDayData.getBalancePayFee()));
                intervalDate.setBrokerageFee(yesterdayData.getBrokerageFee().add(nowDayData.getBrokerageFee()));
            } else {
                TradingDayRecord nowDayData = getTradeNowDate();
                // 获取周一到现在的数据
                String weekStartDateStr = DateUtil.beginOfWeek(nowDate).toString("yyyy-MM-dd");
                String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
                intervalDate = tradingDayRecordService.getByTimeInterval(weekStartDateStr, yesterdayStr);
                intervalDate.setProductOrderNum(intervalDate.getProductOrderNum() + nowDayData.getProductOrderNum());
                intervalDate.setProductOrderPayNum(intervalDate.getProductOrderPayNum() + nowDayData.getProductOrderPayNum());
                intervalDate.setProductOrderPayFee(intervalDate.getProductOrderPayFee().add(nowDayData.getProductOrderPayFee()));
                intervalDate.setProductOrderRefundNum(intervalDate.getProductOrderRefundNum() + nowDayData.getProductOrderRefundNum());
                intervalDate.setProductOrderRefundFee(intervalDate.getProductOrderRefundFee().add(nowDayData.getProductOrderRefundFee()));
                intervalDate.setRechargeOrderNum(intervalDate.getRechargeOrderNum() + nowDayData.getRechargeOrderNum());
                intervalDate.setRechargeOrderFee(intervalDate.getRechargeOrderFee().add(nowDayData.getRechargeOrderFee()));
                intervalDate.setBalancePayFee(intervalDate.getBalancePayFee().add(nowDayData.getBalancePayFee()));
                intervalDate.setBrokerageFee(intervalDate.getBrokerageFee().add(nowDayData.getBrokerageFee()));
            }

            // 获取上一周的数据
            DateTime offsetWeek = DateUtil.offsetWeek(nowDate, -1);
            String beforeWeekStartDateStr = DateUtil.beginOfWeek(offsetWeek).toString("yyyy-MM-dd");
            String beforeWeekEndDateStr = DateUtil.endOfWeek(offsetWeek).toString("yyyy-MM-dd");
            TradingDayRecord intervalDate2 = tradingDayRecordService.getByTimeInterval(beforeWeekStartDateStr, beforeWeekEndDateStr);
            // 计算环比，返回对象
            return getCommonResponse(intervalDate, intervalDate2);
        }
        if (Constants.SEARCH_DATE_MONTH.equals(dateLimit)) {// 月
            // 判断今天是否是本月第一天
            int dayOfMonth = DateUtil.thisDayOfMonth();
            DateTime nowDate = DateUtil.date();
            TradingDayRecord intervalDate = new TradingDayRecord();
            if (dayOfMonth == 1) {// 一号
                // 获取今天的数据(所有数据现查)
                intervalDate = getTradeNowDate();
            } else if (dayOfMonth == 2) {// 二号
                TradingDayRecord nowDayData = getTradeNowDate();
                // 获取昨天的数据
                String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
                TradingDayRecord yesterdayData = tradingDayRecordService.getByDate(yesterdayStr);
                intervalDate.setProductOrderNum(yesterdayData.getProductOrderNum() + nowDayData.getProductOrderNum());
                intervalDate.setProductOrderPayNum(yesterdayData.getProductOrderPayNum() + nowDayData.getProductOrderPayNum());
                intervalDate.setProductOrderPayFee(yesterdayData.getProductOrderPayFee().add(nowDayData.getProductOrderPayFee()));
                intervalDate.setProductOrderRefundNum(yesterdayData.getProductOrderRefundNum() + nowDayData.getProductOrderRefundNum());
                intervalDate.setProductOrderRefundFee(yesterdayData.getProductOrderRefundFee().add(nowDayData.getProductOrderRefundFee()));
                intervalDate.setRechargeOrderNum(yesterdayData.getRechargeOrderNum() + nowDayData.getRechargeOrderNum());
                intervalDate.setRechargeOrderFee(yesterdayData.getRechargeOrderFee().add(nowDayData.getRechargeOrderFee()));
                intervalDate.setBalancePayFee(yesterdayData.getBalancePayFee().add(nowDayData.getBalancePayFee()));
                intervalDate.setBrokerageFee(yesterdayData.getBrokerageFee().add(nowDayData.getBrokerageFee()));
            } else {
                TradingDayRecord nowDayData = getTradeNowDate();
                // 获取月初到现在的数据
                String monthStartDateStr = DateUtil.beginOfMonth(nowDate).toString("yyyy-MM-dd");
                String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
                intervalDate = tradingDayRecordService.getByTimeInterval(monthStartDateStr, yesterdayStr);
                intervalDate.setProductOrderNum(intervalDate.getProductOrderNum() + nowDayData.getProductOrderNum());
                intervalDate.setProductOrderPayNum(intervalDate.getProductOrderPayNum() + nowDayData.getProductOrderPayNum());
                intervalDate.setProductOrderPayFee(intervalDate.getProductOrderPayFee().add(nowDayData.getProductOrderPayFee()));
                intervalDate.setProductOrderRefundNum(intervalDate.getProductOrderRefundNum() + nowDayData.getProductOrderRefundNum());
                intervalDate.setProductOrderRefundFee(intervalDate.getProductOrderRefundFee().add(nowDayData.getProductOrderRefundFee()));
                intervalDate.setRechargeOrderNum(intervalDate.getRechargeOrderNum() + nowDayData.getRechargeOrderNum());
                intervalDate.setRechargeOrderFee(intervalDate.getRechargeOrderFee().add(nowDayData.getRechargeOrderFee()));
                intervalDate.setBalancePayFee(intervalDate.getBalancePayFee().add(nowDayData.getBalancePayFee()));
                intervalDate.setBrokerageFee(intervalDate.getBrokerageFee().add(nowDayData.getBrokerageFee()));
            }

            // 获取上一月的数据
            DateTime offsetMonth = DateUtil.offsetMonth(nowDate, -1);
            String beforeMonthStartDateStr = DateUtil.beginOfMonth(offsetMonth).toString("yyyy-MM-dd");
            String beforeMonthEndDateStr = DateUtil.endOfMonth(offsetMonth).toString("yyyy-MM-dd");
            TradingDayRecord intervalDate2 = tradingDayRecordService.getByTimeInterval(beforeMonthStartDateStr, beforeMonthEndDateStr);
            // 计算环比，返回对象
            return getCommonResponse(intervalDate, intervalDate2);
        }
        if (Constants.SEARCH_DATE_YEAR.equals(dateLimit)) {// 年
            // 判断今天是否是本年第一天
            DateTime nowDate = DateUtil.date();
            TradingDayRecord intervalDate = new TradingDayRecord();
            // 获取当年的开始日期
            String beginOfYear = DateUtil.beginOfYear(nowDate).toString("yyyy-MM-dd");
            String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
            if (beginOfYear.equals(nowDate.toString("yyyy-MM-dd"))) {// 第一天
                // 获取今天的数据(所有数据现查)
                intervalDate = getTradeNowDate();
            } else if (beginOfYear.equals(yesterdayStr)) {// 第二天
                TradingDayRecord nowDayData = getTradeNowDate();
                // 获取昨天的数据
                TradingDayRecord yesterdayData = tradingDayRecordService.getByDate(yesterdayStr);
                intervalDate.setProductOrderNum(yesterdayData.getProductOrderNum() + nowDayData.getProductOrderNum());
                intervalDate.setProductOrderPayNum(yesterdayData.getProductOrderPayNum() + nowDayData.getProductOrderPayNum());
                intervalDate.setProductOrderPayFee(yesterdayData.getProductOrderPayFee().add(nowDayData.getProductOrderPayFee()));
                intervalDate.setProductOrderRefundNum(yesterdayData.getProductOrderRefundNum() + nowDayData.getProductOrderRefundNum());
                intervalDate.setProductOrderRefundFee(yesterdayData.getProductOrderRefundFee().add(nowDayData.getProductOrderRefundFee()));
                intervalDate.setRechargeOrderNum(yesterdayData.getRechargeOrderNum() + nowDayData.getRechargeOrderNum());
                intervalDate.setRechargeOrderFee(yesterdayData.getRechargeOrderFee().add(nowDayData.getRechargeOrderFee()));
                intervalDate.setBalancePayFee(yesterdayData.getBalancePayFee().add(nowDayData.getBalancePayFee()));
                intervalDate.setBrokerageFee(yesterdayData.getBrokerageFee().add(nowDayData.getBrokerageFee()));
            } else {
                TradingDayRecord nowDayData = getTradeNowDate();
                // 获取年初到现在的数据
                intervalDate = tradingDayRecordService.getByTimeInterval(beginOfYear, yesterdayStr);
                intervalDate.setProductOrderNum(intervalDate.getProductOrderNum() + nowDayData.getProductOrderNum());
                intervalDate.setProductOrderPayNum(intervalDate.getProductOrderPayNum() + nowDayData.getProductOrderPayNum());
                intervalDate.setProductOrderPayFee(intervalDate.getProductOrderPayFee().add(nowDayData.getProductOrderPayFee()));
                intervalDate.setProductOrderRefundNum(intervalDate.getProductOrderRefundNum() + nowDayData.getProductOrderRefundNum());
                intervalDate.setProductOrderRefundFee(intervalDate.getProductOrderRefundFee().add(nowDayData.getProductOrderRefundFee()));
                intervalDate.setRechargeOrderNum(intervalDate.getRechargeOrderNum() + nowDayData.getRechargeOrderNum());
                intervalDate.setRechargeOrderFee(intervalDate.getRechargeOrderFee().add(nowDayData.getRechargeOrderFee()));
                intervalDate.setBalancePayFee(intervalDate.getBalancePayFee().add(nowDayData.getBalancePayFee()));
                intervalDate.setBrokerageFee(intervalDate.getBrokerageFee().add(nowDayData.getBrokerageFee()));
            }

            // 获取上一年的数据

            DateTime offsetYear = DateUtil.offsetDay(DateUtil.beginOfYear(nowDate), -1);
            String beforeYearStartDateStr = DateUtil.beginOfYear(offsetYear).toString("yyyy-MM-dd");
            String beforeYearEndDateStr = DateUtil.endOfYear(offsetYear).toString("yyyy-MM-dd");
            TradingDayRecord intervalDate2 = tradingDayRecordService.getByTimeInterval(beforeYearStartDateStr, beforeYearEndDateStr);
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
                TradingDayRecord nowDayData = getTradeNowDate();
                // 获取昨天的数据
                DateTime yesterday = DateUtil.yesterday();
                String yesterdayStr = yesterday.toString("yyyy-MM-dd");
                TradingDayRecord yesterdayData = tradingDayRecordService.getByDate(yesterdayStr);
                // 计算环比，返回对象
                return getCommonResponse(nowDayData, yesterdayData);
            }
            // 不是今天
            TradingDayRecord dayData = tradingDayRecordService.getByDate(dateStr);
            String beforeDateStr = DateUtil.offsetDay(startDate, -1).toString("yyyy-MM-dd");
            TradingDayRecord beforeDayData = tradingDayRecordService.getByDate(beforeDateStr);
            // 计算环比，返回对象
            return getCommonResponse(dayData, beforeDayData);
        }
        // 判断开始时间是否大于结束时间
        if (startDate.compareTo(endDate) >= 0) {
            throw new CrmebException("请选择正确的时间范围");
        }
        // 获取两个日期间隔了多少天
        long between = DateUtil.between(startDate, endDate, DateUnit.DAY);
        TradingDayRecord intervalDate = new TradingDayRecord();
        // 判断结束时间是否是今天
        if (DateUtil.isSameDay(startDate, DateUtil.date())) {
            // 获取今天的数据(所有数据现查)
            TradingDayRecord nowDayData = getTradeNowDate();
            String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
            intervalDate = tradingDayRecordService.getByTimeInterval(startDate.toString("yyyy-MM-dd"), yesterdayStr);
            intervalDate.setProductOrderNum(intervalDate.getProductOrderNum() + nowDayData.getProductOrderNum());
            intervalDate.setProductOrderPayNum(intervalDate.getProductOrderPayNum() + nowDayData.getProductOrderPayNum());
            intervalDate.setProductOrderPayFee(intervalDate.getProductOrderPayFee().add(nowDayData.getProductOrderPayFee()));
            intervalDate.setProductOrderRefundNum(intervalDate.getProductOrderRefundNum() + nowDayData.getProductOrderRefundNum());
            intervalDate.setProductOrderRefundFee(intervalDate.getProductOrderRefundFee().add(nowDayData.getProductOrderRefundFee()));
            intervalDate.setRechargeOrderNum(intervalDate.getRechargeOrderNum() + nowDayData.getRechargeOrderNum());
            intervalDate.setRechargeOrderFee(intervalDate.getRechargeOrderFee().add(nowDayData.getRechargeOrderFee()));
            intervalDate.setBalancePayFee(intervalDate.getBalancePayFee().add(nowDayData.getBalancePayFee()));
            intervalDate.setBrokerageFee(intervalDate.getBrokerageFee().add(nowDayData.getBrokerageFee()));
        } else {
            intervalDate = tradingDayRecordService.getByTimeInterval(startDate.toString("yyyy-MM-dd"), endDate.toString("yyyy-MM-dd"));
        }
        // 获取前一个区间的数据
        String beforeDateStr = DateUtil.offsetDay(startDate, -((int) between + 1)).toString("yyyy-MM-dd");
        String beforeEndDateStr = DateUtil.offsetDay(startDate, -1).toString("yyyy-MM-dd");
        TradingDayRecord intervalDate2 = tradingDayRecordService.getByTimeInterval(beforeDateStr, beforeEndDateStr);
        // 计算环比，返回对象
        return getCommonResponse(intervalDate, intervalDate2);
    }

    /**
     * 交易趋势数据
     * @param dateLimit 时间参数
     * @return List
     */
    @Override
    public List<TrandeTrendDateResponse> getTrendDataByDate(String dateLimit) {
        String startDateStr;
        String endDateStr;

        // 时间段数据查询(包含7天、30天、周、月、年)
        if (Constants.SEARCH_DATE_LATELY_7.equals(dateLimit)) {// 7天
            DateTime nowDate = DateUtil.date();
            startDateStr = DateUtil.offsetDay(nowDate, -7).toString("yyyy-MM-dd");
            endDateStr = DateUtil.offsetDay(nowDate, -1).toString("yyyy-MM-dd");
            List<TradingDayRecord> dayRecordList = tradingDayRecordService.findTrendDataOfBetween(startDateStr, endDateStr);
            List<TrandeTrendDateResponse> responseList = dayRecordList.stream().map(e -> {
                TrandeTrendDateResponse response = new TrandeTrendDateResponse();
                BeanUtils.copyProperties(e, response);
                response.setDate(e.getDate());
                response.setTurnover(e.getProductOrderPayFee().add(e.getRechargeOrderFee()));
                response.setProPayAmount(e.getProductOrderPayFee());
                response.setRechargeAmount(e.getRechargeOrderFee());
                response.setPayoutAmount(e.getBalancePayFee().add(e.getBrokerageFee()).add(e.getProductOrderRefundFee()));
                return response;
            }).collect(Collectors.toList());
            return responseList;
        }
        if (Constants.SEARCH_DATE_LATELY_30.equals(dateLimit)) {// 30天
            DateTime nowDate = DateUtil.date();
            startDateStr = DateUtil.offsetDay(nowDate, -30).toString("yyyy-MM-dd");
            endDateStr = DateUtil.offsetDay(nowDate, -1).toString("yyyy-MM-dd");
            List<TradingDayRecord> dayRecordList = tradingDayRecordService.findTrendDataOfBetween(startDateStr, endDateStr);
            List<TrandeTrendDateResponse> responseList = dayRecordList.stream().map(e -> {
                TrandeTrendDateResponse response = new TrandeTrendDateResponse();
                response.setDate(e.getDate());
                response.setTurnover(e.getProductOrderPayFee().add(e.getRechargeOrderFee()));
                response.setProPayAmount(e.getProductOrderPayFee());
                response.setRechargeAmount(e.getRechargeOrderFee());
                response.setPayoutAmount(e.getBalancePayFee().add(e.getBrokerageFee()).add(e.getProductOrderRefundFee()));
                return response;
            }).collect(Collectors.toList());
            return responseList;
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
            throw new CrmebException("请选择正确的时间范围");
        }
        // 判断开始时间是否大于结束时间
        if (startDate.compareTo(endDate) >= 0) {
            throw new CrmebException("请选择正确的时间范围");
        }
        startDateStr = startDate.toString("yyyy-MM-dd");
        endDateStr = endDate.toString("yyyy-MM-dd");

        List<TradingDayRecord> dayRecordList =  tradingDayRecordService.findTrendDataOfBetween(startDateStr, endDateStr);
        List<TrandeTrendDateResponse> responseList = dayRecordList.stream().map(e -> {
            TrandeTrendDateResponse response = new TrandeTrendDateResponse();
            response.setDate(e.getDate());
            response.setTurnover(e.getProductOrderPayFee().add(e.getRechargeOrderFee()));
            response.setProPayAmount(e.getProductOrderPayFee());
            response.setRechargeAmount(e.getRechargeOrderFee());
            response.setPayoutAmount(e.getBalancePayFee().add(e.getBrokerageFee()).add(e.getProductOrderRefundFee()));
            return response;
        }).collect(Collectors.toList());
        return responseList;
    }

    /**
     * 计算环比
     * @param data 数值
     * @param beforeData 上一期数值
     * @return 环比率
     * （本期数 - 上期数）/上期数量*100%
     */
    private String calculateRatio(Integer data, Integer beforeData) {
        if (data.equals(0)) {
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
     * 计算环比
     * @param data 数值
     * @param beforeData 上一期数值
     * @return 环比率
     * （本期数 - 上期数）/上期数量*100%
     */
    private String calculateRatio(BigDecimal data, BigDecimal beforeData) {
        if (BigDecimal.ZERO.compareTo(data) == 0) {
            return "0%";
        }
        if (BigDecimal.ZERO.compareTo(beforeData) == 0) {
            return "100%";
        }
        BigDecimal ratio = data.subtract(beforeData).divide(beforeData, 2, BigDecimal.ROUND_HALF_UP);
        int intValue = ratio.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue();
        return intValue + "%";
    }

    /**
     * 获取今日商品（商城维度）数据
     */
    private TradingDayRecord getTradeNowDate() {
        String todayStr = DateUtil.date().toString("yyyy-MM-dd");
        // 订单数量
        Integer orderNum = storeOrderService.getOrderNumByDate(todayStr);

        // 订单支付数量
        Integer payOrderNum = storeOrderService.getPayOrderNumByDate(todayStr);

        // 订单支付金额(是否包含余额支付金额)
        BigDecimal payOrderAmount = storeOrderService.getPayOrderAmountByDate(todayStr);

        // 订单退款数量
        Integer refundOrderNum = storeOrderStatusService.getRefundOrderNumByDate(todayStr);

        // 订单退款金额
        BigDecimal refundOrderAmount = storeOrderStatusService.getRefundOrderAmountByDate(todayStr);

        // 充值订单数量
        Integer rechargeOrderNum = userRechargeService.getRechargeOrderNumByDate(todayStr);

        // 充值订单金额
        BigDecimal rechargeOrderAmount = userRechargeService.getRechargeOrderAmountByDate(todayStr);

        // 余额支付金额
        BigDecimal yuePayOrderAmount = storeOrderService.getYuePayOrderAmountByDate(todayStr);

        // 支付佣金金额（用户确认到账佣金）
        BigDecimal brokerageAmount = brokerageRecordService.getBrokerageAmountByDate(todayStr);

        TradingDayRecord tradingDayRecord = new TradingDayRecord();
        tradingDayRecord.setDate(todayStr);
        tradingDayRecord.setProductOrderNum(orderNum);
        tradingDayRecord.setProductOrderPayNum(payOrderNum);
        tradingDayRecord.setProductOrderPayFee(payOrderAmount);
        tradingDayRecord.setProductOrderRefundNum(refundOrderNum);
        tradingDayRecord.setProductOrderRefundFee(refundOrderAmount);
        tradingDayRecord.setRechargeOrderNum(rechargeOrderNum);
        tradingDayRecord.setRechargeOrderFee(rechargeOrderAmount);
        tradingDayRecord.setBalancePayFee(yuePayOrderAmount);
        tradingDayRecord.setBrokerageFee(brokerageAmount);
        return tradingDayRecord;
    }

    /**
     * 计算环比，返回响应对象
     * @param dataRecord 数据记录
     * @param beforeDataRecord 上一期数据记录
     * @return ShoppingProductDataResponse
     */
    private TradingDataResponse getCommonResponse(TradingDayRecord dataRecord, TradingDayRecord beforeDataRecord) {
        TradingDataResponse dataResponse = new TradingDataResponse();
        // 营业额
        dataResponse.setTurnover(dataRecord.getProductOrderPayFee().add(dataRecord.getRechargeOrderFee()));
        BigDecimal beforeTurnover = beforeDataRecord.getProductOrderPayFee().add(beforeDataRecord.getRechargeOrderFee());
        dataResponse.setTurnoverRatio(calculateRatio(dataResponse.getTurnover(), beforeTurnover));

        // 商品支付金额
        dataResponse.setProPayAmount(dataRecord.getProductOrderPayFee());
        dataResponse.setProPayAmountRatio(calculateRatio(dataRecord.getProductOrderPayFee(), beforeDataRecord.getProductOrderPayFee()));

        // 充值金额
        dataResponse.setRechargeAmount(dataRecord.getRechargeOrderFee());
        dataResponse.setRechargeAmountRatio(calculateRatio(dataRecord.getRechargeOrderFee(), beforeDataRecord.getRechargeOrderFee()));

        // 支出金额
        dataResponse.setPayoutAmount(dataRecord.getBalancePayFee().add(dataRecord.getBrokerageFee()).add(dataRecord.getProductOrderRefundFee()));
        BigDecimal beforePayoutAmount = beforeDataRecord.getBalancePayFee().add(beforeDataRecord.getBrokerageFee()).add(beforeDataRecord.getProductOrderRefundFee());
        dataResponse.setPayoutAmountRatio(calculateRatio(dataResponse.getPayoutAmount(), beforePayoutAmount));

        // 余额支付金额
        dataResponse.setBalanceAmount(dataRecord.getBalancePayFee());
        dataResponse.setBalanceAmountRatio(calculateRatio(dataRecord.getBalancePayFee(), beforeDataRecord.getBalancePayFee()));

        // 支付佣金金额
        dataResponse.setPayoutBrokerageAmount(dataRecord.getBrokerageFee());
        dataResponse.setPayoutBrokerageAmountRatio(calculateRatio(dataRecord.getBrokerageFee(), beforeDataRecord.getBrokerageFee()));

        // 商品退款金额
        dataResponse.setProRefundAmount(dataRecord.getProductOrderRefundFee());
        dataResponse.setProRefundAmountRatio(calculateRatio(dataRecord.getProductOrderRefundFee(), beforeDataRecord.getProductOrderRefundFee()));
        return dataResponse;
    }
}
