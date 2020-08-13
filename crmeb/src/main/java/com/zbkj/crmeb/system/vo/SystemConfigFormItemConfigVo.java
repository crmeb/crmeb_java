package com.zbkj.crmeb.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

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
public class SystemConfigFormItemConfigVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "")
    private String label;

    @ApiModelProperty(value = "")
    private String showLabel;

    @ApiModelProperty(value = "")
    private String changeTag;

    @ApiModelProperty(value = "")
    private String labelWidth;

    @ApiModelProperty(value = "")
    private String tag;

    @ApiModelProperty(value = "")
    private String tagIcon;

    @ApiModelProperty(value = "")
    private String span;

    @ApiModelProperty(value = "")
    private String layout;

    @ApiModelProperty(value = "")
    private Boolean required;

    @ApiModelProperty(value = "验证规则")
    private List<SystemConfigFormItemConfigRegListVo> regList;

    @ApiModelProperty(value = "")
    private String document;

    @ApiModelProperty(value = "")
    private String formId;

    @ApiModelProperty(value = "")
    private String renderKey;

    @ApiModelProperty(value = "")
    private String defaultValue;


}
