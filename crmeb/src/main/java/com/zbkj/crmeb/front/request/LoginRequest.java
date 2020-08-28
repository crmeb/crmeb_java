package com.zbkj.crmeb.front.request;

import com.constants.RegularConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * <p>
 * 手机快速登录
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="LoginRequest对象", description="手机快速登录")
public class LoginRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "手机号", required = true, example = "18888888")
    @Pattern(regexp = RegularConstants.PHONE, message = "手机号码格式错误")
    @JsonProperty(value = "account")
    private String phone;

    @ApiModelProperty(value = "密码", required = true, example = "1~[6,18]")
    @Pattern(regexp = RegularConstants.PASSWORD, message = "密码格式错误，密码必须以字母开头，长度在6~18之间，只能包含字符、数字和下划线")
    private String password;

    @ApiModelProperty(value = "推广人id")
    @JsonProperty(value = "spread_spid")
    private Integer spreadPid = 0;
}