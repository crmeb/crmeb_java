package com.zbkj.crmeb.store.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 订单九宫格数据
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreOrderTopItemResponse对象", description="订单九宫格数据")
public class StoreOrderTopItemResponse implements Serializable {
    public StoreOrderTopItemResponse() {}
    public StoreOrderTopItemResponse(Integer count, BigDecimal amount, BigDecimal weChatAmount, BigDecimal yueAmount) {
        this.count = count;
        this.amount = amount;
        this.weChatAmount = weChatAmount;
        this.yueAmount = yueAmount;
    }

    @ApiModelProperty(value = "订单数量")
    private Integer count;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "微信支付金额")
    private BigDecimal weChatAmount;

    @ApiModelProperty(value = "余额支付")
    private BigDecimal yueAmount;
}
