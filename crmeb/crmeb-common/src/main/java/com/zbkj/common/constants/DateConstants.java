package com.zbkj.common.constants;

/**
 *  Config配置常量类
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public class DateConstants {

    /** 日期格式:年-月-日 时:分:秒 */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /** 日期格式:年-月-日 */
    public static final String DATE_FORMAT_DATE = "yyyy-MM-dd";
    /** 日期格式:年 */
    public static final String DATE_FORMAT_YEAR = "yyyy";
    /** 日期格式:月日- */
    public static final String DATE_FORMAT_MONTH_DATE = "MM-dd";
    /** 日期格式:年-月 */
    public static final String DATE_FORMAT_MONTH = "yyyy-MM";
    /** 日期格式:年月日时分秒 */
    public static final String DATE_TIME_FORMAT_NUM = "yyyyMMddHHmmss";
    /** 日期格式:年月日 */
    public static final String DATE_FORMAT_NUM = "yyyyMMdd";
    /** 日期格式:年月日 开始时间 */
    public static final String DATE_FORMAT_START = "yyyy-MM-dd 00:00:00";
    /** 日期格式:年月日 结束时间 */
    public static final String DATE_FORMAT_END = "yyyy-MM-dd 23:59:59";
    /** 日期格式:每个月的开始时间 */
    public static final String DATE_FORMAT_MONTH_START = "yyyy-MM-01 00:00:00";
    /** 日期格式:每年的开始时间 */
    public static final String DATE_FORMAT_YEAR_START = "yyyy-01-01 00:00:00";
    /** 日期格式:每年的结束时间 */
    public static final String DATE_FORMAT_YEAR_END = "yyyy-12-31 23:59:59";
    /** 日期格式:时:分:秒 */
    public static final String DATE_FORMAT_HHMMSS = "HH:mm:ss";
    /** 每天开始时间 */
    public static final String DATE_FORMAT_START_PEREND = "00:00:00";
    /** 每日结束时间 */
    public static final String DATE_FORMAT_END_PEREND = "23:59:59";
    /** 日期格式:年-月-日 时:分 */
    public static final String DATE_FORMAT_HHMM = "yyyy-MM-dd HH:mm";
    /** 日期格式:时分 */
    public static final String DATE_FORMAT_TIME_HHMM = "HHmm";
    /** 日期格式:rfc3339标准格式 2015-05-20T13:29:35.120+08:00 */
    public static final String DATE_FORMAT_RFC_3339 = "yyyy-MM-dd'T'HH:mm:ssXXX";

    /** 搜索日期-今天 */
    public static final String SEARCH_DATE_DAY = "today";
    /** 搜索日期-昨天 */
    public static final String SEARCH_DATE_YESTERDAY = "yesterday";
    /** 搜索日期-最近7天 */
    public static final String SEARCH_DATE_LATELY_7 = "lately7";
    /** 搜索日期-最近30天 */
    public static final String SEARCH_DATE_LATELY_30 = "lately30";
    /** 搜索日期-本周 */
    public static final String SEARCH_DATE_WEEK = "week";
    /** 搜索日期-上周 */
    public static final String SEARCH_DATE_PRE_WEEK = "preWeek";
    /** 搜索日期-本月 */
    public static final String SEARCH_DATE_MONTH = "month";
    /** 搜索日期-上月 */
    public static final String SEARCH_DATE_PRE_MONTH = "preMonth";
    /** 搜索日期-年 */
    public static final String SEARCH_DATE_YEAR = "year";
    /** 搜索日期-上一年 */
    public static final String SEARCH_DATE_PRE_YEAR = "preYear";
    /** 搜索日期-最近90天 */
    public static final String SEARCH_DATE_LATELY_90 = "lately90";
    /** 搜索日期-最近180天 */
    public static final String SEARCH_DATE_LATELY_180 = "lately180";

    // 时间类型开始时间
    public static String DATE_TIME_TYPE_BEGIN = "begin";
    // 时间类型结束时间
    public static String DATE_TIME_TYPE_END = "end";
}
