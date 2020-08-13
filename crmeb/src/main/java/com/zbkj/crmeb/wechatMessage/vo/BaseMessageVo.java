package com.zbkj.crmeb.wechatMessage.vo;

import com.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BaseMessageVo对象", description="微信消息基础模板")
public class BaseMessageVo{
    @ApiModelProperty(value = "开发者微信号")
    protected String ToUserName;

    @ApiModelProperty(value = "发送方帐号（一个OpenID）")
    protected String FromUserName;

    @ApiModelProperty(value = "消息创建时间 （整型）")
    protected Long CreateTime = DateUtil.getTime();

    @ApiModelProperty(value = "消息类型，文本为text")
    protected String MsgType = "text";
}
