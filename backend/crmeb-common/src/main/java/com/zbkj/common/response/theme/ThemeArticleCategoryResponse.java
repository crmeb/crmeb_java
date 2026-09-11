package com.zbkj.common.response.theme;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 主题文章分类响应对象
 */
@Data
@ApiModel(value = "ThemeArticleCategoryResponse对象", description = "主题文章分类响应对象")
public class ThemeArticleCategoryResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分类ID")
    private Integer id;

    @ApiModelProperty(value = "父级ID")
    private Integer pid;

    @ApiModelProperty(value = "分类名称")
    private String title;
}
