package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 整体保存表单数据
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SystemFormCheckRequest对象", description="整体保存表单数据")
public class SystemFormCheckRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表单名称", required = true)
    @Min(value = 0, message = "请选择表单")
    private Integer id;

    @ApiModelProperty(value = "排序", required = true)
    private Integer sort;

    @ApiModelProperty(value = "状态（1：开启；0：关闭；）")
    private Boolean status;

    @ApiModelProperty(value = "字段值列表", required = true)
    @NotEmpty(message = "fields 至少要有一组数据")
    @Size(min = 1, message = "fields 至少要有一组数据")
    private List<SystemFormItemCheckRequest> fields;

}
