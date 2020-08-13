package com.zbkj.crmeb.system.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author stivepeim
 * @title: SystemRoleRequest
 * @projectName crmeb
 * @Description: TODO
 * @since 2020/4/1810:09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_role")
@ApiModel(value="SystemRole对象", description="身份管理表")
public class SystemRoleRequest {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "身份管理名称", required = true)
    @NotNull(message = "身份管理名称不能为空")
    private String roleName;

    @ApiModelProperty(value = "身份管理权限(menus_id)", required = true)
    @NotNull(message = "身份管理权限不能为空")
    private String rules;

    @ApiModelProperty(value = "身份管理名称", required = true)
    @Min(value=0)
    @NotNull(message = "level 不能为空，且为正整数")
    private Integer level;

    @ApiModelProperty(value = "状态", required = true)
    private Boolean status;
}
