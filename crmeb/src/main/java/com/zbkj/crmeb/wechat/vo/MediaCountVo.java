package com.zbkj.crmeb.wechat.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.HashMap;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MediaCountVo对象", description="微信素材总数")
public class MediaCountVo {
    @ApiModelProperty(value = "音频")
    @JsonProperty(value = "voice_count")
    private int voice;

    @ApiModelProperty(value = "视频")
    @JsonProperty(value = "video_count")
    private int video;

    @ApiModelProperty(value = "图片")
    @JsonProperty(value = "image_count")
    private int image;

    @ApiModelProperty(value = "新闻")
    @JsonProperty(value = "news_count")
    private int news;
}
