package com.zbkj.crmeb.wechatMessage.vo;

import com.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 微信消息基础模板
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
@ApiModel(value="BaseMessageVo对象", description="微信消息基础模板")
public class BaseMessageVo{
    @ApiModelProperty(value = "开发者微信号")
    protected String ToUserName;

    @ApiModelProperty(value = "发送方帐号（一个OpenID）")
    protected String FromUserName;

    @ApiModelProperty(value = "消息创建时间 （整型）")
    protected Long CreateTime = DateUtil.getTime();

    @ApiModelProperty(value = "消息类型，文本为text")
    protected String MsgType = "text";
}
