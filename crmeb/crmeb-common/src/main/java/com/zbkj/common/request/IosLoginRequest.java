package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * ios登录请求体
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="IosLoginRequest对象", description="ios登录请求体")
public class IosLoginRequest {

    @ApiModelProperty(value = "iosToken", required = true)
//    @NotBlank(message = "identityToken不能为空")
    private String identityToken;

    @ApiModelProperty(value = "App服务商唯一用户标识", required = true)
    @NotBlank(message = "openId不能为空")
    private String openId;

    @ApiModelProperty(value = "Ios用户电子邮箱")
//    @NotBlank(message = "email不能为空")
    private String email;
}
