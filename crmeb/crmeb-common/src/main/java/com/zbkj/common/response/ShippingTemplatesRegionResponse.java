package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 运费模板区域响应对象
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
@ApiModel(value = "ShippingTemplatesRegionResponse对象", description = "运费模板区域响应对象")
public class ShippingTemplatesRegionResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "城市名称描述")
    private String title;

    @ApiModelProperty(value = "首件", required = true, example = "0.1")
    @DecimalMin(value = "0.1", message = "首件金额不能低于0.1")
    private BigDecimal first;

    @ApiModelProperty(value = "首件运费", required = true, example = "0.1")
    @DecimalMin(value = "0.1", message = "首件运费金额不能低于0.1")
    private BigDecimal firstPrice;

    @ApiModelProperty(value = "续件", required = true, example = "0.1")
    @DecimalMin(value = "0.1", message = "续件不能低于0.1")
    private BigDecimal renewal;

    @ApiModelProperty(value = "续件运费", required = true, example = "0.1")
    @DecimalMin(value = "0.1", message = "续件运费金额不能低于0.1")
    private BigDecimal renewalPrice;

    @ApiModelProperty(value = "分组唯一值")
    private String uniqid;
}
