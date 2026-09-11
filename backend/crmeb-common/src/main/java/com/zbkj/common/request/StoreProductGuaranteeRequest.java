package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品保障服务请求对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreProductGuaranteeRequest对象", description="商品保障服务请求对象")
public class StoreProductGuaranteeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "保障条款ID,新增时不填，修改时必填")
    private Integer id;

    @ApiModelProperty(value = "保障条款名称")
    @NotEmpty(message = "保障条款名称不能为空")
    @Length(max = 100, message = "保障条款名称不能超过100个字符")
    private String name;

    @ApiModelProperty(value = "图标")
    @NotEmpty(message = "图标不能为空")
    private String icon;

    @ApiModelProperty(value = "条款内容")
    @NotEmpty(message = "条款内容不能为空")
    @Length(max = 100, message = "条款内容不能超过100个字符")
    private String content;

    @ApiModelProperty(value = "排序")
    @NotNull(message = "排序不能为空")
    @Range(min = 0, max = 999, message = "排序的范围为0-999")
    private Integer sort;

    @ApiModelProperty(value = "显示状态")
    private Boolean isShow;

}
