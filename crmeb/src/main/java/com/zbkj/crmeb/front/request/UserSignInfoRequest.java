package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserSignInfoRequest对象", description="个人签到信息")
public class UserSignInfoRequest implements Serializable {

    private static final long serialVersionUID=1L;


    @ApiModelProperty(value = "是否统计签到|1=是,0=否")
    private Boolean sign = false;

    @ApiModelProperty(value = "是否统计积分使用情况|1=是,0=否")
    private Boolean integral = false;

    @ApiModelProperty(value = "是否统计签到和统计积分使用情况|1=是,0=否")
    private Boolean all = false;

}
