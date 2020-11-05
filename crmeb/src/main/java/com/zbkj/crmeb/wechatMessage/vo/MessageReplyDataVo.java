package com.zbkj.crmeb.wechatMessage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 数据存储回复消息内容对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MessageReplyDataVo对象", description="数据存储回复消息内容对象")
public class MessageReplyDataVo{

    @ApiModelProperty(value = "文本消息内容")
    private String Content;

    @ApiModelProperty(value = "图片/音频链接")
    private String src;

    @ApiModelProperty(value = "图片/音频媒体ID")
    private String mediaId;

    @ApiModelProperty(value = "文本消息内容")
    private Integer articleId;
}
