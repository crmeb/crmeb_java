package com.zbkj.crmeb.wechatMessage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 微信消息 图片/语音 模板
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
@ApiModel(value="MessageImageItemVo对象", description="微信消息 图片/语音 模板")
public class MessageImageItemVo{
    public MessageImageItemVo() {}
    public MessageImageItemVo(String mediaId) {
        MediaId = mediaId;
    }

    @ApiModelProperty(value = "通过素材管理中的接口上传多媒体文件，得到的id。")
    private String MediaId;
}
