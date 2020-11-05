package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 退款前验证
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
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
