package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 运费模板包邮响应对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ShippingTemplatesFreeResponse对象", description = "运费模板包邮响应对象")
public class ShippingTemplatesFreeResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "描述")
    private String title;

    @ApiModelProperty(value = "包邮件数")
    private BigDecimal number;

    @ApiModelProperty(value = "包邮金额")
    private BigDecimal price;

    @ApiModelProperty(value = "分组唯一值")
    private String uniqid;
}
