package com.zbkj.common.result;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.exception.BusinessExceptionAssert;

/**
 * @ClassName SystemFormResultCode
 * @Description 系统设置响应码枚举
 * @Author HZW
 * @Date 2023/3/4 11:12
 * @Version 1.0
 */
public enum SystemConfigResultCode implements BusinessExceptionAssert {

    FORM_TEMP_NOT_EXIST(7101, "表单不存在"),
    FORM_TEMP_PARAMETER_ERROR(7102, "模板表单 【{}】 的内容不是正确的JSON格式！"),
    FORM_TEMP_NAME_REPEAT(7103, "模板表单名称重复"),

    CATEGORY_NOT_EXIST(7201, "分类不存在"),

    NOTIFICATION_NOT_EXIST(7301, "消息通知不存在"),
    NOTIFICATION_SMS_CLOSE(7302, "消息通知短信开关未开启"),
    SMS_TEMPLATE_NOT_EXIST(7303, "短信模板不存在"),
    NOTIFICATION_NOT_CONFIG_PUBLIC(7304, "消息通知没有配置公众号模板"),
    NOTIFICATION_NOT_CONFIG_ROUTINE(7304, "消息通知没有配置小程序订阅模板"),

    SIGN_COFNIG_NOT_EXIST(7401, "签到配置不存在"),

    MENU_NOT_EXIST(7501, "菜单不存在"),
    ROLE_NOT_EXIST(7502, "角色不存在"),
    ROLE_DELETE(7502, "角色已删除"),
    ROLE_USED(7502, "角色已使用"),

    USER_LEVEL_NOT_EXIST(7601, "系统用户等级不存在"),

    PAGE_DIY_NOT_EXIST(7801, "diy模板信息不存在"),

    CITY_REGION_NOT_EXIST(7901, "城市区域不存在"),
    EXPRESS_NOT_EXIST(7905, "快递公司不存在"),

    ;

    SystemConfigResultCode(Integer code, String message) {
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
    public SystemConfigResultCode setCode(Integer code) {
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
    public SystemConfigResultCode setMessage(String message) {
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
