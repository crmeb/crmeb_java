package com.zbkj.crmeb.system.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author stivepeim
 * @title: SystemAdminLoginRequest
 * @projectName crmeb
 * @Description: TODO
 * @since 2020/4/1418:29
 */
@Data
public class SystemAdminLoginRequest {
    @ApiModelProperty(value = "后台管理员账号", example = "userName")
    @NotEmpty(message = "账号 不能为空")
    private String account;

    @ApiModelProperty(value = "后台管理员密码", example = "userPassword")
    @NotEmpty(message = "密码 不能为空")
    private String pwd;

    @ApiModelProperty(value = "key", required = true)
    @NotEmpty(message = "验证码key 不能为空")
    private String key;

    @ApiModelProperty(value = "code", required = true)
    @NotEmpty(message = "验证码 不能为空")
    private String code;

    @ApiModelProperty(value = "微信授权code")
    private String wxCode;
}
