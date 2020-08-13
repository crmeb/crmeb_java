package com.zbkj.crmeb.store.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author stivepeim
 * @title: StoreOrderInfoRequest
 * @projectName crmeb
 * @description: TODO
 * @date 2020/5/2816:09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreOrderInfoRequest对象", description="订单购物详情表")
public class StoreOrderInfoRequest {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单id")
    private Integer orderId;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "购买东西的详细信息")
    private String info;

    @ApiModelProperty(value = "唯一id")
    private String unique;
}
