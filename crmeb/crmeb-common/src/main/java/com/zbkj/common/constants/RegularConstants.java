package com.zbkj.common.constants;

/**
 *  正则表达式类
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public class RegularConstants {
    //数字
    public static final String NUMBER = "^-?[0-9]+";

    //邮箱
    public static final String EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    //手机
    public static final String PHONE = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";

    //只校验手机为11位
    public static final String PHONE_TWO = "^1\\d{10}$";

    //整数或者小数：
    public static final String INTEGER_OR_FLOAT = "^[0-9]+=\\.{0,1}[0-9]{0,2}$";

    //n位的数字
    public static final String NUMBER_LEN = "^\\d{n}$";

    //n位的数字
    public static final String NUMBER_MIN_LEN = "^\\d{n,}$";

    //m~n位的数字
    public static final String NUMBER_LEN_RANGE = "^\\d{m,n}$";

    //零和非零开头的数字
    public static final String NUMBER_LEN_UNSIGNED_CONTAINS_ZERO = "^(0|[1-9][0-9]*)$";

    //有两位小数的正实数
    public static final String FLOAT_TWO_LEN = "^[0-9]+(.[0-9]{2})?$";

    //有两位小数的正实数
    public static final String FLOAT_LEN_RANGE = "^[0-9]+(.[0-9]{1,3})?$";

    //非零的正整数
    public static final String NUMBER_UNSIGNED = "^\\+?[1-9][0-9]*$";

    //非0的负数
    public static final String NUMBER_NOT_UNSIGNED = "^\\-[1-9][]0-9\"*$";

    //3位字符串
    public static final String STRING_THREE_LEN = "^.{3}$";

    //所有英文字母
    public static final String STRING_ALL_EN_CHARS = "^[A-Za-z]+$";

    //由26个大写英文字母组成的字符串
    public static final String STRING_UPPER_EN_CHARS = "^[A-Z]+$";

    //由26个小写英文字母组成的字符串
    public static final String STRING_LOWER_EN_CHARS = "^[a-z]+$";

    //由数字和26个英文字母组成的字符串
    public static final String STRING_ALL_EN_CHARS_AND_NUMBER = "^[A-Za-z0-9]+$";

    //由数字和26个英文字母组成的字符串
    public static final String STRING_ALL_EN_CHARS_AND_UNDER_LINE = "^\\w+";

    //验证用户密码， 正确格式为：以字母开头，长度在6~18之间，只能包含字符、数字和下划线。
    public static final String PASSWORD = "^[a-zA-Z]\\w{5,17}$";

    //验证码， 正确格式为：4位数字验证码。
    public static final String SMS_VALIDATE_CODE_NUM = "^\\d{6}$";

    //验证码， 正确格式为：6位数字验证码。
    public static final String VALIDATE_CODE_NUM_SIX = "^\\d{6}$";

    //汉字
    public static final String CHINESE = "^[\\u4e00-\\u9fa5]{0,}$";

    //url http开头
    public static final String URL = "^http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";

    //电话 正确格式为："XXX-XXXXXXX"、"XXXX-XXXXXXXX"、"XXX-XXXXXXX"、"XXX-XXXXXXXX"、"XXXXXXX"和"XXXXXXXX"
    public static final String TEL = "^(\\(\\d{3,4}-)|\\d{3.4}-)?\\d{7,8}$";

    //验证身份证号 （15位或18位数字）
    public static final String ID_CARD = "^\\d{15}|\\d{18}$";

    // 验证一年的12个月 正确格式为："01"～"09"和"1"～"12"
    public static final String MONTH = "^(0?[1-9]|1[0-2])$";

    // 验证一个月的31天 正确格式为；"01"～"09"和"1"～"31"
    public static final String DAY = "^((0?[1-9])|((1|2)[0-9])|30|31)$";

    // 匹配空行的正则表达式
    public static final String EMPTY_LINE = "\\n[\\s| ]*\\r";

    // 匹配html标签的正则表达式
    public static final String HTML_TAG = "<(.*)>(.*)<\\/(.*)>|<(.*)\\/>";

    // 匹配首尾空格的正则表达式
    public static final String TRIM = "(^\\s*)|(\\s*$)";


}
