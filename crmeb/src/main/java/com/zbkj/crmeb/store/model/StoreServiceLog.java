package com.zbkj.crmeb.store.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 客服用户对话记录表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_service_log")
@ApiModel(value="StoreServiceLog对象", description="客服用户对话记录表")
public class StoreServiceLog implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "客服用户对话记录表ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "消息内容")
    private String msn;

    @ApiModelProperty(value = "发送人uid")
    private Integer uid;

    @ApiModelProperty(value = "接收人uid")
    private Integer toUid;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否已读（0：否；1：是；）")
    private Boolean type;

    @ApiModelProperty(value = "是否提醒过")
    private Boolean remind;

    @ApiModelProperty(value = "消息类型 1=文字 2=表情 3=图片 4=语音")
    private Boolean msnType;


}
