package com.zbkj.common.request;

import com.zbkj.common.request.onepass.OnePassShipmentCreateOrderRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 订单发货对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreOrderSendRequest对象", description="订单发货对象")
public class StoreOrderSendRequest {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单id")
    private Integer id;

    @ApiModelProperty(value = "订单编号")
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "类型， express 发货，send 送货，fictitious虚拟", allowableValues = "express,send,fictitious", required = true)
    @NotBlank(message = "请选择类型")
    private String deliveryType;

    @ApiModelProperty(value = "快递公司名,发货类型必传")
    private String expressName;

    @ApiModelProperty(value = "快递公司编码,发货类型必传")
    private String expressCode;

    @ApiModelProperty(value = "快递单号,发货类型必传")
    private String expressNumber;

    @ApiModelProperty(value = "发货记录类型，1快递发货、2电子面单, 3一号通-商家发货")
    private String expressRecordType;

    @ApiModelProperty(value = "电子面单模板,电子面单必传")
    private String expressTempId;

    @ApiModelProperty(value = "寄件人姓名,电子面单必传")
    private String toName;

    @ApiModelProperty(value = "寄件人电话,电子面单必传")
    private String toTel;

    @ApiModelProperty(value = "寄件人地址,电子面单必传")
    private String toAddr;

    @ApiModelProperty(value = "送货人姓名,送货类型必传")
    private String deliveryName;

    @ApiModelProperty(value = "送货人电话,送货类型必传")
    private String deliveryTel;

    @ApiModelProperty(value = "商家发货一号通内置功能")
    private OnePassShipmentCreateOrderRequest shipment;
}
