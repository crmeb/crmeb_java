package com.zbkj.crmeb.user.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户通知表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_notice")
@ApiModel(value="UserNotice对象", description="用户通知表")
public class UserNoticeRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "接收消息的用户id（类型：json数据）")
    private String uid;

    @ApiModelProperty(value = "消息通知类型（1：系统消息；2：用户通知）")
    private Boolean type;

    @ApiModelProperty(value = "发送人")
    private String user;

    @ApiModelProperty(value = "通知消息的标题信息")
    private String title;

    @ApiModelProperty(value = "通知消息的内容")
    private String content;

    @ApiModelProperty(value = "通知消息发送的时间")
    private Integer addTime;

    @ApiModelProperty(value = "是否发送（0：未发送；1：已发送）")
    private Boolean isSend;

    @ApiModelProperty(value = "发送时间")
    private Integer sendTime;


}
