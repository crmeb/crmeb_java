package com.zbkj.crmeb.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * item对象
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
