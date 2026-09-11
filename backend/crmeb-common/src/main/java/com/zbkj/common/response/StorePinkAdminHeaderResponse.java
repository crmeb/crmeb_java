package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "StorePinkAdminHeaderResponse", description = "拼团开团记录列表表头数量响应对象")
public class StorePinkAdminHeaderResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "拼团中数量")
    private Integer ingNum;

    @ApiModelProperty(value = "拼团成功数量")
    private Integer successNum;

    @ApiModelProperty(value = "拼团失败数量")
    private Integer failNum;
}
