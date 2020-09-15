package com.zbkj.crmeb.wechat.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 小程序我的模板
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-08-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WechatProgramMyTempSearchRequest对象", description="小程序我的模板")
public class WechatProgramMyTempSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "模版标题")
    private String title;

    @ApiModelProperty(value = "我的模板id")
    private String tempId;

    @ApiModelProperty(value = "状态 0，禁用，1启用")
    private Boolean status;

    @ApiModelProperty(value = "应用场景")
    private String type;

    private Integer id;
}
