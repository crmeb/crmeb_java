package com.zbkj.common.model.systemStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

// 磁盘信息
@Data
public class DiskInfo {

    @ApiModelProperty(value = "磁盘路径")
    private String path;

    @ApiModelProperty(value = "文件系统")
    private String fileSystem;

    @ApiModelProperty(value = "盘符类型")
    private String type;

    @ApiModelProperty(value = "总大小")
    private String totalSize;

    @ApiModelProperty(value = "可用大小")
    private String freeSize;

    @ApiModelProperty(value = "已用大小")
    private String usedSize;

    @ApiModelProperty(value = "已用百分比")
    private double usedPercentage;

}
