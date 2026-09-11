package com.zbkj.common.result;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.exception.BusinessExceptionAssert;

/**
 * @ClassName ProductResultCode
 * @Description 商品模块响应状态码
 * @Author HZW
 * @Date 2024/4/6 12:20
 * @Version 1.0
 */
public enum ProductResultCode implements BusinessExceptionAssert {

    PRODUCT_EXIST(6101, "商品已存在"),
    PRODUCT_NOT_EXIST(6102, "商品不存在"),
    PRODUCT_RECYCLE(6103, "商品已进入回收站"),
    PRODUCT_AUDIT_STATUS_EXCEPTION(6104, "商品审核状态异常"),
    PRODUCT_DELETE(6105, "商品已经删除"),
    PRODUCT_IS_SHOW(6106, "商品已上架"),
    PRODUCT_AUDIT_ING(6107, "商品审核中"),

    PRODUCT_PLAT_CATEGORY_NOT_EXIST(6201, "商品平台分类不存在"),
    PRODUCT_PLAT_CATEGORY_USED(6202, "商品分类已使用"),
    PRODUCT_MER_CATEGORY_NOT_EXIST(6203, "商户商品分类不存在"),
    PRODUCT_MER_CATEGORY_USED(6204, "商户商品分类已使用"),

    PRODUCT_CDKEY_LIBRARY_NOT_EXIST(6301, "商品卡密库不存在"),
    PRODUCT_CDKEY_LIBRARY_INTERACTION(6302, "商品卡密库已经关联"),
    PRODUCT_CDKEY_NOT_QUICK_ADD(6303, "卡密商品不支持快捷添加库存"),
    PRODUCT_CARD_SECRET_NOT_EXIST(6304, "商品卡密不存在"),
    PRODUCT_CARD_SECRET_USED(6305, "商品卡密已使用"),
    PRODUCT_CARD_SECRET_DELETE(6306, "商品卡密已删除"),

    PRODUCT_TAG_STATUS_TRUE(6401, "商品标签状态已开启"),

    SHIPPING_TEMPLATES_NOT_EXIST(6501, "运费模板不存在"),
    SHIPPING_TEMPLATES_USED(6502, "运费模板已使用"),

    PRODUCT_RULE_EXIST(6601, "商品规格值已经存在"),
    PRODUCT_RULE_NOT_EXIST(6602, "商品规格值不存在"),

    PRODUCT_BRAND_USED(6701, "商品品牌已使用"),
    PRODUCT_BRAND_NOT_EXIST(6702, "商品品牌不存在"),
    PRODUCT_BRAND_EXIST(6703, "商品品牌已存在"),

    PRODUCT_GUARANTEE_GROUP_NOT_EXIST(6801, "商品保障服务组合不存在"),
    PRODUCT_GUARANTEE_USED(6802, "商品保障服务已使用"),
    PRODUCT_GUARANTEE_NOT_EXIST(6803, "商品保障服务不存在"),

    PRODUCT_ATTR_VALUE_NOT_EXIST(6901, "商品规格属性值不存在"),

    PRODUCT_REPLY_NOT_EXIST(6902, "商品评论不存在"),
    PRODUCT_REPLY_ED(6903, "商品已评论"),


    ;

    ProductResultCode(Integer code, String message) {
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
    public ProductResultCode setCode(Integer code) {
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
    public ProductResultCode setMessage(String message) {
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
