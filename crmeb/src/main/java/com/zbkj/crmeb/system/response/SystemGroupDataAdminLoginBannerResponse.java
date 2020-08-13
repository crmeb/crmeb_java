package com.zbkj.crmeb.system.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 门店店员表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SystemGroupDataAdminLoginBannerResponse对象", description="后台登录页面轮播图")
public class SystemGroupDataAdminLoginBannerResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "图片")
    private String pic;

}
