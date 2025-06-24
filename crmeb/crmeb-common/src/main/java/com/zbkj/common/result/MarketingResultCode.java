package com.zbkj.common.result;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.exception.BusinessExceptionAssert;

/**
 * @ClassName MarketingResultCode
 * @Description 营销模块响应码
 * @Author HZW
 * @Date 2023/5/17 9:17
 * @Version 1.0
 */
public enum MarketingResultCode implements BusinessExceptionAssert {

    ARTICLE_CATEGORY_USED(12001, "文章分类已使用"),
    ARTICLE_CATEGORY_NOT_EXIST(12002, "文章分类不存在"),
    ARTICLE_NOT_EXIST(12003, "文章不存在"),

    SECKILL_TIME_INTERVAL_NOT_EXIST(12101, "秒杀时段不存在"),
    SECKILL_TIME_INTERVAL_USED(12102, "秒杀时段已使用"),
    SECKILL_ACTIVITY_NOT_EXIST(12103, "秒杀活动不存在"),
    SECKILL_ACTIVITY_STATUS_ABNORMAL(12104, "秒杀活动状态异常"),
    SECKILL_ACTIVITY_PRODUCT_NOT_EXIST(12105, "秒杀活动商品不存在"),
    SECKILL_ACTIVITY_PRODUCT_STATUS_ABNORMAL(12105, "秒杀活动商品状态异常"),

    SECKILL_ACTIVITY_END(12106, "秒杀活动已经结束"),
    SECKILL_ACTIVITY_MERCHANT_LEVEL_INSUFFICIENT(12107, "秒杀活动商家等级不足"),
    ;

    MarketingResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 响应状态码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 响应信息补充
     */
    private Object[] msgParams;


    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public MarketingResultCode setCode(Integer code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        if (ArrayUtil.isNotEmpty(msgParams)) {
            return StrUtil.format(message, msgParams);
        }
        return message;
    }

    @Override
    public MarketingResultCode setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public IResultEnum setMsgParams(Object... msgParams) {
        this.msgParams = msgParams;
        return this;
    }

    @Override
    public Object[] getMsgParams() {
        return msgParams;
    }
}
