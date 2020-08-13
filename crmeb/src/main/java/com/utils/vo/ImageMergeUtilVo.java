package com.utils.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 图片合成详情
 * @author Mr.Zhang
 * @since 2020-04-18
 */
@Data
public class ImageMergeUtilVo {

    @ApiModelProperty(value = "图片地址", required = true)
    @NotBlank(message = "图片地址必须填写")
    private String path; //地址

    @ApiModelProperty(value = "x轴", required = true)
    @Min(value = 0, message = "x轴至少为0")
    private int x; //x轴

    @ApiModelProperty(value = "y轴", required = true)
    @Min(value = 0, message = "y轴至少为0")
    private int y; //y轴
}
