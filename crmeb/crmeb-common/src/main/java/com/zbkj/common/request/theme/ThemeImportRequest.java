package com.zbkj.common.request.theme;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 主题导入请求
 */
@Data
@ApiModel(value = "ThemeImportRequest对象", description = "主题导入请求")
public class ThemeImportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "上传后的ZIP文件路径", required = true)
    @NotBlank(message = "请上传主题ZIP文件")
    private String url;
}
