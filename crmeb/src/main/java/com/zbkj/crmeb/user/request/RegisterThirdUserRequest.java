package com.zbkj.crmeb.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@ApiModel(value="RegisterThirdUserRequest对象", description="三方用户注册对象")
public class RegisterThirdUserRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户昵称", required = true)
    private String nickName;

    @ApiModelProperty(value = "性别", required = true)
    private String sex;

    @ApiModelProperty(value = "用户个人资料填写的省份")
    private String province;

    @ApiModelProperty(value = "普通用户个人资料填写的城市")
    private String city;

    @ApiModelProperty(value = "国家，如中国为CN")
    private String country;

    @ApiModelProperty(value = "用户头像", required = true)
    private String avatar;

    @ApiModelProperty(value = "推广人id")
    @JsonProperty(value = "spread_spid")
    private Integer spreadPid = 0;
}
