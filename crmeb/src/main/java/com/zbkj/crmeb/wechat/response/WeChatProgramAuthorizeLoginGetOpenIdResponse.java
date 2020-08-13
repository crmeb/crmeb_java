package com.zbkj.crmeb.wechat.response;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户地址表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WeChatProgramAuthorizeLoginGetOpenIdResponse对象", description="微信小程序用户授权返回数据")
public class WeChatProgramAuthorizeLoginGetOpenIdResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "token")
    @TableField(value = "session_key")
    private String sessionKey;

    @ApiModelProperty(value = "过期时间")
    @TableField(value = "openid")
    private String openId;

    @ApiModelProperty(value = "unionid")
    @TableField(value = "unionid")
    private String unionId;

    @ApiModelProperty(value = "错误码")
    @TableField(value = "errcode")
    private String errCode;

    @ApiModelProperty(value = "错误信息")
    @TableField(value = "errmsg")
    private String errMsg;
}
