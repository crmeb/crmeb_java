package com.zbkj.crmeb.wechat.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SendTemplateMessageItemVo对象", description="微信模板发送数据类")
public class SendProgramTemplateMessageItemVo {
    public SendProgramTemplateMessageItemVo() {}
    public SendProgramTemplateMessageItemVo(String value) {
        this.value = value;
    }

    @ApiModelProperty(value = "显示的文字内容", required = true)
    private String value;
}
