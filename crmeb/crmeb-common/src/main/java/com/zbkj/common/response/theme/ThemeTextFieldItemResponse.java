package com.zbkj.common.response.theme;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 主题文本字段项响应对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "ThemeTextFieldItemResponse对象", description = "主题文本字段项响应对象")
public class ThemeTextFieldItemResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "字段展示名称")
    private String label;

    @ApiModelProperty(value = "字段标识")
    private String value;
}
