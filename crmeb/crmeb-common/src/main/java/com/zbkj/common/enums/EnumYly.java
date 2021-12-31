package com.zbkj.common.enums;

/** 易联云 枚举
 * @program: crmeb
 * @author: 大粽子
 * @create: 2021-11-27 10:38
 **/
public enum EnumYly {
    VOLUME_RESPONSE_TYPE_FENGMINGQI("蜂鸣器","buzzer"),
    VOLUME_RESPONSE_TYPE_HORN("喇叭","horn"),
    VOLUME_RESPONSE_VOICE1("音量1","1"),
    VOLUME_RESPONSE_VOICE2("音量2","2"),
    VOLUME_RESPONSE_VOICE3("音量3","3"),
    VOLUME_RESPONSE_VOICE4("音量4","4"),
    ;

    private String name;
    private String code;

    EnumYly(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
