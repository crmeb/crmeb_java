package com.zbkj.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 微信订单支付者Dto对象
 *
 * @author Hzw
 * @version 1.0.0
 * @Date 2023/11/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WechatOrderPayerDto", description="微信订单支付者Dto对象")
public class WechatOrderPayerDto implements Serializable {

    private static final long serialVersionUID = -8804881346870986621L;

    @ApiModelProperty(value = "用户标识，用户在商户appid下的唯一标识", required = true)
    // 下单前需获取到用户的Openid 示例值: oUpF8uMuAJO_M2pxb1Q9zNjWeS6o 字符字节限制: [1, 128]
    private String openid;

}
