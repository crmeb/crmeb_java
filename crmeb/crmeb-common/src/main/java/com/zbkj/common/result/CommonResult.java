package com.zbkj.common.result;

import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.vo.MyRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 公共返回对象
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
public class CommonResult<T> implements Serializable {

    private static final long serialVersionUID = -6630747483482976634L;

    /**
     * 响应码
     */
    private Integer code;
    /**
     * 响应消息
     */
    private String message;
    /**
     * 响应体
     */
    private T data;

    // ===========构造器开始，构造器私有，外部不可直接创建=========================================

    private CommonResult() {
        this.code = 200;
    }

    private CommonResult(T data) {
        this.code = 200;
        this.data = data;
    }

    private CommonResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private CommonResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private CommonResult(IResultEnum iResultEnum) {
        this.code = iResultEnum.getCode();
        this.message = iResultEnum.getMessage();
    }

    // ===========构造器结束，构造器私有，外部不可直接创建=========================================

    /**
     * 成功返回,没有结果
     */
    public static <T> CommonResult<T> success() {
        return new CommonResult<>();
    }

    /**
     * 成功返回,有返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(data);
    }

    /**
     * 成功返回结果
     *
     * @param record 获取的数据
     */
    public static CommonResult<Map<String, Object>> success(MyRecord record) {
        return new CommonResult<>(record.getColumns());
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
        return new CommonResult<>(list);
    }

    /**
     * 通用返回失败
     *
     * @param iResultEnum 结果枚举
     * @return T
     */
    public static <T> CommonResult<T> failed(IResultEnum iResultEnum) {
        return new CommonResult<>(iResultEnum);
    }

    /**
     * 失败返回结果
     *
     * @param resultCode 结果枚举
     * @param message    错误信息
     */
    public static <T> CommonResult<T> failed(CommonResultCode resultCode, String message) {
        return new CommonResult<>(resultCode.getCode(), message);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed(CrmebException e) {
        return new CommonResult<>(e.getCode(), e.getMessage());
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed() {
        return failed(CommonResultCode.ERROR);
    }

    /**
     * 失败返回结果
     *
     * @param message    错误信息
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<>(CommonResultCode.ERROR.getCode(), message);
    }

    public long getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public CommonResult<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
