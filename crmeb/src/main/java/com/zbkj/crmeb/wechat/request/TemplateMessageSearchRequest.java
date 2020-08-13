package com.zbkj.crmeb.wechat.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TemplateMessageSearchRequest {

    @ApiModelProperty(value = "模板名")
    private String name;

    @ApiModelProperty(value = "模板编号")
    private String tempKey;

    @ApiModelProperty(value = "模板ID")
    private String tempId;

    @ApiModelProperty(value = "状态, 0 正常，1 禁用")
    private Integer status = null;

    @ApiModelProperty(value = "类型， 0 订阅消息, 1 模板消息")
    private Integer type = null;
}
