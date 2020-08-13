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
@ApiModel(value="WeChatAuthorizeLoginUserInfoResponse对象", description="获取微信用户信息")
public class WeChatAuthorizeLoginUserInfoResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户的唯一标识")
    @TableField(value = "openId")
    private String openId;

    @ApiModelProperty(value = "用户昵称")
    @TableField(value = "nickname")
    private String nickName;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "用户个人资料填写的省份")
    private String province;

    @ApiModelProperty(value = "普通用户个人资料填写的城市")
    private String city;

    @ApiModelProperty(value = "国家，如中国为CN")
    private String country;

    @ApiModelProperty(value = "用户头像")
    @TableField(value = "headimgurl")
    private String avatar;

    @ApiModelProperty(value = "用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）")
    private String privilege;

    @ApiModelProperty(value = "只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。")
    @TableField(value = "unionid")
    private String unionId;

}
