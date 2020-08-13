package com.utils.vo;

import lombok.Data;

/**
 * @author stivepeim
 * @title: dateLimitVoUtil
 * @projectName crmeb
 * @Description: 工具类
 * @since 2020/4/1414:37
 */
@Data
public class dateLimitUtilVo {
    public dateLimitUtilVo() {}
    public dateLimitUtilVo(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private String startTime; //开始时间

    private String endTime; //结束时间
}
