package com.zbkj.crmeb.express.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 *  模版Request
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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_shipping_templates")
@ApiModel(value="ShippingTemplatesRequest对象", description="模板")
public class ShippingTemplatesRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "模板名称", required = true)
    @NotBlank(message = "模板名称必须填写")
    private String name;

    @ApiModelProperty(value = "计费方式 1(按件数), 2(按重量)，3(按体积)", example = "1", required = true)
    @NotNull(message = "计费方式必须选择")
    @Range(min = 1, max = 3, message = "计费方式选择区间 1(按件数), 2(按重量)，3(按体积)")
    private Integer type;

    @ApiModelProperty(value = "配送区域及运费", required = true)
    private List<ShippingTemplatesRegionRequest> shippingTemplatesRegionRequestList;

    @ApiModelProperty(value = "指定包邮", example = "1", required = true)
    @NotNull(message = "指定包邮必须选择")
    private Boolean appoint;

    @ApiModelProperty(value = "指定包邮设置", required = true)
    private List<ShippingTemplatesFreeRequest> shippingTemplatesFreeRequestList;

    @ApiModelProperty(value = "排序", example = "0")
    @NotNull(message = "排序数字必须填写")
    private Integer sort;

}
