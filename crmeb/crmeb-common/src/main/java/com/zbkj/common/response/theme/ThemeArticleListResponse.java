package com.zbkj.common.response.theme;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 主题文章列表响应对象
 */
@Data
@ApiModel(value = "ThemeArticleListResponse对象", description = "主题文章列表响应对象")
public class ThemeArticleListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文章列表")
    private List<ThemeArticleResponse> list = new ArrayList<>();

    @ApiModelProperty(value = "总数")
    private Long count = 0L;
}
