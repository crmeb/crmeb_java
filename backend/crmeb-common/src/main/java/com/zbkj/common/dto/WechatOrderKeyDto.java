package com.zbkj.common.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 微信合订单KeyDto对象
 *
 * @author Hzw
 * @version 1.0.0
 * @Date 2023/11/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WechatOrderKeyDto", description="微信合订单KeyDto对象")
public class WechatOrderKeyDto implements Serializable {

    private static final long serialVersionUID = -8804881346870986621L;

    @ApiModelProperty(value = "订单单号类型，用于确认需要上传详情的订单", required = true)
    // 枚举值1，使用下单商户号和商户侧单号；枚举值2，使用微信支付单号。
    @JsonProperty(value = "order_number_type")
    @JSONField(name = "order_number_type")
    private Integer orderNumberType;

    @ApiModelProperty("原支付交易对应的微信订单号")
    @JsonProperty(value = "transaction_id")
    @JSONField(name = "transaction_id")
    private String transactionId;

    @ApiModelProperty("支付下单商户的商户号，由微信支付生成并下发")
    private String mchid;

    @ApiModelProperty("商户系统内部订单号，只能是数字、大小写字母_-*且在同一个商户号下唯一")
    @JsonProperty(value = "out_trade_no")
    @JSONField(name = "out_trade_no")
    private String outTradeNo;

}
