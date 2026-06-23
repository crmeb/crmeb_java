package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.RedisConstatns;
import com.zbkj.common.exception.CrmebException;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.model.record.ProductDayRecord;
import com.zbkj.common.model.record.ShoppingProductDayRecord;
import com.zbkj.common.request.ProductRankingRequest;
import com.zbkj.common.response.ProductRankingResponse;
import com.zbkj.common.response.ShoppingProductDataResponse;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * ProductStatisticsService 接口实现
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
public class ProductStatisticsServiceImpl implements ProductStatisticsService {

    @Autowired
    private ShoppingProductDayRecordService shoppingProductDayRecordService;

    @Autowired
    private ProductDayRecordService productDayRecordService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StoreProductRelationService productRelationService;

    @Autowired
    private StoreOrderService storeOrderService;


    /**
     * 根据日期获取统计数据(商城维度)
     * @param dateLimit 日期参数
     * @return ShoppingProductDataResponse
     */
    @Override
    public ShoppingProductDataResponse getDataByDate(String dateLimit) {
        if (StrUtil.isBlank(dateLimit)) {
            throw new CrmebException("日期参数不能为空");
        }
        // 判断时间类型
        if (Constants.SEARCH_DATE_DAY.equals(dateLimit)) {
            // 获取今天的数据(所有数据现查)
            ShoppingProductDayRecord nowDayData = getShoppingProductNowDate();
            // 获取昨天的数据
            DateTime yesterday = DateUtil.yesterday();
            String yesterdayStr = yesterday.toString("yyyy-MM-dd");
            ShoppingProductDayRecord yesterdayData = shoppingProductDayRecordService.getByDate(yesterdayStr);
            // 计算环比，返回对象
            return getCommonResponse(nowDayData, yesterdayData);
        }
        if (Constants.SEARCH_DATE_YESTERDAY.equals(dateLimit)) {
            // 获取昨天的数据
            DateTime yesterday = DateUtil.yesterday();
            String yesterdayStr = yesterday.toString("yyyy-MM-dd");
            ShoppingProductDayRecord yesterDayData = shoppingProductDayRecordService.getByDate(yesterdayStr);
            // 获取更前一天的数据
            String beforeDay = DateUtil.offsetDay(yesterday, -1).toString("yyyy-MM-dd");
            ShoppingProductDayRecord beforeDayDta = shoppingProductDayRecordService.getByDate(beforeDay);
            // 计算环比，返回对象
            return getCommonResponse(yesterDayData, beforeDayDta);
        }
        // 时间段数据查询(包含7天、30天、周、月、年)
        if (Constants.SEARCH_DATE_LATELY_7.equals(dateLimit)) {// 7天
            // 分别获取今天、前6天的数据，数据相加
            DateTime nowDate = DateUtil.date();
            // 获取今天的数据(所有数据现查)
            ShoppingProductDayRecord nowDayData = getShoppingProductNowDate();
            // 获取往前推七天的日期跟昨天的日期
            String beforeSevenDateStr = DateUtil.offsetDay(nowDate, -6).toString("yyyy-MM-dd");
            String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
            ShoppingProductDayRecord intervalDate = shoppingProductDayRecordService.getByTimeInterval(beforeSevenDateStr, yesterdayStr);
            intervalDate.setAddProductNum(intervalDate.getAddProductNum() + nowDayData.getAddProductNum());
            intervalDate.setPageView(intervalDate.getPageView() + nowDayData.getPageView());
            intervalDate.setCollectNum(intervalDate.getCollectNum() + nowDayData.getCollectNum());
            intervalDate.setAddCartNum(intervalDate.getAddCartNum() + nowDayData.getAddCartNum());
            intervalDate.setOrderProductNum(intervalDate.getOrderProductNum() + nowDayData.getOrderProductNum());
            intervalDate.setOrderSuccessProductNum(intervalDate.getOrderSuccessProductNum() + nowDayData.getOrderSuccessProductNum());

            // 获取前14天-前8天这个区间的数据
            String beforeFourteenDateStr = DateUtil.offsetDay(nowDate, -13).toString("yyyy-MM-dd");
            String beforeEightDateStr = DateUtil.offsetDay(nowDate, -7).toString("yyyy-MM-dd");
            ShoppingProductDayRecord intervalDate2 = shoppingProductDayRecordService.getByTimeInterval(beforeFourteenDateStr, beforeEightDateStr);
            // 计算环比，返回对象
            return getCommonResponse(intervalDate, intervalDate2);
        }
        if (Constants.SEARCH_DATE_LATELY_30.equals(dateLimit)) {// 30天
            DateTime nowDate = DateUtil.date();
            // 获取今天的数据(所有数据现查)
            ShoppingProductDayRecord nowDayData = getShoppingProductNowDate();
            // 获取往前推30天的日期跟昨天的日期
            String beforeSevenDateStr = DateUtil.offsetDay(nowDate, -29).toString("yyyy-MM-dd");
            String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
            ShoppingProductDayRecord intervalDate = shoppingProductDayRecordService.getByTimeInterval(beforeSevenDateStr, yesterdayStr);
            intervalDate.setAddProductNum(intervalDate.getAddProductNum() + nowDayData.getAddProductNum());
            intervalDate.setPageView(intervalDate.getPageView() + nowDayData.getPageView());
            intervalDate.setCollectNum(intervalDate.getCollectNum() + nowDayData.getCollectNum());
            intervalDate.setAddCartNum(intervalDate.getAddCartNum() + nowDayData.getAddCartNum());
            intervalDate.setOrderProductNum(intervalDate.getOrderProductNum() + nowDayData.getOrderProductNum());
            intervalDate.setOrderSuccessProductNum(intervalDate.getOrderSuccessProductNum() + nowDayData.getOrderSuccessProductNum());

            // 获取前14天-前8天这个区间的数据
            String beforeFourteenDateStr = DateUtil.offsetDay(nowDate, -59).toString("yyyy-MM-dd");
            String beforeEightDateStr = DateUtil.offsetDay(nowDate, -30).toString("yyyy-MM-dd");
            ShoppingProductDayRecord intervalDate2 = shoppingProductDayRecordService.getByTimeInterval(beforeFourteenDateStr, beforeEightDateStr);
            // 计算环比，返回对象
            return getCommonResponse(intervalDate, intervalDate2);
        }
        if (Constants.SEARCH_DATE_WEEK.equals(dateLimit)) {// 周
            // 判断今天是否是本周第一天
            int dayOfWeek = DateUtil.thisDayOfWeek();
            DateTime nowDate = DateUtil.date();
            ShoppingProductDayRecord intervalDate = new ShoppingProductDayRecord();
            if (dayOfWeek == 2) {// 周一
                // 获取今天的数据(所有数据现查)
                intervalDate = getShoppingProductNowDate();
            } else if (dayOfWeek == 3) {// 周二
                ShoppingProductDayRecord nowDayData = getShoppingProductNowDate();
                // 获取昨天的数据
                String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
                ShoppingProductDayRecord yesterDayData = shoppingProductDayRecordService.getByDate(yesterdayStr);
                intervalDate.setAddProductNum(yesterDayData.getAddProductNum() + nowDayData.getAddProductNum());
                intervalDate.setPageView(yesterDayData.getPageView() + nowDayData.getPageView());
                intervalDate.setCollectNum(yesterDayData.getCollectNum() + nowDayData.getCollectNum());
                intervalDate.setAddCartNum(yesterDayData.getAddCartNum() + nowDayData.getAddCartNum());
                intervalDate.setOrderProductNum(yesterDayData.getOrderProductNum() + nowDayData.getOrderProductNum());
                intervalDate.setOrderSuccessProductNum(yesterDayData.getOrderSuccessProductNum() + nowDayData.getOrderSuccessProductNum());
            } else {
                ShoppingProductDayRecord nowDayData = getShoppingProductNowDate();
                // 获取周一到现在的数据
                String weekStartDateStr = DateUtil.beginOfWeek(nowDate).toString("yyyy-MM-dd");
                String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
                intervalDate = shoppingProductDayRecordService.getByTimeInterval(weekStartDateStr, yesterdayStr);
                intervalDate.setAddProductNum(intervalDate.getAddProductNum() + nowDayData.getAddProductNum());
                intervalDate.setPageView(intervalDate.getPageView() + nowDayData.getPageView());
                intervalDate.setCollectNum(intervalDate.getCollectNum() + nowDayData.getCollectNum());
                intervalDate.setAddCartNum(intervalDate.getAddCartNum() + nowDayData.getAddCartNum());
                intervalDate.setOrderProductNum(intervalDate.getOrderProductNum() + nowDayData.getOrderProductNum());
                intervalDate.setOrderSuccessProductNum(intervalDate.getOrderSuccessProductNum() + nowDayData.getOrderSuccessProductNum());
            }

            // 获取上一周的数据
            DateTime offsetWeek = DateUtil.offsetWeek(nowDate, -1);
            String beforeWeekStartDateStr = DateUtil.beginOfWeek(offsetWeek).toString("yyyy-MM-dd");
            String beforeWeekEndDateStr = DateUtil.endOfWeek(offsetWeek).toString("yyyy-MM-dd");
            ShoppingProductDayRecord intervalDate2 = shoppingProductDayRecordService.getByTimeInterval(beforeWeekStartDateStr, beforeWeekEndDateStr);
            // 计算环比，返回对象
            return getCommonResponse(intervalDate, intervalDate2);
        }
        if (Constants.SEARCH_DATE_MONTH.equals(dateLimit)) {// 月
            // 判断今天是否是本月第一天
            int dayOfMonth = DateUtil.thisDayOfMonth();
            DateTime nowDate = DateUtil.date();
            ShoppingProductDayRecord intervalDate = new ShoppingProductDayRecord();
            if (dayOfMonth == 1) {// 一号
                // 获取今天的数据(所有数据现查)
                intervalDate = getShoppingProductNowDate();
            } else if (dayOfMonth == 2) {// 二号
                ShoppingProductDayRecord nowDayData = getShoppingProductNowDate();
                // 获取昨天的数据
                String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
                ShoppingProductDayRecord yesterDayData = shoppingProductDayRecordService.getByDate(yesterdayStr);
                intervalDate.setAddProductNum(yesterDayData.getAddProductNum() + nowDayData.getAddProductNum());
                intervalDate.setPageView(yesterDayData.getPageView() + nowDayData.getPageView());
                intervalDate.setCollectNum(yesterDayData.getCollectNum() + nowDayData.getCollectNum());
                intervalDate.setAddCartNum(yesterDayData.getAddCartNum() + nowDayData.getAddCartNum());
                intervalDate.setOrderProductNum(yesterDayData.getOrderProductNum() + nowDayData.getOrderProductNum());
                intervalDate.setOrderSuccessProductNum(yesterDayData.getOrderSuccessProductNum() + nowDayData.getOrderSuccessProductNum());
            } else {
                ShoppingProductDayRecord nowDayData = getShoppingProductNowDate();
                // 获取月初到现在的数据
                String monthStartDateStr = DateUtil.beginOfMonth(nowDate).toString("yyyy-MM-dd");
                String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
                intervalDate = shoppingProductDayRecordService.getByTimeInterval(monthStartDateStr, yesterdayStr);
                intervalDate.setAddProductNum(intervalDate.getAddProductNum() + nowDayData.getAddProductNum());
                intervalDate.setPageView(intervalDate.getPageView() + nowDayData.getPageView());
                intervalDate.setCollectNum(intervalDate.getCollectNum() + nowDayData.getCollectNum());
                intervalDate.setAddCartNum(intervalDate.getAddCartNum() + nowDayData.getAddCartNum());
                intervalDate.setOrderProductNum(intervalDate.getOrderProductNum() + nowDayData.getOrderProductNum());
                intervalDate.setOrderSuccessProductNum(intervalDate.getOrderSuccessProductNum() + nowDayData.getOrderSuccessProductNum());
            }

            // 获取上一月的数据
            DateTime offsetMonth = DateUtil.offsetMonth(nowDate, -1);
            String beforeMonthStartDateStr = DateUtil.beginOfMonth(offsetMonth).toString("yyyy-MM-dd");
            String beforeMonthEndDateStr = DateUtil.endOfMonth(offsetMonth).toString("yyyy-MM-dd");
            ShoppingProductDayRecord intervalDate2 = shoppingProductDayRecordService.getByTimeInterval(beforeMonthStartDateStr, beforeMonthEndDateStr);
            // 计算环比，返回对象
            return getCommonResponse(intervalDate, intervalDate2);
        }
        if (Constants.SEARCH_DATE_YEAR.equals(dateLimit)) {// 年
            // 判断今天是否是本年第一天
            DateTime nowDate = DateUtil.date();
            ShoppingProductDayRecord intervalDate = new ShoppingProductDayRecord();
            // 获取当年的开始日期
            String beginOfYear = DateUtil.beginOfYear(nowDate).toString("yyyy-MM-dd");
            String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
            if (beginOfYear.equals(nowDate.toString("yyyy-MM-dd"))) {// 第一天
                // 获取今天的数据(所有数据现查)
                intervalDate = getShoppingProductNowDate();
            } else if (beginOfYear.equals(yesterdayStr)) {// 第二天
                ShoppingProductDayRecord nowDayData = getShoppingProductNowDate();
                // 获取昨天的数据
                ShoppingProductDayRecord yesterDayData = shoppingProductDayRecordService.getByDate(yesterdayStr);
                intervalDate.setAddProductNum(yesterDayData.getAddProductNum() + nowDayData.getAddProductNum());
                intervalDate.setPageView(yesterDayData.getPageView() + nowDayData.getPageView());
                intervalDate.setCollectNum(yesterDayData.getCollectNum() + nowDayData.getCollectNum());
                intervalDate.setAddCartNum(yesterDayData.getAddCartNum() + nowDayData.getAddCartNum());
                intervalDate.setOrderProductNum(yesterDayData.getOrderProductNum() + nowDayData.getOrderProductNum());
                intervalDate.setOrderSuccessProductNum(yesterDayData.getOrderSuccessProductNum() + nowDayData.getOrderSuccessProductNum());
            } else {
                ShoppingProductDayRecord nowDayData = getShoppingProductNowDate();
                // 获取年初到现在的数据
                intervalDate = shoppingProductDayRecordService.getByTimeInterval(beginOfYear, yesterdayStr);
                intervalDate.setAddProductNum(intervalDate.getAddProductNum() + nowDayData.getAddProductNum());
                intervalDate.setPageView(intervalDate.getPageView() + nowDayData.getPageView());
                intervalDate.setCollectNum(intervalDate.getCollectNum() + nowDayData.getCollectNum());
                intervalDate.setAddCartNum(intervalDate.getAddCartNum() + nowDayData.getAddCartNum());
                intervalDate.setOrderProductNum(intervalDate.getOrderProductNum() + nowDayData.getOrderProductNum());
                intervalDate.setOrderSuccessProductNum(intervalDate.getOrderSuccessProductNum() + nowDayData.getOrderSuccessProductNum());
            }

            // 获取上一年的数据

            DateTime offsetYear = DateUtil.offsetDay(DateUtil.beginOfYear(nowDate), -1);
            String beforeYearStartDateStr = DateUtil.beginOfYear(offsetYear).toString("yyyy-MM-dd");
            String beforeYearEndDateStr = DateUtil.endOfYear(offsetYear).toString("yyyy-MM-dd");
            ShoppingProductDayRecord intervalDate2 = shoppingProductDayRecordService.getByTimeInterval(beforeYearStartDateStr, beforeYearEndDateStr);
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
                ShoppingProductDayRecord nowDayData = getShoppingProductNowDate();
                // 获取昨天的数据
                DateTime yesterday = DateUtil.yesterday();
                String yesterdayStr = yesterday.toString("yyyy-MM-dd");
                ShoppingProductDayRecord yesterdayData = shoppingProductDayRecordService.getByDate(yesterdayStr);
                // 计算环比，返回对象
                return getCommonResponse(nowDayData, yesterdayData);
            }
            // 不是今天
            ShoppingProductDayRecord dayData = shoppingProductDayRecordService.getByDate(dateStr);
            String beforeDateStr = DateUtil.offsetDay(startDate, -1).toString("yyyy-MM-dd");
            ShoppingProductDayRecord beforeDayData = shoppingProductDayRecordService.getByDate(beforeDateStr);
            // 计算环比，返回对象
            return getCommonResponse(dayData, beforeDayData);
        }
        // 判断开始时间是否大于结束时间
        if (startDate.compareTo(endDate) >= 0) {
            throw new CrmebException("请选择正确的时间范围");
        }
        // 获取两个日期间隔了多少天
        long between = DateUtil.between(startDate, endDate, DateUnit.DAY);
        ShoppingProductDayRecord intervalDate = new ShoppingProductDayRecord();
        // 判断结束时间是否是今天
        if (DateUtil.isSameDay(startDate, DateUtil.date())) {
            // 获取今天的数据(所有数据现查)
            ShoppingProductDayRecord nowDayData = getShoppingProductNowDate();
            String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
            intervalDate = shoppingProductDayRecordService.getByTimeInterval(startDate.toString("yyyy-MM-dd"), yesterdayStr);
            intervalDate.setAddProductNum(intervalDate.getAddProductNum() + nowDayData.getAddProductNum());
            intervalDate.setPageView(intervalDate.getPageView() + nowDayData.getPageView());
            intervalDate.setCollectNum(intervalDate.getCollectNum() + nowDayData.getCollectNum());
            intervalDate.setAddCartNum(intervalDate.getAddCartNum() + nowDayData.getAddCartNum());
            intervalDate.setOrderProductNum(intervalDate.getOrderProductNum() + nowDayData.getOrderProductNum());
            intervalDate.setOrderSuccessProductNum(intervalDate.getOrderSuccessProductNum() + nowDayData.getOrderSuccessProductNum());
        } else {
            intervalDate = shoppingProductDayRecordService.getByTimeInterval(startDate.toString("yyyy-MM-dd"), endDate.toString("yyyy-MM-dd"));
        }
        // 获取前一个区间的数据
        String beforeDateStr = DateUtil.offsetDay(startDate, -((int) between + 1)).toString("yyyy-MM-dd");
        String beforeEndDateStr = DateUtil.offsetDay(startDate, -1).toString("yyyy-MM-dd");
        ShoppingProductDayRecord intervalDate2 = shoppingProductDayRecordService.getByTimeInterval(beforeDateStr, beforeEndDateStr);
        // 计算环比，返回对象
        return getCommonResponse(intervalDate, intervalDate2);
    }

    /**
     * 获取商品排行榜
     * @param request 查询参数
     * @return CommonPage
     */
    @Override
    public CommonPage<ProductRankingResponse> getRanking(ProductRankingRequest request) {
        PageInfo<ProductDayRecord> recordPageInfo = productDayRecordService.getRanking(request);
        List<ProductRankingResponse> list = CollUtil.newArrayList();
        if (CollUtil.isEmpty(recordPageInfo.getList())) {
            return CommonPage.restPage(CommonPage.copyPageInfo(recordPageInfo, list));
        }
        int sort = 1;
        for (ProductDayRecord record : recordPageInfo.getList()) {
            StoreProduct product = storeProductService.getById(record.getProductId());
            ProductRankingResponse response = new ProductRankingResponse();
            BeanUtils.copyProperties(record, response);
            response.setSalesNum(record.getOrderProductNum());
            response.setSalesAmount(record.getOrderSuccessProductFee());
            response.setSort(sort);
            response.setProName(product.getStoreName());
            response.setImage(product.getImage());
            list.add(response);
            sort ++;
        }
        return CommonPage.restPage(CommonPage.copyPageInfo(recordPageInfo, list));
    }

    /**
     * 商品趋势数据
     * @param dateLimit 日期参数
     * @return List
     */
    @Override
    public List<ShoppingProductDayRecord> getTrendDataByDate(String dateLimit) {
        String startDateStr;
        String endDateStr;

        // 时间段数据查询(包含7天、30天、周、月、年)
        if (Constants.SEARCH_DATE_LATELY_7.equals(dateLimit)) {// 7天
            DateTime nowDate = DateUtil.date();
            startDateStr = DateUtil.offsetDay(nowDate, -7).toString("yyyy-MM-dd");
            endDateStr = DateUtil.offsetDay(nowDate, -1).toString("yyyy-MM-dd");
            return shoppingProductDayRecordService.findTrendDataOfBetween(startDateStr, endDateStr);
        }
        if (Constants.SEARCH_DATE_LATELY_30.equals(dateLimit)) {// 30天
            DateTime nowDate = DateUtil.date();
            startDateStr = DateUtil.offsetDay(nowDate, -30).toString("yyyy-MM-dd");
            endDateStr = DateUtil.offsetDay(nowDate, -1).toString("yyyy-MM-dd");
            return shoppingProductDayRecordService.findTrendDataOfBetween(startDateStr, endDateStr);
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

        return shoppingProductDayRecordService.findTrendDataOfBetween(startDateStr, endDateStr);
    }

    /**
     * 获取今日商品（商城维度）数据
     */
    private ShoppingProductDayRecord getShoppingProductNowDate() {
        String todayStr = DateUtil.date().toString("yyyy-MM-dd");
        // 商品新增量
        Integer newProductNum = storeProductService.getNewProductByDate(todayStr);

        // 浏览量
        int pageViewNum = 0;
        Object pageViewObject = redisUtil.get(RedisConstatns.PRO_PAGE_VIEW_KEY + todayStr);
        if (ObjectUtil.isNotNull(pageViewObject)) {
            pageViewNum = (Integer) pageViewObject;
        }

        // 收藏量
        Integer collectNum = productRelationService.getCountByDate(todayStr);

        // 加购量
        int addCartNum = 0;
        Object addCartObject = redisUtil.get(RedisConstatns.PRO_ADD_CART_KEY + todayStr);
        if (ObjectUtil.isNotNull(addCartObject)) {
            addCartNum = (Integer) addCartObject;
        }

        // 交易总件数
        Integer orderProductNum = storeOrderService.getOrderProductNumByDate(todayStr);

        // 交易成功件数
        Integer orderSuccessProductNum = storeOrderService.getOrderSuccessProductNumByDate(todayStr);

        ShoppingProductDayRecord shoppingProductDayRecord = new ShoppingProductDayRecord();
        shoppingProductDayRecord.setDate(todayStr);
        shoppingProductDayRecord.setAddProductNum(newProductNum);
        shoppingProductDayRecord.setPageView(pageViewNum);
        shoppingProductDayRecord.setCollectNum(collectNum);
        shoppingProductDayRecord.setAddCartNum(addCartNum);
        shoppingProductDayRecord.setOrderProductNum(orderProductNum);
        shoppingProductDayRecord.setOrderSuccessProductNum(orderSuccessProductNum);
        return shoppingProductDayRecord;
    }

    /**
     * 计算环比，返回响应对象
     * @param dataRecord 数据记录
     * @param beforeDataRecord 上一期数据记录
     * @return ShoppingProductDataResponse
     */
    private ShoppingProductDataResponse getCommonResponse(ShoppingProductDayRecord dataRecord, ShoppingProductDayRecord beforeDataRecord) {
        ShoppingProductDataResponse dataResponse = new ShoppingProductDataResponse();
        // 新增量
        dataResponse.setNewProductNum(dataRecord.getAddProductNum());
        dataResponse.setNewProductNumRatio(calculateRatio(dataRecord.getAddProductNum(), beforeDataRecord.getAddProductNum()));

        // 浏览量
        dataResponse.setPageView(dataRecord.getPageView());
        dataResponse.setPageViewRatio(calculateRatio(dataRecord.getPageView(), beforeDataRecord.getPageView()));

        // 收藏数
        dataResponse.setCollectNum(dataRecord.getCollectNum());
        dataResponse.setCollectNumRatio(calculateRatio(dataRecord.getCollectNum(), beforeDataRecord.getCollectNum()));

        // 加购数
        dataResponse.setAddCartNum(dataRecord.getAddCartNum());
        dataResponse.setAddCartNumRatio(calculateRatio(dataRecord.getAddCartNum(), beforeDataRecord.getAddCartNum()));

        // 交易总件数
        dataResponse.setOrderProductNum(dataRecord.getOrderProductNum());
        dataResponse.setOrderProductNumRatio(calculateRatio(dataRecord.getOrderProductNum(), beforeDataRecord.getOrderProductNum()));

        // 交易成功件数
        dataResponse.setOrderSuccessProductNum(dataRecord.getOrderSuccessProductNum());
        dataResponse.setOrderSuccessProductNumRatio(calculateRatio(dataRecord.getOrderSuccessProductNum(), beforeDataRecord.getOrderSuccessProductNum()));

        return dataResponse;
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
}
