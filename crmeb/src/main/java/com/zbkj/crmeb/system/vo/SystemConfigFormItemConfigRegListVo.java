package com.zbkj.crmeb.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * SystemConfigFormItemConfigRegListVo对象
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SystemConfigFormItemConfigRegListVo对象", description="item对象验证规则")
public class SystemConfigFormItemConfigRegListVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "正则表达式")
    private String pattern;

    @ApiModelProperty(value = "错误提示语")
    private String message;
}
