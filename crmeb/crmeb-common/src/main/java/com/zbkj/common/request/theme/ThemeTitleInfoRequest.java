package com.zbkj.common.request.theme;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 主题标题信息保存请求
 */
@Data
@ApiModel(value = "ThemeTitleInfoRequest对象", description = "主题标题信息保存请求")
public class ThemeTitleInfoRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主题名称", required = true)
    @NotBlank(message = "请填写主题名称")
    @Length(max = 255, message = "主题名称长度不能超过255个字符")
    private String title;

    @ApiModelProperty(value = "主题简介")
    @Length(max = 255, message = "主题简介长度不能超过255个字符")
    private String info;

    @ApiModelProperty(value = "页面类型：theme主题，micro微页面")
    @JsonProperty("page_type")
    private String pageType;
}
