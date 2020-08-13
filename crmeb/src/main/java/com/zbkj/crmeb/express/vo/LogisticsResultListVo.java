package com.zbkj.crmeb.express.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="LogisticsResultListVo对象", description="快递轨迹")
public class LogisticsResultListVo {

    @ApiModelProperty(value = "时间")
    private String time;

    @ApiModelProperty(value = "日志")
    private String status;
}
