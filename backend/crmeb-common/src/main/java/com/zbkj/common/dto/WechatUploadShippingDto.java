package com.zbkj.common.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 微信发货信息录入Dto对象
 *
 * @author Hzw
 * @version 1.0.0
 * @Date 2023/11/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WechatUploadShippingDto", description="微信发货信息录入Dto对象")
public class WechatUploadShippingDto implements Serializable {

    private static final long serialVersionUID = -8804881346870986621L;

    @ApiModelProperty(value = "合单订单，需要上传购物详情的合单订单，根据订单类型二选一", required = true)
    @JsonProperty(value = "order_key")
    @JSONField(name = "order_key")
    private WechatOrderKeyDto orderKey;

    @ApiModelProperty(value = "物流模式，发货方式", required = true)
    // 枚举值：1、实体物流配送采用快递公司进行实体物流配送形式 2、同城配送 3、虚拟商品，虚拟商品，例如话费充值，点卡等，无实体配送形式 4、用户自提
    @JsonProperty(value = "logistics_type")
    @JSONField(name = "logistics_type")
    private Integer logisticsType;

    @ApiModelProperty(value = "发货模式，发货模式", required = true)
    // 枚举值：1、UNIFIED_DELIVERY（统一发货）2、SPLIT_DELIVERY（分拆发货） 示例值: UNIFIED_DELIVERY
    @JsonProperty(value = "delivery_mode")
    @JSONField(name = "delivery_mode")
    private Integer deliveryMode;

    @ApiModelProperty("分拆发货模式时必填，用于标识分拆发货模式下是否已全部发货完成，只有全部发货完成的情况下才会向用户推送发货完成通知。示例值: true/false")
    @JsonProperty(value = "is_all_delivered")
    @JSONField(name = "is_all_delivered")
    private Boolean isAllDelivered;

    @ApiModelProperty(value = "物流信息列表，发货物流单列表，支持统一发货（单个物流单）和分拆发货（多个物流单）两种模式，多重性: [1, 10]", required = true)
    @JsonProperty(value = "shipping_list")
    @JSONField(name = "shipping_list")
    private List<WechatUploadShippingInfoDto> shippingList;

    @ApiModelProperty(value = "上传时间，用于标识请求的先后顺序 示例值: 2021-05-20T13:29:35.120+08:00", required = true)
    @JsonProperty(value = "upload_time")
    @JSONField(name = "upload_time")
    private String uploadTime;

    @ApiModelProperty(value = "支付者，支付者信息", required = true)
    private WechatOrderPayerDto payer;

}
