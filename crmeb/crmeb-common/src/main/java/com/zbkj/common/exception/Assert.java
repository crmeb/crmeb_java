package com.zbkj.common.exception;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @ClassName Assert
 * @Description 断言
 * @Author HZW
 * @Date 2023/2/22 12:37
 * @Version 1.0
 */
public interface Assert {

    /**
     * 创建异常
     */
    CrmebException newException();

    /**
     * 断言对象不为空
     */
    default <T> T assertNotNull(T obj) {
        if (ObjectUtil.isEmpty(obj)) {
            throw newException();
        }
        return obj;
    }

    /**
     * 断言对象是空
     */
    default void assertIsNull(Object obj) {
        if (ObjectUtil.isNotNull(obj)) {
            throw newException();
        }
    }

    /**
     * 断言两个对象不相等
     */
    default void assertNotEquals(Object obj, Object obj1) {
        if (ObjectUtil.equal(obj, obj1)) {
            throw newException();
        }
    }


    /**
     * 断言字符串不为空
     */
    default void assertNotEmptyStr(String str) {
        if (StrUtil.isBlank(str)) {
            throw newException();
        }
    }

    /**
     * 断言两个对象相等
     */
    default void assertEquals(Object obj, Object obj1) {
        if (ObjectUtil.notEqual(obj, obj1)) {
            throw newException();
        }
    }

    /**
     * 断言true
     */
    default void assertIsTrue(boolean expression) {
        if (!expression) {
            throw newException();
        }
    }

    /**
     * 断言false
     */
    default void assertIsFalse(boolean expression) {
        if (expression) {
            throw newException();
        }
    }

    /**
     * 断言int数组范围
     */
    default int assertBetween(int value, int min, int max) {
        if (value >= min && value <= max) {
            return value;
        }
        throw newException();
    }

    /**
     * 断言long数值范围
     */
    default long assertBetween(long value, long min, long max) {
        if (value >= min && value <= max) {
            return value;
        }
        throw newException();
    }

    /**
     * 断言double数值范围
     */
    default double assertBetween(double value, double min, double max) {
        if (value >= min && value <= max) {
            return value;
        }
        throw newException();
    }

    /**
     * 断言int大于数值范围
     */
    default int assertGreaterThan(int value , int min){
        if (value <= min){
            throw newException();
        }
        return value;
    }

}
