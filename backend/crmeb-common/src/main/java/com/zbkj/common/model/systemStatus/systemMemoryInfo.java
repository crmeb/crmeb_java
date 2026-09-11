package com.zbkj.common.model.systemStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

// 系统内存
@Data
public class systemMemoryInfo {


    @ApiModelProperty(value = "总内存")
    private String systemTotal;

    @ApiModelProperty(value = "已用内存")
    private String systemUsed;

    @ApiModelProperty(value = "剩余内存")
    private String systemFree;

    @ApiModelProperty(value = "使用率")
    private double systemUsageRate;
}
