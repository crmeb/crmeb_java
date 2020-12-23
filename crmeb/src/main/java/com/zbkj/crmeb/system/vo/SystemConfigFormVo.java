package com.zbkj.crmeb.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * SystemConfigFormVo对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SystemConfigFormVo对象", description="form对象")
public class SystemConfigFormVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "表单名称")
    private String formRef;

    @ApiModelProperty(value = "form对象")
    private String formModel;

    @ApiModelProperty(value = "大小")
    private String size;

    @ApiModelProperty(value = "label位置")
    private String labelPosition;

    @ApiModelProperty(value = "label宽度")
    private String labelWidth;

    @ApiModelProperty(value = "form规则")
    private String formRules;

    @ApiModelProperty(value = "")
    private String gutter;

    @ApiModelProperty(value = "是否禁用")
    private String disabled;

    @ApiModelProperty(value = "span")
    private String span;

    @ApiModelProperty(value = "button")
    private String formBtns;

    @ApiModelProperty(value = "字段值列表")
    private List<String> fields;



}
