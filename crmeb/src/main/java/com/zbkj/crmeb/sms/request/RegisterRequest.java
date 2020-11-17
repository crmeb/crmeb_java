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
 * 短信账号注册
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

    private String verify_code;

    private String url;
}
