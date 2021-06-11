package com.zbkj.crmeb.store.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 订单状态数量
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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreOrderCountItemResponse对象", description="订单状态数量")
public class StoreOrderCountItemResponse implements Serializable {

    private static final long serialVersionUID = -8605913636959651047L;

    @ApiModelProperty(value = "总数")
    private Integer all;

    @ApiModelProperty(value = "未支付")
    private Integer unPaid;

    @ApiModelProperty(value = "未发货")
    private Integer notShipped;

    @ApiModelProperty(value = "待收货")
    private Integer spike;

    @ApiModelProperty(value = "待评价")
    private Integer bargain;

    @ApiModelProperty(value = "交易完成")
    private Integer complete;

    @ApiModelProperty(value = "待核销")
    private Integer toBeWrittenOff;

    @ApiModelProperty(value = "退款中")
    private Integer refunding;

    @ApiModelProperty(value = "已退款")
    private Integer refunded;

    @ApiModelProperty(value = "0 未退款 1 申请中 2 已退款")
    private Integer refundStatus;

    @ApiModelProperty(value = "已删除")
    private Integer deleted;
}
