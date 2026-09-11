package com.zbkj.common.model.systemStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
// CPU信息
@Data
public class CpuInfo {

    @ApiModelProperty(value = "核心数")
    private int coreCount;

    @ApiModelProperty(value = "用户使用率")
    private double userUsageRate;

    @ApiModelProperty(value = "系统使用率")
    private double systemUsageRate;

    @ApiModelProperty(value = "当前空闲率")
    private double freeUsageRate;
}
