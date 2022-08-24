package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 短信修改签名请求对象
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
@ApiModel(value = "SmsModifySignRequest对象", description = "短信修改签名请求对象")
public class SmsModifySignRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "新的短信签名", required = true)
    @NotBlank(message = "短信签名不能为空")
    private String sign;

    @ApiModelProperty(value = "账号绑定的手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @ApiModelProperty(value = "短信验证码", required = true)
    @NotBlank(message = "短信验证码不能为空")
    private String code;

}
