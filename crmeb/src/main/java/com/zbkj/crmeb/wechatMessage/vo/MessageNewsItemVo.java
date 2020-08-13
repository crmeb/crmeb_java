package com.zbkj.crmeb.wechatMessage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MessageNewsItemVo对象", description="微信消息 图片/语音 模板")
public class MessageNewsItemVo{
    public MessageNewsItemVo() {}
    public MessageNewsItemVo(MessageNewsItemDetailVo data) {
        item = data;
    }

    @ApiModelProperty(value = "文章详情")
    private MessageNewsItemDetailVo item;
}
