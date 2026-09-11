package com.zbkj.common.model.systemStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
// JVM内存
@Data
public class JvmMemoryInfo {

    @ApiModelProperty(value = "JVM总内存")
    private String jvmTotal;

    @ApiModelProperty(value = "JVM已用内存")
    private String jvmUsed;

    @ApiModelProperty(value = "JVM剩余内存")
    private String jvmFree;

    @ApiModelProperty(value = "JVM使用率")
    private double jvmUsageRate;
}
