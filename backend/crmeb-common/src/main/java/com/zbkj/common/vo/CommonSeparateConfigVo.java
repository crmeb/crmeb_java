package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 公共单独ConfigVO对象
 *
 * @author Hzw
 * @version 1.0.0
 * @Date 2024/1/3
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CommonSeparateConfigVo", description = "公共单独ConfigVO对象")
public class CommonSeparateConfigVo implements Serializable {

    private static final long serialVersionUID = -8779005742779172739L;

    @ApiModelProperty(value = "config Key")
    @NotBlank(message = "key不能为空")
    private String key;

    @ApiModelProperty(value = "config value")
    private String value;
}
