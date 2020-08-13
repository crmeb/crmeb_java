package com.zbkj.crmeb.wechatMessage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MessageImageItemVo对象", description="微信消息 图片/语音 模板")
public class MessageImageItemVo{
    public MessageImageItemVo() {}
    public MessageImageItemVo(String mediaId) {
        MediaId = mediaId;
    }

    @ApiModelProperty(value = "通过素材管理中的接口上传多媒体文件，得到的id。")
    private String MediaId;
}
