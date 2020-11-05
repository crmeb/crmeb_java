package com.zbkj.crmeb.system.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 身份管理搜索Request对象
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
public class SystemRoleSearchRequest {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "身份管理名称")
//    @NotNull(message = "身份管理名称不能为空")
    private String roleName;

    @ApiModelProperty(value = "身份管理权限(menus_id)")
//    @NotNull(message = "身份管理权限不能为空")
    private String rules;

    @ApiModelProperty(value = "身份管理名称")
//    @Min(value=0)
//    @NotNull(message = "level 不能为空，且为正整数")
    private Integer level;

    @ApiModelProperty(value = "状态")
    private Boolean status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
}
