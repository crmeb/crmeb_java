package com.zbkj.common.request.theme;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 主题模块保存请求
 */
@Data
@ApiModel(value = "ThemeSaveRequest对象", description = "主题模块保存请求")
public class ThemeSaveRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模板主题ID，0表示不复制")
    private Integer tid = 0;

    @ApiModelProperty(value = "主题名称")
    @Length(max = 255, message = "主题名称长度不能超过255个字符")
    private String title;

    @ApiModelProperty(value = "模块类型：home/category/detail/user/theme", required = true)
    @NotBlank(message = "请选择主题模块类型")
    private String type;

    @ApiModelProperty(value = "模块配置数据")
    private Object value = "";

    @ApiModelProperty(value = "页面类型：theme主题，micro微页面")
    @JsonProperty(value = "page_type")
    @JsonAlias(value = "pageType")
    @Length(max = 255, message = "页面类型长度不能超过255个字符")
    private String pageType = "theme";
}
