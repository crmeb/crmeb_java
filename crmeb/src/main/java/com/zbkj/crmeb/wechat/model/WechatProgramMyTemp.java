package com.zbkj.crmeb.wechat.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
@TableName("eb_wechat_program_my_temp")
@ApiModel(value="WechatProgramMyTemp对象", description="小程序我的模板")
public class WechatProgramMyTemp implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "微信模板id")
    private Integer tid;

    @ApiModelProperty(value = "模版标题")
    private String title;

    @ApiModelProperty(value = "模板关键字id，多个逗号分割")
    private String kid;

    @ApiModelProperty(value = "场景描述")
    private String sceneDesc;

    @ApiModelProperty(value = "微信模板id")
    private String tempId;

    @ApiModelProperty(value = "扩展字段")
    private String extra;

    @ApiModelProperty(value = "状态 0，禁用，1启用")
    private Boolean status;

    @ApiModelProperty(value = "应用场景")
    private String type;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
