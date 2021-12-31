package com.zbkj.common.model.wechat;

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
 * 微信回调表
 * </p>
 *
 * @author HZW
 * @since 2021-05-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_wechat_callback")
@ApiModel(value="WechatCallback对象", description="微信回调表")
public class WechatCallback implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商家小程序名称")
    private String toUserName;

    @ApiModelProperty(value = "微信团队的 OpenID(固定值)")
    private String fromUserName;

    @ApiModelProperty(value = "事件时间,Unix时间戳")
    private Long createTime;

    @ApiModelProperty(value = "消息类型")
    private String msgType;

    @ApiModelProperty(value = "事件类型")
    private String event;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "创建时间")
    private Date addTime;


}
