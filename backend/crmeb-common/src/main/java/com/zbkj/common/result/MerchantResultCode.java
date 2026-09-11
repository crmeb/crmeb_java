package com.zbkj.common.result;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.exception.BusinessExceptionAssert;

/**
 * @ClassName MerchantResultCode
 * @Description 商户模块响应状态码
 * @Author HZW
 * @Date 2024/4/6 12:20
 * @Version 1.0
 */
public enum MerchantResultCode implements BusinessExceptionAssert {

    MERCHANT_EXIST(4101, "商户已存在"),
    MERCHANT_NOT_EXIST(4102, "商户不存在"),
    MERCHANT_SWITCH_CLOSE(4103, "商户开关未开启"),
    MERCHANT_SWITCH_OPEN(4104, "商户开关已开启"),
    MERCHANT_NOT_TAKE_THEIR(4104, "商户未开启自提"),

    MERCHANT_APPLY_NOT_EXIST(4201, "商户申请单不存在"),

    MERCHANT_TYPE_NOT_EXIST(4301, "商户类型不存在"),
    MERCHANT_TYPE_USED(4302, "商户类型已使用"),

    MERCHANT_ADDRESS_NOT_EXIST(4401, "商户地址不存在"),

    MERCHANT_CATEGORY_NOT_EXIST(4501, "商户分类不存在"),
    MERCHANT_CATEGORY_USED(4502, "商户分类已使用"),

    MERCHANT_CLOSING_RECORD_NOT_EXIST(4601, "商户结算单不存在"),
    MERCHANT_CLOSING_RECORD_AUDIT_ED(4602, "商户提现申请已经处理"),
    MERCHANT_CLOSING_RECORD_AUDIT_STATUS_ABNORMAL(4603, "商户提现申请审核状态异常"),

    MERCHANT_BALANCE_NOT_ENOUGH(4701, "商户余额不足"),

    MERCHANT_PRINT_NOT_EXIST(4701, "商户打印机不存在"),

    MERCHANT_EXPRESS_NOT_EXIST(4801, "商户物流公司不存在"),

    ;

    MerchantResultCode(Integer code, String message) {
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
    public MerchantResultCode setCode(Integer code) {
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
    public MerchantResultCode setMessage(String message) {
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
