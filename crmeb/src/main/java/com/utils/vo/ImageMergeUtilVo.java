package com.utils.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 图片合成详情
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
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
