package com.zbkj.crmeb.statistics.service.impl;

import com.constants.Constants;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.zbkj.crmeb.log.service.StoreProductLogService;
import com.zbkj.crmeb.statistics.response.HomeRateResponse;
import com.zbkj.crmeb.statistics.service.HomeService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 用户表 服务实现类
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
@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductLogService storeProductLogService;

    /**
     * 销售额
     * @author Mr.Zhang
     * @since 2020-05-16
     * @return HashMap<String, Object>
     */
    @Override
    public HomeRateResponse sales() {
        //今日销售额
        BigDecimal today = storeOrderService.getSumBigDecimal(null, Constants.SEARCH_DATE_DAY);

        //昨天销售额
        BigDecimal yesterday = storeOrderService.getSumBigDecimal(null, Constants.SEARCH_DATE_YESTERDAY);

        //本周销售额
        BigDecimal week = storeOrderService.getSumBigDecimal(null, Constants.SEARCH_DATE_WEEK);

        //上周销售额
        BigDecimal preWeek = storeOrderService.getSumBigDecimal(null, Constants.SEARCH_DATE_PRE_WEEK);

        //总销售额
        BigDecimal all = storeOrderService.getSumBigDecimal(null, null);

        //同比公式 即：同比增长率=（本年的指标值-去年同期的值）/ 去年同期的值 * 100%


        //日同比
        int dayRate = CrmebUtil.getRate(today, yesterday);

        //周同比
        int weekRate = CrmebUtil.getRate(week, preWeek);


        return new HomeRateResponse(yesterday, dayRate, weekRate, all);
    }

    /**
     * 订单量
     * @author Mr.Zhang
     * @since 2020-05-16
     * @return HashMap<String, Object>
     */
    @Override
    public HomeRateResponse order() {
        ///今日数量
        int today = storeOrderService.getOrderCount(null, Constants.SEARCH_DATE_DAY);

        //昨日数量
        int yesterday = storeOrderService.getOrderCount(null, Constants.SEARCH_DATE_YESTERDAY);

        //本周数量
        int week = storeOrderService.getOrderCount(null, Constants.SEARCH_DATE_WEEK);

        //上周数量
        int preWeek = storeOrderService.getOrderCount(null, Constants.SEARCH_DATE_PRE_WEEK);

        //总数量
        int all = storeOrderService.getOrderCount(null, null);

        //同比公式 即：同比增长率=（本年的指标值-去年同期的值）/ 去年同期的值 * 100%


        //日同比
        int dayRate = CrmebUtil.getRate(today, yesterday);

        //周同比
        int weekRate = CrmebUtil.getRate(week, preWeek);


        return new HomeRateResponse(yesterday, dayRate, weekRate, all);
    }

    /**
     * 新增用户
     * @author Mr.Zhang
     * @since 2020-07-30
     * @return UserCreateResponse
     */
    @Override
    public HomeRateResponse user() {
        //今日数量
        Integer today = userService.getAddUserCountByDate(Constants.SEARCH_DATE_DAY);

        //昨日数量
        Integer yesterday = userService.getAddUserCountByDate(Constants.SEARCH_DATE_YESTERDAY);

        //本周数量
        Integer week = userService.getAddUserCountByDate(Constants.SEARCH_DATE_WEEK);

        //上周数量
        Integer preWeek = userService.getAddUserCountByDate(Constants.SEARCH_DATE_PRE_WEEK);

        //总数量
        Integer all = userService.getAddUserCountByDate(null);

        //同比公式 即：同比增长率=（本年的指标值-去年同期的值）/ 去年同期的值 * 100%


        //日同比
        int dayRate = CrmebUtil.getRate(today, yesterday);

        //周同比
        int weekRate = CrmebUtil.getRate(week, preWeek);


        return new HomeRateResponse(yesterday, dayRate, weekRate, all);
    }

    /**
     * 用户访问量
     * @author Mr.Zhang
     * @since 2020-05-16
     * @return HashMap<String, Object>
     */
    @Override
    public HomeRateResponse views() {
        //今日访问量
        Integer today = storeProductLogService.getCountByTimeAndType(Constants.SEARCH_DATE_DAY, "visit");

        //昨日访问量
        Integer yesterday = storeProductLogService.getCountByTimeAndType(Constants.SEARCH_DATE_YESTERDAY, "visit");

        //本周访问量
        Integer week = storeProductLogService.getCountByTimeAndType(Constants.SEARCH_DATE_WEEK, "visit");

        //上周访问量
        Integer preWeek = storeProductLogService.getCountByTimeAndType(Constants.SEARCH_DATE_PRE_WEEK, "visit");

        //日同比
        Integer dayRate = CrmebUtil.getRate(today, yesterday);

        //周同比
        Integer weekRate = CrmebUtil.getRate(week, preWeek);

        //总访问量
        Integer all = storeProductLogService.getCountByTimeAndType(Constants.SEARCH_DATE_MONTH, "visit");
        return new HomeRateResponse(yesterday, dayRate, weekRate, all);
    }

    /**
     * 增长率计算
     */
    private Double growth(Integer nowValuem, Integer lastValue) {
        if (nowValuem == 0 && lastValue == 0) return 0D;
        if (lastValue == 0) return nowValuem.doubleValue();
        if (nowValuem == 0) return -lastValue.doubleValue();
        double v = (nowValuem - lastValue) / lastValue.doubleValue();
        BigDecimal bigDecimal = new BigDecimal(v * 100).setScale(2, RoundingMode.UP);
        return bigDecimal.doubleValue();
    }

    /**
     * 用户曲线图
     * @author Mr.Zhang
     * @since 2020-05-16
     */
    @Override
    public Map<Object, Object> chartUser() {
        return dataFormat(userService.getAddUserCountGroupDate(Constants.SEARCH_DATE_LATELY_30), Constants.SEARCH_DATE_LATELY_30);
    }

    /**
     * 订单量趋势
     * @author Mr.Zhang
     * @since 2020-05-16
     */
    @Override
    public Map<String, Object> chartOrder() {
        Map<String, Object> map = new HashMap<>();

        List<StoreOrder> list = storeOrderService.getOrderGroupByDate(Constants.SEARCH_DATE_LATELY_30, Constants.NUM_TEN);

        map.put("quality",
                dataFormat(getOrderCountGroupByDate(list), Constants.SEARCH_DATE_LATELY_30)
        );
        map.put("price",
                dataFormat(getOrderPriceGroupByDate(list), Constants.SEARCH_DATE_LATELY_30)
        );
        return map;
    }

    /**
     * 按开始结束时间查询每日新增订单数量
     * @param list List<StoreOrder> 时间范围
     * @author Mr.Zhang
     * @since 2020-05-16
     * @return HashMap<String, Object>
     */
    private Map<Object, Object> getOrderCountGroupByDate(List<StoreOrder> list) {
        Map<Object, Object> map = new HashMap<>();

        if(list.size() < 1){
            return map;
        }

        for (StoreOrder storeOrder : list) {
            map.put(storeOrder.getOrderId(), storeOrder.getId());
        }

        return map;
    }

    /**
     * 按开始结束时间查询每日新增订单销售额
     * @param list List<StoreOrder> 时间范围
     * @author Mr.Zhang
     * @since 2020-05-16
     * @return HashMap<String, Object>
     */
    private Map<Object, Object> getOrderPriceGroupByDate(List<StoreOrder> list) {
        Map<Object, Object> map = new HashMap<>();

        if(list.size() < 1){
            return map;
        }

        for (StoreOrder storeOrder : list) {
            map.put(storeOrder.getOrderId(), storeOrder.getPayPrice());
        }

        return map;
    }

    /**
     * 日期和数量格式化
     * @author Mr.Zhang
     * @since 2020-05-16
     * @return Map<String, Integer>
     */
    private Map<Object, Object> dataFormat(Map<Object, Object> countGroupDate, String dateLimit){
        Map<Object, Object> map = new LinkedHashMap<>();
        List<String> listDate = DateUtil.getListDate(dateLimit);

        String[] weekList = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

        int i = 0;
        for (String date : listDate) {
            Object count = 0;
            if(countGroupDate.containsKey(date)){
                count = countGroupDate.get(date);
            }
            String key;

            //周格式化
            switch(dateLimit){
                //格式化周
                case Constants.SEARCH_DATE_WEEK:
                case Constants.SEARCH_DATE_PRE_WEEK:
                    key = weekList[i];
                    break;
                    //格式化月
                case Constants.SEARCH_DATE_PRE_MONTH:
                case Constants.SEARCH_DATE_MONTH:
                    key = i + 1 + "";
                    break;
                    //默认显示两位日期
                default:
                    key = date.substring(5, 10);
            }
            map.put(key, count);
            i++;
        }
        return map;
    }

    /**
     * 日期和数量格式化
     * @author Mr.Zhang
     * @since 2020-05-16
     * @return Map<String, Integer>
     */
    private Map<Object, Object> dataFormatYear(Map<Object, Object> countGroupDate, String dateLimit){
        Map<Object, Object> map = new LinkedHashMap<>();
        List<Object> listDate = new ArrayList<>();
        String year = "";
        if(dateLimit.equals(Constants.SEARCH_DATE_YEAR)) {
            year = DateUtil.nowYear();
        }

        if(dateLimit.equals(Constants.SEARCH_DATE_PRE_YEAR)) {
            year = DateUtil.lastYear();
        }

        //处理年
        //12个月份数据
        for (int i = 1; i <= 12; i++){
            String month = i + "";
            if(i < 10){
                month = "0" + i;
            }
            listDate.add(year + "-" + month);
        }

        String[] monthList = new String[]{"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月" , "十一月", "十二月" };

        int i = 0;
        for (Object date : listDate) {
            Object count = 0;
            if(countGroupDate.containsKey(date)){
                count = countGroupDate.get(date);
            }
            map.put(monthList[i], count);
            i++;
        }
        return map;
    }

    /**
     * 用户购买统计
     * @author Mr.Zhang
     * @since 2020-05-16
     */
    @Override
    public Map<String, Integer> chartUserBuy() {
        Map<String, Integer> map = new HashMap<>();
        //未消费用户
        map.put("zero", userService.getCountByPayCount(Constants.NUM_ZERO, Constants.NUM_ZERO));

        //消费一次用户
        map.put("one", userService.getCountByPayCount(Constants.NUM_ONE, Constants.NUM_ONE));

        //留存客户
        map.put("history", userService.getCountByPayCount(Constants.NUM_TWO, Constants.NUM_THREE));

        //回流客户
        map.put("back", userService.getCountByPayCount(Constants.NUM_ONE, Constants.EXPORT_MAX_LIMIT));

        return map;
    }

    /**
     * 周订单量趋势
     * @author Mr.Zhang
     * @since 2020-05-16
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> chartOrderInWeek() {
        return returnOrderDate(Constants.SEARCH_DATE_WEEK, Constants.SEARCH_DATE_PRE_WEEK, Constants.NUM_TEN);


    }

    /**
     * 月订单量趋势
     * @author Mr.Zhang
     * @since 2020-05-16
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> chartOrderInMonth() {
        return returnOrderDate(Constants.SEARCH_DATE_MONTH, Constants.SEARCH_DATE_PRE_MONTH, Constants.NUM_TEN);
    }

    /**
     * 年订单量趋势
     * @author Mr.Zhang
     * @since 2020-05-16
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> chartOrderInYear() {
        Map<String, Object> map = new HashMap<>();

        //查询本年订单量
        List<StoreOrder> list = storeOrderService.getOrderGroupByDate(Constants.SEARCH_DATE_YEAR, Constants.NUM_SEVEN);

        map.put("quality",
                dataFormatYear(getOrderCountGroupByDate(list), Constants.SEARCH_DATE_YEAR)
        );
        map.put("price",
                dataFormatYear(getOrderPriceGroupByDate(list), Constants.SEARCH_DATE_YEAR)
        );

        //查询上年订单量
        List<StoreOrder> preList = storeOrderService.getOrderGroupByDate(Constants.SEARCH_DATE_PRE_YEAR, Constants.NUM_SEVEN);

        map.put("preQuality",
                dataFormatYear(getOrderCountGroupByDate(preList), Constants.SEARCH_DATE_PRE_YEAR)
        );
        map.put("prePrice",
                dataFormatYear(getOrderPriceGroupByDate(preList), Constants.SEARCH_DATE_PRE_YEAR)
        );

        return map;
    }

    /**
     * 组装订单统计返回数据
     * @author Mr.Zhang
     * @since 2020-05-16
     * @return Map<String, Object>
     */
    private Map<String, Object> returnOrderDate(String dateLimit, String preDateLimit, int leftTime){
        Map<String, Object> map = new HashMap<>();

        //查询本周周订单量
        List<StoreOrder> list = storeOrderService.getOrderGroupByDate(dateLimit, leftTime);

        map.put("quality",
                dataFormat(getOrderCountGroupByDate(list), dateLimit)
        );
        map.put("price",
                dataFormat(getOrderPriceGroupByDate(list), dateLimit)
        );

        //查询上周周订单量
        List<StoreOrder> preList = storeOrderService.getOrderGroupByDate(preDateLimit, leftTime);

        map.put("preQuality",
                dataFormat(getOrderCountGroupByDate(preList), preDateLimit)
        );
        map.put("prePrice",
                dataFormat(getOrderPriceGroupByDate(preList), preDateLimit)
        );

        return map;
    }

}
