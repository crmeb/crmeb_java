package com.zbkj.common.request.theme;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 主题图片保存请求
 */
@Data
@ApiModel(value = "ThemeImageRequest对象", description = "主题图片保存请求")
public class ThemeImageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "图片所属模块类型：home/detail/user", required = true)
    @NotBlank(message = "请选择图片所属模块类型")
    private String type;

    @ApiModelProperty(value = "图片地址", required = true)
    @NotBlank(message = "请上传图片")
    @Length(max = 255, message = "图片地址长度不能超过255个字符")
    private String image;
}
