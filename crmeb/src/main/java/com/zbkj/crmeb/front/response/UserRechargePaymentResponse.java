package com.zbkj.crmeb.front.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 充值返回对象
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
@ApiModel(value="UserRechargePaymentResponse对象", description="充值返回对象")
public class UserRechargePaymentResponse {
    @ApiModelProperty(value = "调用接口提交的公众账号ID")
    private String appId;

    @ApiModelProperty(value = "当前的时间，其他详见时间戳规则")
    private Integer timeStamp = DateUtil.getNowTime();

    @ApiModelProperty(value = "随机字符串")
    private String nonceStr;

    @ApiModelProperty(value = "订单详情扩展字符串")
    @JsonProperty(value = "package")
    private String extra;

    @ApiModelProperty(value = "签名方式")
    private String signType = "MD5";

    @ApiModelProperty(value = "签名")
    private String paySign;

    @ApiModelProperty(value = "H5支付的跳转url")
    private String h5PayUrl;

    @ApiModelProperty(value = "微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时,针对H5支付此参数无特殊用途")
    private String prepayId;
}
