package com.zbkj.crmeb.sms.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Classname SmsLoginRequest
 * @Description 短信API登录
 * @Date 2020/6/15 2:23 下午
 * @Created by stivepeim
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SmsLoginRequest对象", description="短信登录")
public class SmsLoginRequest {

    @ApiModelProperty(value = "账号", required = true)
    private String account;

    @ApiModelProperty(value = "smsToken", required = true)
    private String token;
}
