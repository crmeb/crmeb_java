package com.zbkj.crmeb.payment.vo.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 微信回调对象
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
@ApiModel(value="CallbackVo对象", description="微信回调")
public class CallbackVo {
    @ApiModelProperty(value = "调用接口提交的公众账号ID")
    @JsonProperty(value = "appid")
    private String appId;

    @ApiModelProperty(value = "调用接口提交的商户号")
    @JsonProperty(value = "mch_id")
    private String mchId;

    @ApiModelProperty(value = "调用接口提交的终端设备号")
    @JsonProperty(value = "device_info")
    private String deviceInfo;

    @ApiModelProperty(value = "微信返回的随机字符串")
    @JsonProperty(value = "nonce_str")
    private String nonceStr;

    @ApiModelProperty(value = "微信返回的签名")
    private String sign;

    @ApiModelProperty(value = "SUCCESS/FAIL此字段是通信标识，非交易标识，交易是否成功需要查看trade_state来判断")
    @JsonProperty(value = "return_code")
    private String returnCode;

    @ApiModelProperty(value = "当return_code为FAIL时返回信息为错误原因 ，例如 签名失败 参数格式校验错误")
    @JsonProperty(value = "return_msg")
    private String returnMsg;

    @ApiModelProperty(value = "SUCCESS/FAIL 业务结果")
    @JsonProperty(value = "result_code")
    private String resultCode;

    @ApiModelProperty(value = "详细参见错误列表")
    @JsonProperty(value = "err_code")
    private String errCode;

    @ApiModelProperty(value = "错误返回的信息描述")
    @JsonProperty(value = "err_code_des")
    private String errCodeDes;

    @ApiModelProperty(value = "用户在商户appid下的唯一标识")
    private String openid;

    @ApiModelProperty(value = "微信支付订单号")
    @JsonProperty(value = "transaction_id")
    private String transactionId;

    @ApiModelProperty(value = "微信支付订单号")
    @JsonProperty(value = "out_trade_no")
    private String outTradeNo;

    @ApiModelProperty(value = "商家数据包，原样返回")
    private String attach;

    @ApiModelProperty(value = "支付完成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则")
    @JsonProperty(value = "time_end")
    private String timeEnd;
}
