package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.net.Inet4Address;

/**
 * 创建订单参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderCreateRequest对象", description="创建订单参数")
public class OrderCreateRequest {

    @ApiModelProperty(value = "真实名称")
//    @NotNull(message = "商品id不能为空")
    private String realName;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "收货地址id")
    private Integer addressId;

    @ApiModelProperty(value = "")
    private Integer formId;

    @ApiModelProperty(value = "")
    private Integer couponId;

    @ApiModelProperty(value = "")
    private String payType;

    @ApiModelProperty(value = "")
    private Boolean useIntegral;

    @ApiModelProperty(value = "")
    private Integer bargainId;

    @ApiModelProperty(value = "")
    private Integer combinationId;

    @ApiModelProperty(value = "")
    private Integer pinkId;

    @ApiModelProperty(value = "")
    private Integer seckillId;

    @ApiModelProperty(value = "")
    private String mark;

    @ApiModelProperty(value = "")
    private Integer storeId;

    @ApiModelProperty(value = "")
    private String from;

    @ApiModelProperty(value = "快递类型")
    private Integer shippingType;

    @ApiModelProperty(value = "是否为立即购买", required = true)
    @NotNull(message = "是否为立即购买不能为空")
    private Boolean isNew;
}
