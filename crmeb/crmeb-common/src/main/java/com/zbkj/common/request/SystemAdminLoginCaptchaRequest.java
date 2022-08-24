package com.zbkj.common.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * PC登录请求对象 行为验证码
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
public class SystemAdminLoginCaptchaRequest {

    @ApiModelProperty(required = true)
    String captchaVerification;

    @ApiModelProperty(required = true)
    String token;

    @ApiModelProperty(required = true)
    String secretKey;


}

