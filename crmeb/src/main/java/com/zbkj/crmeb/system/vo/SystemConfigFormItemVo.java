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
 * item对象
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SystemConfigFormItemVo对象", description="item对象")
public class SystemConfigFormItemVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "配置")
    private SystemConfigFormItemConfigVo __config__;

    @ApiModelProperty(value = "")
    private String placeholder;

    @ApiModelProperty(value = "")
    private String step;

    @ApiModelProperty(value = "")
    private String stepStrictly;

    @ApiModelProperty(value = "")
    private String controlsPosition;

    @ApiModelProperty(value = "")
    private String disabled;

    @ApiModelProperty(value = "")
    private String __vModel__;



}
