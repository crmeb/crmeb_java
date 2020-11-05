package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.user.model.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * Login Response
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="LoginResponse", description="用户登录返回数据")
public class LoginResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户登录密钥")
    private String token;

    @ApiModelProperty(value = "用户登录密钥到期时间")
    private Date expiresTime;

    @ApiModelProperty(value = "user对象")
    private User user;

}
