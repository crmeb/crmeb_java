package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 用户资金统计
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-06-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserBalanceResponse对象", description="用户资金统计")
public class UserBalanceResponse implements Serializable {
    public UserBalanceResponse(){}
    public UserBalanceResponse(BigDecimal nowMoney, BigDecimal recharge, BigDecimal orderStatusSum) {
        this.nowMoney = nowMoney;
        this.recharge = recharge;
        this.orderStatusSum = orderStatusSum;
    }

    private static final long serialVersionUID=1L;


    @ApiModelProperty(value = "当前总资金")
    private BigDecimal nowMoney;

    @ApiModelProperty(value = "累计充值")
    private BigDecimal recharge;

    @ApiModelProperty(value = "累计消费")
    private BigDecimal orderStatusSum;

}
