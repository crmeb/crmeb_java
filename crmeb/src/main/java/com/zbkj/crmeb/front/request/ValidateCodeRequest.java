package com.zbkj.crmeb.front.request;


import com.constants.RegularConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * <p>
 * 验证码类
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ValidateCodeRequest对象", description="发送手机验证码类")
public class ValidateCodeRequest implements Serializable {
    @Pattern(regexp = RegularConstants.PHONE, message = "手机号码格式错误")
    private String phone;
}
