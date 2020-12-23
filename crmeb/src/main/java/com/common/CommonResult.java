package com.common;

import com.exception.ExceptionCodeEnum;
import com.exception.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 公共返回对象
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
public class CommonResult<T> {
    private long code;
    private String message;
    private T data;

    protected CommonResult() {
    }

    protected CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     */
    public static <T> CommonResult<T> success() {
        return new CommonResult<T>(ExceptionCodeEnum.SUCCESS.getCode(), ExceptionCodeEnum.SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回结果
     */
    public static <T> CommonResult<T> success(String message) {
        return new CommonResult<T>(ExceptionCodeEnum.SUCCESS.getCode(), message, null);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(ExceptionCodeEnum.SUCCESS.getCode(), ExceptionCodeEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     *
     * @param record 获取的数据
     */
    public static CommonResult<Map<String, Object>> success(MyRecord record) {
        return new CommonResult<>(ExceptionCodeEnum.SUCCESS.getCode(), ExceptionCodeEnum.SUCCESS.getMessage(), record.getColumns());
    }

    /**
     * 成功返回结果
     *
     * @param recordList 获取的数据
     */
    public static CommonResult<List<Map<String, Object>>> success(List<MyRecord> recordList) {
        List<Map<String, Object>> list = new ArrayList<>();
        recordList.forEach(i -> {
             list.add(i.getColumns());
        });
        return new CommonResult<>(ExceptionCodeEnum.SUCCESS.getCode(), ExceptionCodeEnum.SUCCESS.getMessage(), list);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param  message 提示信息
     */
    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<T>(ExceptionCodeEnum.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     */
    public static <T> CommonResult<T> failed(ExceptionHandler errorCode) {
        System.out.println("errorCode1:" + errorCode);
        return new CommonResult<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static <T> CommonResult<T> failed(ExceptionHandler errorCode,String message) {
        System.out.println("errorCode2:" + errorCode);
        return new CommonResult<T>(errorCode.getCode(), message, null);
    }

    /**
     * 失败返回结果
     * @param message 提示信息
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(ExceptionCodeEnum.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed() {
        return failed(ExceptionCodeEnum.FAILED);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> CommonResult<T> validateFailed() {
        return failed(ExceptionCodeEnum.VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     * @param message 提示信息
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<T>(ExceptionCodeEnum.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<T>(ExceptionCodeEnum.UNAUTHORIZED.getCode(), ExceptionCodeEnum.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 未登录返回结果
     */
    public static <T> CommonResult<T> unauthorized() {
        return new CommonResult<T>(ExceptionCodeEnum.UNAUTHORIZED.getCode(), ExceptionCodeEnum.UNAUTHORIZED.getMessage(), null);
    }

    /**
     * 没有权限查看
     */
    public static <T> CommonResult<T> forbidden() {
        return new CommonResult<T>(ExceptionCodeEnum.FORBIDDEN.getCode(), ExceptionCodeEnum.FORBIDDEN.getMessage(), null);
    }

    /**
     * 未授权返回结果
     */
    public static <T> CommonResult<T> forbidden(T data) {
        return new CommonResult<T>(ExceptionCodeEnum.FORBIDDEN.getCode(), ExceptionCodeEnum.FORBIDDEN.getMessage(), data);
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
