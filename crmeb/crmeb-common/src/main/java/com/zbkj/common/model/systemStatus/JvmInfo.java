package com.zbkj.common.model.systemStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
// JVM信息
@Data
public class JvmInfo {

    @ApiModelProperty(value = "Java名称")
    private String javaName;

    @ApiModelProperty(value = "Java版本")
    private String javaVersion;

    @ApiModelProperty(value = "启动时间")
    private Date startTime;

    @ApiModelProperty(value = "运行时长")
    private String uptime;

    @ApiModelProperty(value = "安装路径")
    private String installPath;

    @ApiModelProperty(value = "项目路径")
    private String projectPath;

    @ApiModelProperty(value = "运行参数")
    private List<String> jvmOptions;
}
