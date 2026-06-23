package com.zbkj.common.model.systemStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

// 服务器信息
@Data
public class ServerInfo {

    @ApiModelProperty(value = "服务器名称")
    private String serverName;

    @ApiModelProperty(value = "服务器IP")
    private String serverIP;

    @ApiModelProperty(value = "操作系统")
    private String os;

    @ApiModelProperty(value = "系统架构")
    private String systemArch;
}
