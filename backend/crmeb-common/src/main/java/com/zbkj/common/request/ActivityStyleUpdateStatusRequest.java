package com.zbkj.common.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 活动边框 更新状态
 * @Auther: 大粽子
 * @Date: 2023/1/5 15:38
 * @Description: 描述对应的业务场景
 */
@Data
public class ActivityStyleUpdateStatusRequest {
    @ApiModelProperty(value = "主键")
    @NotNull(message = "id 不能为空")
    private Integer id;

    @ApiModelProperty(value = "状态 0=关闭1=开启")
    private Boolean status;
}
