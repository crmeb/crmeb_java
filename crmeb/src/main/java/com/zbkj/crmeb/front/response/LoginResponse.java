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
 * <p>
 * 用户地址表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-28
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
