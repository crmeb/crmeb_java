package com.zbkj.crmeb.wechat.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MediaDetailVo对象", description="微信素材列表详情")
public class MediaDetailVo {
    @ApiModelProperty(value = "媒体id")
    @JsonProperty(value = "media_id")
    private String mediaId;

    @ApiModelProperty(value = "名称")
    @JsonProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "更新时间")
    @JsonProperty(value = "update_time")
    private String updateTime;

    @ApiModelProperty(value = "访问地址")
    @JsonProperty(value = "url")
    private String url;
}
