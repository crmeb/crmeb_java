package com.zbkj.crmeb.wechatMessage.vo;

import com.constants.WeChatConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

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
@ApiModel(value="MessageNewsVo对象", description="微信消息 图片/语音 模板")
public class MessageNewsVo extends BaseMessageVo {
    public MessageNewsVo() {}
    public MessageNewsVo(String toUserName, String fromUserName, int articleCount, List<MessageNewsItemVo> messageNewsItemVoList) {
        super();
        ToUserName = toUserName;
        FromUserName = fromUserName;
        ArticleCount = articleCount;
        Articles = messageNewsItemVoList;
        MsgType = WeChatConstants.WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_NEWS;
    }

    @ApiModelProperty(value = "图文消息个数；当用户发送文本、图片、语音、视频、图文、地理位置这六种消息时，开发者只能回复1条图文消息；其余场景最多可回复8条图文消息")
    private int ArticleCount = 1;

    @ApiModelProperty(value = "图文消息信息，注意，如果图文数超过限制，则将只发限制内的条数")
    private List<MessageNewsItemVo> Articles;
}
