package com.zbkj.crmeb.wechatMessage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 微信消息 图片/语音 详情 模板
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
@ApiModel(value="MessageNewsItemDetailVo对象", description="微信消息 图片/语音 模板")
public class MessageNewsItemDetailVo{
    public MessageNewsItemDetailVo() {}
    public MessageNewsItemDetailVo(String title, String description, String picUrl, String url) {
        Title = title;
        Description = description;
        PicUrl = picUrl;
        Url = url;
    }

    @ApiModelProperty(value = "图文消息标题")
    private String Title;

    @ApiModelProperty(value = "图文消息描述")
    private String Description;

    @ApiModelProperty(value = "图片链接，支持JPG、PNG格式，较好的效果为大图360*200，小图200*200")
    private String PicUrl;

    @ApiModelProperty(value = "点击图文消息跳转链接")
    private String Url;
}
