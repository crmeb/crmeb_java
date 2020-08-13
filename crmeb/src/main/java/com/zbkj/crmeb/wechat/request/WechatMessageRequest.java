package com.zbkj.crmeb.wechat.request;

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
 * 用户行为记录表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_wechat_message")
@ApiModel(value="WechatMessage对象", description="用户行为记录表")
public class WechatMessageRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户openid")
    private String openid;

    @ApiModelProperty(value = "操作类型")
    private String type;

    @ApiModelProperty(value = "操作详细记录")
    private String result;

    @ApiModelProperty(value = "操作时间")
    private Integer addTime;


}
