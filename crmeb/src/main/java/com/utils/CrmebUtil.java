package com.utils;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.constants.Constants;
import com.exception.CrmebException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Security;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Crmeb工具类
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
public class CrmebUtil {

    public static String encryptPassword(String pwd, String key){
        try {
            Security.addProvider(new com.sun.crypto.provider.SunJCE());
            Key _key = getDESSercretKey(key);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, _key);
            byte[] data = pwd.getBytes(StandardCharsets.UTF_8);
            byte[] result = cipher.doFinal(data);
            return new sun.misc.BASE64Encoder().encode(result);
        }catch (Exception e){
            throw new CrmebException("密码处理异常");
        }
    }

    /**
     * 解密密码
     */
    public static String decryptPassowrd(String pwd, String key)
            throws Exception {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        Key aKey = getDESSercretKey(key);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, aKey);

        byte[] data = new sun.misc.BASE64Decoder().decodeBuffer(pwd);
        byte[] result = cipher.doFinal(data);

        return new String(result, StandardCharsets.UTF_8);
    }

    /**
     * 获得DES加密秘钥
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     */
    public static SecretKey getDESSercretKey(String key) throws UnsupportedEncodingException {
        byte[] result = new byte[8];
        byte[] keys = null;
        keys = key.getBytes(StandardCharsets.UTF_8);
        for(int i = 0; i<8;i++){
            if(i < keys.length){
                result[i] = keys[i];
            }else{
                result[i] = 0x01;
            }
        }
        return new SecretKeySpec(result, "DES");
    }

    /**
     * 对象转map
     * @param object 对象
     * @author Mr.Zhang
     * @since 2020-04-14
     * @return Map
     */
    public static Map objectToMap(Object object){
        return JSONObject.parseObject(JSONObject.toJSONString(object), Map.class);
    }

    public static Map StringToMap(String strValue){
        return JSONObject.parseObject(strValue, HashMap.class);
    }

    /**
     * map转对象
     * @param map map
     * @param clz 对象
     * @author Mr.Zhang
     * @since 2020-04-14
     * @return Map
     */
    public static <T> T mapToObj(HashMap<String,Object> map, Class<T> clz){
        if (map == null) return null;
        return JSONObject.parseObject(JSONObject.toJSONString(map), clz);
    }

    /**
     * map转对象
     * @param map map
     * @param clz 对象
     * @author Mr.Zhang
     * @since 2020-04-14
     * @return Map
     */
    public static <T> T mapStringToObj(HashMap<String,String> map, Class<T> clz){
        if (map == null) return null;
        return JSONObject.parseObject(JSONObject.toJSONString(map), clz);
    }


    /**
     * 密码工具
     * @param args String[] 字符串数组
     */
    public static void main(String[] args) throws Exception {
//        System.out.println(encryptPassword("123456", "admin"));
		System.out.println(decryptPassowrd("", ""));
    }

    /**
     * map合并
     * @param map 对象
     * @author Mr.Zhang
     * @since 2020-04-14
     * @return Object
     */
    public static Map<String, Object> mergeMap(Map<String, Object> map, Map<String, Object> map1){
        HashMap<String, Object> map2 = new HashMap<>();
        map2.putAll(map);
        map2.putAll(map1);
        return map2;
    }

    /**
     * 字符串分割，转化为数组
     * @param str 字符串
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return List<Integer>
     */
    public static List<Integer> stringToArray(String str){
        return stringToArrayByRegex(str, ",");
    }

    /**
     * 字符串分割，转化为数组
     * @param str 字符串
     * @param regex 分隔符有
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return List<Integer>
     */
    public static List<Integer> stringToArrayByRegex(String str, String regex ){
        List<Integer> list = new ArrayList<>();
        if (str.contains(regex)){

            String[] split = str.split(regex);

            for (String value : split) {
                if(!StringUtils.isBlank(value)){
                    list.add(Integer.parseInt(value.trim()));
                }
            }
        }else {
            list.add(Integer.parseInt(str));
        }
        return list;
    }


    /**
     * 字符串分割，转化为数组
     * @param str 字符串
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return List<String>
     */
    public static List<String> stringToArrayStr(String str){
        return stringToArrayStrRegex(str, ",");
    }

    /**
     * 数字字符数据转int格式数据
     * @param str 待转换的数字字符串
     * @return int数组
     */
    public static List<Integer> stringToArrayInt(String str){
        List<String> strings = stringToArrayStrRegex(str, ",");
        List<Integer> ids = new ArrayList<>();
        for (String string : strings) {
            ids.add(Integer.parseInt(string.trim()));
        }
        return ids;
    }

    /**
     * 字符串分割，转化为数组
     * @param str 字符串
     * @param regex 分隔符有
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return List<String>
     */
    public static List<String> stringToArrayStrRegex(String str, String regex ){
        List<String> list = new ArrayList<>();
        if (str.contains(regex)){

            String[] split = str.split(regex);

            for (String value : split) {
                if(!StringUtils.isBlank(value)){
                    list.add(value);
                }
            }
        }else {
            list.add(str);
        }
        return list;
    }

    /**
     * 字符串分割，转化为数组
     * @param str 字符串
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return List<Object>
     */
    public static List<Object> stringToArrayObject(String str){
        return stringToArrayObjectRegex(str, ",");
    }

    /**
     * 字符串分割，转化为数组
     * @param str 字符串
     * @param regex 分隔符有
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return List<Object>
     */
    public static List<Object> stringToArrayObjectRegex(String str, String regex ){
        List<Object> list = new ArrayList<>();
        if (str.contains(regex)){

            String[] split = str.split(regex);

            for (String value : split) {
                if(!StringUtils.isBlank(value)){
                    list.add(value);
                }
            }
        }else {
            list.add(str);
        }
        return list;
    }

    /**
     * json字符串转数组
     * @param str 字符串
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return List<String>
     */
    public static List<String> jsonToListString(String str){
        try{
            return JSONObject.parseArray(str).toJavaList(String.class);
        }catch (Exception e){
            ArrayList<String> list = new ArrayList<>();
            list.add(str);
            return list;
        }
    }

    /**
     * json字符串转数组
     * @param str 字符串
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return List<Integer>
     */
    public static List<Integer> jsonToListInteger(String str){
        try{
            return JSONObject.parseArray(str).toJavaList(Integer.class);
        }catch (Exception e){
            ArrayList<Integer> list = new ArrayList<>();
            list.add(Integer.parseInt(str));
            return list;
        }
    }

    /**
     * json字符串转数组
     * @param str 字符串
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return List<Integer>
     */
    public static List<Object> jsonToListObject(String str){
        try{
            return JSONObject.parseArray(str).toJavaList(Object.class);
        }catch (Exception e){
            ArrayList<Object> list = new ArrayList<>();
            list.add(str);
            return list;
        }
    }

    /**
     * json字符串转数组
     * @param str 字符串
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return List<T>
     */
    public static <T> List<T> jsonToListClass(String str, Class<T> cls){
        try{
            return JSONObject.parseArray(str, cls);
        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    /**
     * 去掉域名 https / http / www 信息
     * @param domain 域名
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return String
     */
    public static String getCleanLowerDomain(String domain){
        domain = domain.toLowerCase();
        domain = domain.replaceAll("http://", "");
        domain = domain.replaceAll("https://", "");
        domain = domain.replaceAll("www.", "");
        domain = domain.replaceAll("/", "");
        return domain;
    }

    /**
     * 获取根域名
     * @param domain 域名
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return String
     */
    public static String getBaseLowerDomain(String domain){
        if(!domain.contains(".")){
            domain  += ".com";
        }
        domain = getCleanLowerDomain(domain);
        String[] split = domain.split("\\.");
        int len = split.length;
        if(len == 0){
            return domain;
        }
        return split[len - 2] + "." + split[len - 1];
    }

    /**
     * 获取客户端ip
     * @param request 参数
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return String
     */
    public static String getClientIp(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        if(checkIsIp(ip)){
            return ip;
        }

        ip = request.getHeader("X-Real-IP");
        if(checkIsIp(ip)){
            return ip;
        }

        ip = request.getRemoteAddr();
        if(ip.equals("0:0:0:0:0:0:0:1")){
            //本地 localhost访问 ipv6
            ip = "127.0.0.1";
        }
        if(checkIsIp(ip)){
            return ip;
        }

        return "";
    }

    /**
     * 检测是否为ip
     * @param ip 参数
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return String
     */
    public static boolean checkIsIp(String ip){
        if(StringUtils.isBlank(ip)){
            return false;
        }

        if(ip.equals("unKnown")){
            return false;
        }

        if(ip.equals("unknown")){
            return false;
        }

        return ip.split("\\.").length == 4;
    }

    /**
     * 获取fin_in_set拼装sql
     * @param field String 字段
     * @param value Integer 值
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return String
     */
    public static String getFindInSetSql(String field, Integer value ){
        return "find_in_set(" + value +", " + field + ")";
    }

    /**
     * 获取fin_in_set拼装sql
     * @param field String 字段
     * @param list ArrayList<Integer> 值
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return String
     */
    public static String getFindInSetSql(String field, ArrayList<Integer> list ){
        ArrayList<String> sqlList = new ArrayList<>();
        for (Integer value: list) {
            sqlList.add(getFindInSetSql(field, value));
        }
        return "( " + StringUtils.join(sqlList, " or ") + ")";
    }

    /**
     * 获取fin_in_set拼装sql
     * @param field String 字段
     * @param idStr String 多个值，逗号分割
     * @author Mr.Zhang
     * @since 2020-04-22
     * @return String
     */
    public static String getFindInSetSql(String field, String idStr ){
        List<Integer> list = stringToArray(idStr);
        ArrayList<String> sqlList = new ArrayList<>();
        for (Integer value: list) {
            sqlList.add(getFindInSetSql(field, value));
        }
        return "( " + StringUtils.join(sqlList, " or ") + ")";
    }

    /**
     * 通过id获取分类名称
     * @param list 分类集合
     * @param categoryIdStr id字符串
     * @author Mr.Zhang
     * @since 2020-05-06
     * @
     */
    public static String getValueByIndex(HashMap<Integer, String> list, String categoryIdStr){
        if(list.size() < 1 || StringUtils.isBlank(categoryIdStr)){
            return "";
        }
        ArrayList<String> name = new ArrayList<>();
        List<Integer> idList = CrmebUtil.stringToArray(categoryIdStr);

        String str = "";
        for (Integer id : idList) {
             str = getStrValueByIndex(list, id);
            if(!StringUtils.isBlank(str)){
                name.add(getStrValueByIndex(list, id));
            }
        }
        if(name.size() < 1){
            return "";
        }
        return StringUtils.join(name, ",");
    }

    /**
     * 通过id获取分类名称
     * @param list 分类集合
     * @param key id
     * @author Mr.Zhang
     * @since 2020-05-06
     * @
     */
    public static String getStrValueByIndex(HashMap<Integer, String> list, Integer key){
        if(list.size() < 1){
            return "";
        }
        return list.getOrDefault(key, "");
    }

    /**
     * 通过id获取分类名称
     * @param list 分类集合
     * @param key id
     * @author Mr.Zhang
     * @since 2020-05-06
     * @
     */
    public static Integer getIntValueByIndex(HashMap<Integer, Integer> list, Integer key){
        if(null == list ||list.size() < 1){
            return 0;
        }
        return list.getOrDefault(key, 0);
    }

    /**
     * 获取uuid
     * @author Mr.Zhang
     * @since 2020-05-06
     * @
     */
    public static String getUuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 同比率计算 //同比增长率= ((当前周期 - 上一个周期) ÷ 上一个周期 ) *100%
     * @param i1 当前周期
     * @param i2 上一个周期
     * @author Mr.Zhang
     * @since 2020-05-06
     * @
     */
    public static int getRate(Integer i1, Integer i2){
        BigDecimal b1 = new BigDecimal(i1);
        BigDecimal b2 = new BigDecimal(i2);
        return getRate(b1, b2);
    }

    /**
     * 同比率计算 //同比增长率= ((当前周期 - 上一个周期) ÷ 上一个周期 ) *100%
     * @param b1 当前周期
     * @param b2 上一个周期
     * @author Mr.Zhang
     * @since 2020-05-06
     * @
     */
    public static int getRate(BigDecimal b1, BigDecimal b2){
        //计算差值

        if(b2.equals(b1)){
            //数值一样，说明没有增长
            return Constants.NUM_ZERO;
        }

        if(b2.equals(BigDecimal.ZERO)){
            //b2是0
            return Constants.NUM_ONE_HUNDRED;
        }

        return (b1.subtract(b2)).divide(b2, 2, BigDecimal.ROUND_UP).multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue();


//        BigDecimal.setScale();//用于格式化小数点
//        setScale(1);//表示保留以为小数，默认用四舍五入方式
//        setScale(1,BigDecimal.ROUND_DOWN);//直接删除多余的小数位，如2.35会变成2.3
//        setScale(1,BigDecimal.ROUND_UP);//进位处理，2.35变成2.4
//        setScale(1,BigDecimal.ROUND_HALF_UP);//四舍五入，2.35变成2.4
//        setScaler(1,BigDecimal.ROUND_HALF_DOWN);//四舍五入，2.35变成2.3，如果是5则向下舍
    }

    /**
     * 同比率计算 //同比增长率= ((当前周期 - 上一个周期) ÷ 上一个周期 ) *100%
     * @param i1 当前周期
     * @param i2 上一个周期
     * @author Mr.Zhang
     * @since 2020-05-06
     * @
     */
    public static BigDecimal getRateBig(Integer i1, Integer i2){
        BigDecimal b1 = new BigDecimal(i1);
        BigDecimal b2 = new BigDecimal(i2);
        return getRateBig(b1, b2);
    }

    /**
     * 同比率计算 //同比增长率= ((当前周期 - 上一个周期) ÷ 上一个周期 ) *100%
     * 如果上个周期为0， = 当前周期 * 100%
     * @param b1 当前周期
     * @param b2 上一个周期
     * @author Mr.Zhang
     * @since 2020-05-06
     * @
     */
    public static BigDecimal getRateBig(BigDecimal b1, BigDecimal b2){
        //计算差值

        if(b2.compareTo(b1) == 0){
            //数值一样，说明没有增长
            return BigDecimal.ZERO;
        }

        if(b2.compareTo(BigDecimal.ZERO) == 0){
            //b2是0
            return b1.setScale(2, BigDecimal.ROUND_UP);
        }

        return (b1.subtract(b2)).multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).divide(b2, BigDecimal.ROUND_UP);
    }

    /**
     * hash 转换
     * @param hash final byte[] hash参数
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return String
     */
    public static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /**
     * hash 转换
     * @param base64 String 图片流
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return String
     */
    public static String getBase64Image(String base64) {
        return "data:image/png;base64," + base64;
    }

    /**
     * 去掉字符串第一位和最后一位
     * @param param String 参数
     * @return String
     */
    public static String trimSingeQua(String param){
        return param.substring(1,param.length()-1);
    }


    /**
     * 根据长度生成随机数字
     * @param start 起始数字
     * @param end 结束数字
     * @return 生成的随机码
     */
    public static Integer randomCount(Integer start, Integer end){
        return (int)(Math.random()*(end - start +1) + start);
    }

    /**
     * 订单号生成
     * @param payType String 支付类型
     * @return 生成的随机码
     */
    public static String getOrderNo(String payType){
        return payType + System.currentTimeMillis() + randomCount(11111, 99999);
    }

    /**
     * 订单号生成
     * @param payType String 支付类型
     * @return 生成的随机码
     */
    public static String getOrderNoPrefix(String payType){
        switch (payType){
            case Constants.PAY_TYPE_WE_CHAT:
                return Constants.ORDER_NO_PREFIX_WE_CHAT;
                //其他平台
            default:
                return Constants.ORDER_NO_PREFIX_H5;
        }
    }

    /**
     * map排序
     * @param map Map<String, Object> 支付类型
     * @return 生成的随机码
     */
    public static Map<String, Object> mapSort(Map<String, Object> map) {
        return new TreeMap<>(map);
    }

    /**
     * map排序然后按照url模式拼接
     * @param map Map<String, Object> 支付类型
     * @return 生成的随机码
     */
    public static String mapToStringUrl(Map<String, Object> map){
        map = CrmebUtil.mapSort(map);
        StringBuilder sb = new StringBuilder();       // 多线程访问的情况下需要用StringBuffer
        Set es = map.keySet();                 // 所有参与传参的key按照accsii排序（升序）
        for (Object set : es) {
            String k = set.toString();
            Object v = map.get(k);
            sb.append(k).append("=").append(v.toString()).append("&");
        }
        String str = sb.toString();
        return str.substring(0, str.length() - 1);
    }

    public static BigDecimal getBigDecimalRate(String rate) {
        return new BigDecimal(rate).divide(BigDecimal.TEN.multiply(BigDecimal.TEN));
    }

    /**
     * unicode编码转换为汉字
     * @param unicodeStr 待转化的编码
     * @return 返回转化后的汉子
     */
    public static String UnicodeToCN(String unicodeStr) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(unicodeStr);
        char ch;
        while (matcher.find()) {
            //group
            String group = matcher.group(2);
            //ch:'李四'
            ch = (char) Integer.parseInt(group, 16);
            //group1
            String group1 = matcher.group(1);
            unicodeStr = unicodeStr.replace(group1, ch + "");
        }

        return unicodeStr.replace("\\", "").trim();
    }

    /**
     * 汉字转化为Unicode编码
     * @param CN 待转化的中文
     * @return 返回转化之后的unicode编码
     */
    public static String CNToUnicode(String CN) {

        try {
            StringBuffer out = new StringBuffer("");
            //直接获取字符串的unicode二进制
            byte[] bytes = CN.getBytes("unicode");
            //然后将其byte转换成对应的16进制表示即可
            for (int i = 0; i < bytes.length - 1; i += 2) {
                out.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for (int j = str.length(); j < 2; j++) {
                    out.append("0");
                }
                String str1 = Integer.toHexString(bytes[i] & 0xff);
                out.append(str1);
                out.append(str);
            }
            return out.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getSign(Map<String, Object> map, String signKey){
        String result = CrmebUtil.mapToStringUrl(map) + "&key=" + signKey;
//        return DigestUtils.md5Hex(result).toUpperCase();
        String sign = SecureUtil.md5(result).toUpperCase();
        System.out.println("sign ========== " + sign);
        return sign;
    }

    /**
     * 检查是否可以转换int
     * @param str
     * @return
     */
    public static boolean isString2Num(String str){
        Pattern pattern = Pattern.compile("^[0-9]*$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    //数组去重
    public static List<Integer> arrayUnique(Integer[] arr){
        List<Integer> list = new ArrayList<>();
        for (Integer integer : arr) {
            if (!list.contains(integer)) {
                list.add(integer);
            }
        }

        return list;
    }


    /**
     * 百分比计算
     * @param detailTotalNumber  销售量
     * @param totalNumber  限量库存
     * @return  百分比
     */
    public static String percentInstance(Integer detailTotalNumber, Integer totalNumber) {
        Double bfTotalNumber = Double.valueOf(detailTotalNumber);
        Double zcTotalNumber = Double.valueOf(totalNumber);
        double percent = bfTotalNumber/zcTotalNumber;
        //获取格式化对象
        NumberFormat nt = NumberFormat.getPercentInstance();
        //设置百分数精确度2即保留两位小数
        nt.setMinimumFractionDigits(2);
        return nt.format(percent);
    }

    /**
     * 百分比计算
     * @param detailTotalNumber  销售量
     * @param totalNumber  限量库存
     * @return  百分比
     */
    public static int percentInstanceIntVal(Integer detailTotalNumber, Integer totalNumber) {
        BigDecimal sales = new BigDecimal(detailTotalNumber);
        BigDecimal total = new BigDecimal(totalNumber);
        int percentage = sales.divide(total, 2, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).intValue();
        return Math.min(percentage, 100);
    }

    /**
     * 百分比计算
     * @param detailTotalNumber  销售量
     * @param totalNumber  限量库存
     * @return  百分比
     */
    public static int percentInstanceIntVal(BigDecimal detailTotalNumber, BigDecimal totalNumber) {
        int percentage = detailTotalNumber.divide(totalNumber, 2, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).intValue();
        return Math.min(percentage, 100);
    }

    /**
     * Object转List
     */
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if(obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }
}
