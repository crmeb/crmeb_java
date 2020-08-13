package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author stivepeim
 * @title: OrderRefundVerifyRequest
 * @projectName crmeb
 * @description: 退款前验证
 * @date 2020/7/2210:27
 */
@Data
public class OrderRefundVerifyRequest {
    @ApiModelProperty(value = "退款备注说明")
    private String  refund_reason_wap_explain;

    @ApiModelProperty(value = "退款凭证图片")
    private String  refund_reason_wap_img;

    @ApiModelProperty(value = "退款原因")
    @NotNull(message = "退款原因 不能为空")
    private String  text;

    @ApiModelProperty(value = "待退款订单")
    @NotNull(message = "待退款订单 不能为空")
    private String  uni;
}
