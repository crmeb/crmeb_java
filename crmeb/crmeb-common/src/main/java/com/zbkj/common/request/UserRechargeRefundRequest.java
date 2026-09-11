package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户充值退款请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserRechargeRefundRequest对象", description = "用户充值退款")
public class UserRechargeRefundRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "充值记录ID", required = true)
    @NotNull(message = "充值记录ID不能为空")
    @Min(value = 1, message = "充值记录ID必须大于0")
    private Integer id;

    @ApiModelProperty(value = "充值订单号", required = true)
    @NotBlank(message = "充值订单号不能为空")
    private String orderId;

    @ApiModelProperty(value = "退款类型：1-仅本金，2-本金和赠送金额", required = true)
    @NotNull(message = "退款类型不能为空")
    @Range(min = 1, max = 2, message = "退款类型不正确")
    private Integer type;
}
