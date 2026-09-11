package com.zbkj.common.response.theme;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 主题文章响应对象
 */
@Data
@ApiModel(value = "ThemeArticleResponse对象", description = "主题文章响应对象")
public class ThemeArticleResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文章ID")
    private Integer id;

    @ApiModelProperty(value = "分类ID")
    private String cid;

    @ApiModelProperty(value = "文章标题")
    private String title;

    @JsonProperty(value = "image_input")
    @ApiModelProperty(value = "文章图片")
    private List<String> imageInput;

    @ApiModelProperty(value = "浏览次数")
    private String visit;

    @JsonProperty(value = "add_time")
    @ApiModelProperty(value = "添加时间")
    private String addTime;
}
