package com.zbkj.common.response;



import com.zbkj.common.model.systemStatus.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

/**
 * 服务器系统状态
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
public class SystemStatusResponse {

    @ApiModelProperty(value = "CPU信息")
    private CpuInfo cpuInfo;

    @ApiModelProperty(value = "系统内存")
    private com.zbkj.common.model.systemStatus.systemMemoryInfo systemMemoryInfo;

    @ApiModelProperty(value = "JVM内存")
    private JvmMemoryInfo jvmMemoryInfo;

    @ApiModelProperty(value = "服务器信息")
    private ServerInfo serverInfo;

    @ApiModelProperty(value = "JVM信息")
    private JvmInfo jvmInfo;

    @ApiModelProperty(value = "磁盘信息")
    private List<DiskInfo> diskInfos;

}














