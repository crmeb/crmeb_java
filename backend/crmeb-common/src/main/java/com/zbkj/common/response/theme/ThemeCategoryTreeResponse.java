package com.zbkj.common.response.theme;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 主题产品分类响应对象
 */
@Data
@ApiModel(value = "ThemeCategoryTreeResponse对象", description = "主题产品分类响应对象")
public class ThemeCategoryTreeResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分类ID")
    private Integer id;

    @ApiModelProperty(value = "分类值")
    private Integer value;

    @ApiModelProperty(value = "分类名称")
    private String label;

    @ApiModelProperty(value = "分类标题")
    private String title;

    @ApiModelProperty(value = "父级ID")
    private Integer pid;

    @ApiModelProperty(value = "子级分类")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ThemeCategoryTreeResponse> children;
}
