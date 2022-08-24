package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 小程序accessTokenVo对象
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
@ApiModel(value="WeChatAccessTokenVo", description="微信accessTokenVo对象")
public class WeChatAccessTokenVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "获取到的凭证")
    @TableField(value = "access_token")
    private String accessToken;

    @ApiModelProperty(value = "凭证有效时间，单位：秒。目前是7200秒之内的值。")
    @TableField(value = "expires_in")
    private Integer expiresIn;

    @ApiModelProperty(value = "错误码")
    @TableField(value = "errcode")
    private Integer errCode;

    @ApiModelProperty(value = "错误信息")
    @TableField(value = "errmsg")
    private String errMsg;

    /**
     * errcode 的合法值
     * -1	    系统繁忙，此时请开发者稍候再试
     * 0	    请求成功
     * 40001	AppSecret 错误或者 AppSecret 不属于这个小程序，请开发者确认 AppSecret 的正确性
     * 40002	请确保 grant_type 字段值为 client_credential
     * 40013	不合法的 AppID，请开发者检查 AppID 的正确性，避免异常字符，注意大小写
     */
}
