package com.zbkj.crmeb.sms.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 是否发送短信配置
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
