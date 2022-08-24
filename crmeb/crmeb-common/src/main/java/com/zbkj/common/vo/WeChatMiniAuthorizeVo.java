package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 微信小程序用户授权返回数据
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WeChatMiniAuthorizeVo对象", description="微信小程序用户授权返回数据")
public class WeChatMiniAuthorizeVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "会话密钥")
    @TableField(value = "session_key")
    private String sessionKey;

    @ApiModelProperty(value = "用户唯一标识")
    @TableField(value = "openid")
    private String openId;

    @ApiModelProperty(value = "用户在开放平台的唯一标识符，若当前小程序已绑定到微信开放平台帐号下会返回")
    @TableField(value = "unionid")
    private String unionId;

    @ApiModelProperty(value = "错误码")
    @TableField(value = "errcode")
    private String errCode;

    @ApiModelProperty(value = "错误信息")
    @TableField(value = "errmsg")
    private String errMsg;
}
