package com.zbkj.common.token;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 微信用户授权返回数据
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
@ApiModel(value="WeChatAuthorizeLoginGetOpenIdResponse对象", description="微信开放平台获取accessToken对象")
public class WeChatOauthToken implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "accessToken接口调用凭证")
    @TableField(value = "access_token")
    private String accessToken;

    @ApiModelProperty(value = "access_token 接口调用凭证超时时间，单位（秒）")
    @TableField(value = "expires_in")
    private String expiresIn;

    @ApiModelProperty(value = "用户刷新access_token")
    @TableField(value = "refresh_token")
    private String refreshToken;

    @ApiModelProperty(value = "用户OpenId")
    @TableField(value = "openid")
    private String openId;

    @ApiModelProperty(value = "用户授权的作用域，使用逗号（,）分隔")
    private String scope;

}
