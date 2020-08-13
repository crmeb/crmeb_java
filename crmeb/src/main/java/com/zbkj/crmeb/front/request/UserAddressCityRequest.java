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
 * 用户地址表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserAddressCityRequest对象", description="用户地址城市")
public class UserAddressCityRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "收货人所在省", required = true)
    @NotBlank
    private String province;

    @ApiModelProperty(value = "收货人所在市", required = true)
    @NotBlank
    private String city;

    @ApiModelProperty(value = "城市id")
    private Integer cityId = 0;

    @ApiModelProperty(value = "收货人所在区", required = true)
    @NotBlank
    private String district;

}
