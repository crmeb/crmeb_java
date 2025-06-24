package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 登录用户修改密码请求对象
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
@ApiModel(value = "LoginAdminUpdatePasswordRequest", description = "登录用户修改密码请求对象")
public class LoginAdminUpdatePasswordRequest {

    @ApiModelProperty(value = "后台管理员密码", required = true)
    @NotBlank(message = "管理员密码不能为空")
    @Length(min = 6, max = 30, message = "密码长度在6-30个字符")
    private String password;

    @ApiModelProperty(value = "后台管理员密码", required = true)
    @NotBlank(message = "管理员确认密码不能为空")
    @Length(min = 6, max = 30, message = "密码长度在6-30个字符")
    private String confirmPassword;

    @ApiModelProperty(value = "后台管理员原密码", required = true)
    @NotBlank(message = "管理员原密码不能为空")
    @Length(min = 6, max = 30, message = "原密码长度在6-30个字符")
    private String oldPassword;
}
