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
@ApiModel(value="WechatProgramMyTempRequest对象", description="小程序我的模板")
public class WechatProgramMyTempRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "微信模板id")
    private Integer tid;

    @ApiModelProperty(value = "模版标题")
    private String title;

    @ApiModelProperty(value = "模板关键字id，多个逗号分割")
    private String kid;

    @ApiModelProperty(value = "场景描述")
    private String sceneDesc;

    @ApiModelProperty(value = "扩展字段")
    private String extra;
}
