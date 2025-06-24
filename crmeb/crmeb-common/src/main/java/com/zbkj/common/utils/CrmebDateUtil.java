package com.zbkj.common.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.vo.DateLimitUtilVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Date工具类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 **/
public final class CrmebDateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrmebDateUtil.class);

    private CrmebDateUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 获取当前日期,指定格式
     * 描述:<描述函数实现的功能>.
     *
     * @return
     */
    public static String nowDate() {
        return nowDate(DateConstants.DATE_FORMAT_NUM);
    }

    /**
     * 获取当前年,指定格式
     * 描述:<描述函数实现的功能>.
     *
     * @return
     */
    public static String nowYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR) + "";
    }

    /**
     * 获取上一年,指定格式
     * 描述:<描述函数实现的功能>.
     *
     * @return
     */
    public static String lastYear() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        return cal.get(Calendar.YEAR) + "";
    }

    /**
     * 获取下一年,指定格式
     * 描述:<描述函数实现的功能>.
     *
     * @return
     */
    public static String nextYear() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, +1);
        return cal.get(Calendar.YEAR) + "";
    }

    /**
     * 获取当前日期,指定格式
     * 描述:<描述函数实现的功能>.
     *
     * @return
     */
    public static Date nowDateTime() {
        return strToDate(nowDateTimeStr(), DateConstants.DATE_FORMAT);
    }

    /**
     * 获取当前日期,指定格式
     * 描述:<描述函数实现的功能>.
     *
     * @return
     */
    public static String nowDateTimeStr() {
        return nowDate(DateConstants.DATE_FORMAT);
    }

    /**
     * 获取当前日期,指定格式
     * 描述:<描述函数实现的功能>.
     *
     * @return
     */
    public static String nowDate(String DATE_FORMAT) {
        SimpleDateFormat dft = new SimpleDateFormat(DATE_FORMAT);
        return dft.format(new Date());
    }

    /**
     * 获取当前日期,指定格式
     * 描述:<描述函数实现的功能>.
     *
     * @return
     */
    public static String nowDateTime(String DATE_FORMAT) {
        SimpleDateFormat dft = new SimpleDateFormat(DATE_FORMAT);
        return dft.format(new Date());
    }

    /**
     * 获取当前日期,指定格式
     * 描述:<描述函数实现的功能>.
     *
     * @return
     */
    public static Integer getNowTime() {
        long t = (System.currentTimeMillis() / 1000L);
        return Integer.parseInt(String.valueOf(t));
    }

    /**
     * 获取当前时间戳（秒级）
     *
     * @return
     */
    public static Long getTime() {
        return (System.currentTimeMillis() / 1000L);
    }

    /**
     * 获取当前日期,指定格式
     * 描述:<描述函数实现的功能>.
     *
     * @return
     */
    public static Date nowDateTimeReturnDate(String DATE_FORMAT) {
        SimpleDateFormat dft = new SimpleDateFormat(DATE_FORMAT);
        return strToDate(dft.format(new Date()), DATE_FORMAT);
    }

    /**
     * convert a date to string in a specifies fromat.
     *
     * @param date
     * @param DATE_FORMAT
     * @return
     */
    public static String dateToStr(Date date, String DATE_FORMAT) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat myFormat = new SimpleDateFormat(DATE_FORMAT);
        return myFormat.format(date);
    }

    /**
     * parse a String to Date in a specifies fromat.
     *
     * @param dateStr
     * @param DATE_FORMAT
     * @return
     * @throws ParseException
     */
    public static Date strToDate(String dateStr, String DATE_FORMAT) {
        SimpleDateFormat myFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return myFormat.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }


    /**
     * date add Second
     *
     * @param date
     * @param num
     * @return
     */
    public static Date addSecond(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, num);
        return calendar.getTime();
    }

    /**
     * date add Second return String
     *
     * @param date
     * @param num
     * @return
     */
    public static String addSecond(Date date, int num, String DATE_FORMAT) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, num);
        return dateToStr(calendar.getTime(), DATE_FORMAT);
    }

    /**
     * 指定日期加上天数后的日期
     *
     * @param num     为增加的天数
     * @param newDate 创建时间
     * @return
     */
    public static final String addDay(String newDate, int num, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            Date currdate = format.parse(newDate);
            Calendar ca = Calendar.getInstance();
            ca.setTime(currdate);
            ca.add(Calendar.DATE, num);
            return format.format(ca.getTime());
        } catch (ParseException e) {
            LOGGER.error("转化时间出错,", e);
            return null;
        }
    }

    /**
     * 指定日期加上天数后的日期
     *
     * @param num     为增加的天数
     * @param newDate 创建时间
     * @return
     */
    public static final String addDay(Date newDate, int num, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Calendar ca = Calendar.getInstance();
        ca.setTime(newDate);
        ca.add(Calendar.DATE, num);
        return format.format(ca.getTime());
    }

    /**
     * convert long to date
     *
     * @param date 待转换时间戳
     * @return 转换后时间
     */
    public static Date timeStamp11ToDate(Integer date) {
        return new Date(date);
    }

    /**
     * convert long to date string
     *
     * @param date        待转换时间戳
     * @param DATE_FORMAT 格式化时间
     * @return 格式化后的时间
     */
    public static String timeStamp11ToDate(Integer date, String DATE_FORMAT) {
        return dateToStr(new Date(date), DATE_FORMAT);
    }

    /**
     * compare two date String with a pattern
     *
     * @param date1 开始时间
     * @param date2 结束时间
     * @param pattern 时间格式
     * @return 1=D1大 ，-1=D2大，0=相等
     */
    public static int compareDate(String date1, String date2, String pattern) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(pattern);
        try {
            Date dt1 = DATE_FORMAT.parse(date1);
            Date dt2 = DATE_FORMAT.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 检查日期格式是否合法
     *
     * @param date
     * @param style
     * @return
     */
    public static boolean checkDateFormat(String date, String style) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(style);
        try {
            // 设置lenient为false.
            // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            DATE_FORMAT.setLenient(false);
            DATE_FORMAT.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 计算两个时间之间的天数差
     *
     * @param beforeDay 开始时间
     * @param afterDay  结束时间
     * @return 相差天数
     */
    public static long getTwoDateDays(Date beforeDay, Date afterDay) {
        SimpleDateFormat sm = new SimpleDateFormat(DateConstants.DATE_FORMAT_NUM);
        long days = -1;
        try {
            days = (sm.parse(sm.format(afterDay)).getTime() - sm.parse(sm.format(beforeDay)).getTime()) / (1000 * 3600 * 24);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return days;
    }


    //获取时间戳11位
    public static int getSecondTimestamp(Date date) {
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime() / 1000);
        return Integer.parseInt(timestamp);
    }

    //获取时间戳11位
    public static int getSecondTimestamp(String date) {
        if (null == date) {
            return 0;
        }
        Date date1 = strToDate(date, DateConstants.DATE_FORMAT);
        if (date1 == null) {
            return 0;
        }
        String timestamp = String.valueOf(date1.getTime() / 1000);
        return Integer.parseInt(timestamp);
    }

    //获取时间戳10位
    public static int getSecondTimestamp(Long timeMillis) {
        if (null == timeMillis) {
            return 0;
        }
        String timestamp = String.valueOf(timeMillis / 1000);
        return Integer.parseInt(timestamp);
    }

    //获取时间戳11位
    public static int getSecondTimestamp() {
        Date date = strToDate(nowDateTime(DateConstants.DATE_FORMAT), DateConstants.DATE_FORMAT);
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime() / 1000);
        return Integer.parseInt(timestamp);
    }

    /**
     * 获得昨天日期:yyyy-MM-dd  HH:mm:ss
     */
    public static String getYesterdayStr() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        SimpleDateFormat startSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return startSdf.format(c.getTime());
    }

    /**
     * 获得本周第一天:yyyy-MM-dd HH:mm:ss
     */
    public static String getWeekStartDay() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.WEEK_OF_MONTH, 0);
        c.set(Calendar.DAY_OF_WEEK, 2);
        SimpleDateFormat startSdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return startSdf.format(c.getTime());
    }

    /**
     * 获得本周最后一天:yyyy-MM-dd HH:mm:ss
     */
    public static String getWeekEndDay() {
        return addDay(getWeekStartDay(), 7, DateConstants.DATE_FORMAT);
    }

    /**
     * 获得上周第一天:yyyy-MM-dd HH:mm:ss
     */
    public static String getLastWeekStartDay() {
        return addDay(getWeekStartDay(), -7, DateConstants.DATE_FORMAT);
    }

    /**
     * 获得上周最后一天:yyyy-MM-dd HH:mm:ss
     */
    public static String getLastWeekEndDay() {
        return addDay(getLastWeekStartDay(), 7, DateConstants.DATE_FORMAT);
    }

    /**
     * 获得本月最后一天:yyyy-MM-dd HH:mm:ss
     */
    public static String getMonthEndDay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat endSdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        return endSdf.format(c.getTime());
    }

    /**
     * 获得上月第一天:yyyy-MM-dd HH:mm:ss
     */
    public static String getLastMonthStartDay() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat startSdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
        return startSdf.format(c.getTime());
    }

    /**
     * 获得上月最后一天:yyyy-MM-dd HH:mm:ss
     */
    public static String getLastMonthEndDay() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat endSdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        return endSdf.format(c.getTime());
    }

    /**
     * 获得上年第一天:yyyy-MM-dd HH:mm:ss
     */
    public static String getLastYearStartDay() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        SimpleDateFormat startSdf = new SimpleDateFormat("yyyy-01-01 00:00:00");
        return startSdf.format(c.getTime());
    }

    /**
     * 获得上年最后一天:yyyy-MM-dd HH:mm:ss
     */
    public static String getLastYearEndDay() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        SimpleDateFormat endSdf = new SimpleDateFormat("yyyy-12-31 23:59:59");
        return endSdf.format(c.getTime());
    }

    /**
     * 计算时间范围
     *
     * @param data string 类型
     * @return dateLimitUtilVo
     * @since 2020-05-06
     */
    public static DateLimitUtilVo getDateLimit(String data) {
        //时间计算
        String startTime = null;
        String endTime = CrmebDateUtil.nowDateTime(DateConstants.DATE_FORMAT);
        String day = CrmebDateUtil.nowDateTime(DateConstants.DATE_FORMAT_START);
        String end = CrmebDateUtil.nowDateTime(DateConstants.DATE_FORMAT_END);

        if (StrUtil.isNotBlank(data)) {
            switch (data) {
                case DateConstants.SEARCH_DATE_DAY:
                    startTime = day;
                    break;
                case DateConstants.SEARCH_DATE_YESTERDAY:
                    startTime = CrmebDateUtil.addDay(day, -1, DateConstants.DATE_FORMAT_START);
                    endTime = CrmebDateUtil.addDay(end, -1, DateConstants.DATE_FORMAT_END);
                    break;
                case DateConstants.SEARCH_DATE_LATELY_7:
                    startTime = CrmebDateUtil.addDay(day, -6, DateConstants.DATE_FORMAT_START);
                    break;
                case DateConstants.SEARCH_DATE_WEEK:
                    startTime = getWeekStartDay();
                    endTime = getWeekEndDay();
                    break;
                case DateConstants.SEARCH_DATE_PRE_WEEK:
                    startTime = getLastWeekStartDay();
                    endTime = getLastWeekEndDay();
                    break;
                case DateConstants.SEARCH_DATE_LATELY_30:
                    startTime = CrmebDateUtil.addDay(day, -30, DateConstants.DATE_FORMAT_START);
                    break;
                case DateConstants.SEARCH_DATE_LATELY_90:
                    startTime = CrmebDateUtil.addDay(day, -90, DateConstants.DATE_FORMAT_START);
                    break;
                case DateConstants.SEARCH_DATE_LATELY_180:
                    startTime = CrmebDateUtil.addDay(day, -180, DateConstants.DATE_FORMAT_START);
                    break;
                case DateConstants.SEARCH_DATE_MONTH:
                    startTime = CrmebDateUtil.nowDateTime(DateConstants.DATE_FORMAT_MONTH_START);
                    endTime = getMonthEndDay();
                    break;
                case DateConstants.SEARCH_DATE_PRE_MONTH:
                    startTime = getLastMonthStartDay();
                    endTime = getLastMonthEndDay();
                    break;
                case DateConstants.SEARCH_DATE_YEAR:
                    startTime = CrmebDateUtil.nowDateTime(DateConstants.DATE_FORMAT_YEAR_START);
                    endTime = CrmebDateUtil.nowDateTime(DateConstants.DATE_FORMAT_YEAR_END);
                    break;
                case DateConstants.SEARCH_DATE_PRE_YEAR:
                    startTime = getLastYearStartDay();
                    endTime = getLastYearEndDay();
                    break;
                default:
                    List<String> list = CrmebUtil.stringToArrayStr(data);
                    if (list.size() == 1) {
                        throw new CrmebException("选择时间参数格式错误，请在 " +
                                DateConstants.SEARCH_DATE_DAY + "|" +
                                DateConstants.SEARCH_DATE_YESTERDAY + "|" +
                                DateConstants.SEARCH_DATE_LATELY_7 + "|" +
                                DateConstants.SEARCH_DATE_LATELY_30 + "|" +
                                DateConstants.SEARCH_DATE_MONTH + "|" +
                                DateConstants.SEARCH_DATE_YEAR + "|自定义时间范围（格式：yyyy-MM-dd HH:mm:ss，两个时间范围用逗号分割）");
                    }
                    startTime = list.get(0);
                    endTime = list.get(1);

//                    if (startTime.equals(endTime)) {
                    startTime = CrmebDateUtil.appointedDayStrToFormatStr(startTime, DateConstants.DATE_FORMAT_DATE, DateConstants.DATE_FORMAT_START);
                    endTime = CrmebDateUtil.appointedDayStrToFormatStr(endTime, DateConstants.DATE_FORMAT_DATE, DateConstants.DATE_FORMAT_END);
//                    }
                    break;
            }
        }
        return new DateLimitUtilVo(startTime, endTime);
    }

    /**
     * 计算月时间范围
     *
     * @param data string 时间祖父穿
     * @return dateLimitUtilVo
     */
    public static DateLimitUtilVo getMonthLimit(String data) {
        List<String> monthList = CrmebUtil.stringToArrayStr(data);
        if (monthList.size() != 2) {
            throw new CrmebException("选择时间参数格式错误范围（格式：yyyy-MM，两个时间范围用逗号分割）");
        }
        String startMonth = monthList.get(0);
        String endMonth = monthList.get(1);
        if (!isValidMonth(startMonth) || !isValidMonth(endMonth)) {
            throw new CrmebException("时间参数格式错误，格式yyyy-MM");
        }
        return new DateLimitUtilVo(startMonth, endMonth);
    }

    /**
     * 校验是否是年-月格式
     *
     * @param month 月份：年-月
     * @return boolean
     */
    public static boolean isValidMonth(String month) {
        SimpleDateFormat format = new SimpleDateFormat(DateConstants.DATE_FORMAT_MONTH);
        ParsePosition pos = new ParsePosition(0);
        format.setLenient(false);
        Date result = format.parse(month, pos);
        return !(pos.getIndex() == 0) && month.equals(format.format(result));
    }

    /**
     * 获取某一时间段内的时间集合
     *
     * @param data string 类型
     * @return List<Date>
     * @since 2020-05-06
     */
    public static List<String> getListDate(String data) {

        //获取30天的开始结束日期
        DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(data);

        //定义日期集合
        List<String> date = new ArrayList<>();

        //开始日期
        Date startDate = CrmebDateUtil.strToDate(dateLimit.getStartTime(), DateConstants.DATE_FORMAT);

        //结束日期
        Date endDate = CrmebDateUtil.strToDate(dateLimit.getEndTime(), DateConstants.DATE_FORMAT);

        while (endDate.after(startDate)) {
            date.add(CrmebDateUtil.dateToStr(startDate, DateConstants.DATE_FORMAT_DATE)); // 放入集合
            startDate = CrmebDateUtil.strToDate(CrmebDateUtil.addDay(startDate, 1, DateConstants.DATE_FORMAT), DateConstants.DATE_FORMAT); //循环一次 加一天
        }

        return date;
    }

    /**
     * 两个日期之前的相差天数
     *
     * @param starDate 开始日期
     * @param endDate  结束日期
     * @return 相差天数
     */
    public static int daysBetween(Date starDate, Date endDate) {

        Calendar cal = Calendar.getInstance();

        cal.setTime(starDate);

        long time1 = cal.getTimeInMillis();

        cal.setTime(endDate);

        long time2 = cal.getTimeInMillis();

        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));

    }

    /**
     * 获取指定日期指定格式字符串
     *
     * @param dateStr
     * @param DATE_FORMAT
     * @return
     * @throws ParseException
     */
    public static String appointedDayStrToFormatStr(String dateStr, String STR_DATE_FORMAT, String DATE_FORMAT) {
        Date date = CrmebDateUtil.strToDate(dateStr, STR_DATE_FORMAT);
        return CrmebDateUtil.dateToStr(date, DATE_FORMAT);
    }

    /**
     * 获取当前时间小时
     *
     * @return 当前时间小时 默认24小时
     */
    public static int getCurrentHour() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    /**
     * 13位时间戳转字符串
     *
     * @param timestamp
     * @param dateFormat
     * @return
     */
    public static String timestamp2DateStr(Long timestamp, String dateFormat) {
        if (ObjectUtil.isNull(timestamp)) {
            return "";
        }
        if (StrUtil.isBlank(dateFormat)) {
            dateFormat = DateConstants.DATE_FORMAT;
        }
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(date);
    }

    /**
     * 字符串转13位时间戳
     */
    public static Long dateStr2Timestamp(String dateStr, String type) {
        DateTime parse = DateUtil.parse(dateStr);
        if (StrUtil.isNotBlank(type)) {
            if (type.equals(DateConstants.DATE_TIME_TYPE_BEGIN)) {
                parse = DateUtil.beginOfDay(parse);
            }
            if (type.equals(DateConstants.DATE_TIME_TYPE_END)) {
                parse = DateUtil.endOfDay(parse);
            }
        }
        return parse.getTime();
    }

    public static void main(String[] args) {
        DateLimitUtilVo dateLimit = getDateLimit(DateConstants.SEARCH_DATE_LATELY_7);
        System.out.println(dateLimit.getStartTime());
        System.out.println(dateLimit.getEndTime());
    }

    /**
     * 获取某个月的每一天
     * @param yearParam 年
     * @param monthParam 月
     * @return 每一天列表
     */
    public static List<String> getAllDayByMonth(Integer yearParam, Integer monthParam) {
        List<String> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(yearParam, monthParam - 1, 1);
        int year = calendar.get(Calendar.YEAR);//年份
        int month = calendar.get(Calendar.MONTH) + 1;//月份
        int day = calendar.getActualMaximum(Calendar.DATE);
        for (int i = 1; i <= day; i++) {
            String date = null;
            if (month < 10 && i < 10) {
                date = year + "-0" + month + "-0" + i;
            }
            if (month < 10 && i >= 10) {
                date = year + "-0" + month + "-" + i;
            }
            if (month >= 10 && i < 10) {
                date = year + "-" + month + "-0" + i;
            }
            if (month >= 10 && i >= 10) {
                date = year + "-" + month + "-" + i;
            }

            list.add(date);
        }
        return list;
    }
}
