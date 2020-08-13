package com.zbkj.crmeb.store.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author stivepeim
 * @title: StoreOrderStatusRequest
 * @projectName crmeb
 * @description: TODO
 * @date 2020/5/2816:12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_order_status")
@ApiModel(value="StoreOrderStatus对象", description="订单操作记录表")
public class StoreOrderStatusRequest {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单id")
    private Integer oid;

    @ApiModelProperty(value = "操作类型")
    private String changeType;

    @ApiModelProperty(value = "操作备注")
    private String changeMessage;

    @ApiModelProperty(value = "操作时间")
    private Integer changeTime;
}
