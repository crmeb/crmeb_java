package com.zbkj.crmeb.sms.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Classname SmsConfigRequest
 * @Description 是否发送短信配置
 * @Date 2020/6/17 9:42 上午
 * @Created by stivepeim
 */
@Data
public class SmsConfigRequest {
    // 支付成功短信提醒
    @ApiModelProperty(value = "支付成功短信提醒", required = true)
    @NotNull(message = "支付成功短信提醒不能为空")
    private String lowerOrderSwitch;

    // 发货短信提醒
    @ApiModelProperty(value = "发货短信提醒", required = true)
    @NotNull(message = "发货短信提醒不能为空")
    private String deliverGoodsSwitch;

    // 确认收货短信提醒
    @ApiModelProperty(value = "确认收货短信提醒", required = true)
    @NotNull(message = "确认收货短信提醒不能为空")
    private String confirmTakeOverSwitch;

    // 用户下单管理员短信提醒
    @ApiModelProperty(value = "用户下单管理员短信提醒", required = true)
    @NotNull(message = "用户下单管理员短信提醒不能为空")
    private String adminLowerOrderSwitch;

    // 支付成功管理员短信提醒
    @ApiModelProperty(value = "支付成功管理员短信提醒", required = true)
    @NotNull(message = "支付成功管理员短信提醒不能为空")
    private String adminPaySuccessSwitch;

    // 用户确认收货管理员短信提醒
    @ApiModelProperty(value = "用户确认收货管理员短信提醒", required = true)
    @NotNull(message = "用户确认收货管理员短信提醒不能为空")
    private String adminRefundSwitch;

    // 用户发起退款管理员短信提醒
    @ApiModelProperty(value = "用户发起退款管理员短信提醒", required = true)
    @NotNull(message = "用户发起退款管理员短信提醒不能为空")
    private String adminConfirmTakeOverSwitch;

    // 改价短信提醒
    @ApiModelProperty(value = "改价短信提醒", required = true)
    @NotNull(message = "改价短信提醒不能为空")
    private String priceRevisionSwitch;
}
