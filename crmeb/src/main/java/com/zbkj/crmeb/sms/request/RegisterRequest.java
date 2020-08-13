package com.zbkj.crmeb.sms.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.aspectj.bridge.IMessage;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 短信发送记录表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RegisterRequest对象", description="短信账号注册")
public class RegisterRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "账号", required = true)
    @NotNull(message = "账号不能为空")
    private String account;

    @ApiModelProperty(value = "密码", required = true)
    @NotNull(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "域名", required = true)
    @NotNull(message = "域名不能为空")
    private String domain;

    @ApiModelProperty(value = "手机号", required = true)
    @NotNull(message = "手机号不能为空")
    private String phone;

    @ApiModelProperty(value = "签名", required = true)
    @NotNull(message = "签名不能为空")
    private String sign;

    @ApiModelProperty(value = "验证码")
    @NotNull(message = "验证码不能为空")
    private String code;


}
