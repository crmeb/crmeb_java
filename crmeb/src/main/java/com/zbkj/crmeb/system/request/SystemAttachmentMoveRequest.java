package com.zbkj.crmeb.system.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SystemAttachmentMoveRequest {


    @ApiModelProperty(value = "父级id")
    private Integer pid;

    @ApiModelProperty(value = "附件id")
    @NotBlank(message = "请选择附件")
    private String attrId;
}
