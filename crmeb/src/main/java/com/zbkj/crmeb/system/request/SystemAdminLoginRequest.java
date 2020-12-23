package com.zbkj.crmeb.system.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * SystemAdminLoginRequest
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
public class SystemAdminLoginRequest {
    @ApiModelProperty(value = "后台管理员账号", example = "userName")
    @NotEmpty(message = "账号 不能为空")
    private String account;

    @ApiModelProperty(value = "后台管理员密码", example = "userPassword")
    @NotEmpty(message = "密码 不能为空")
    @Length(min = 6, max = 30)
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
