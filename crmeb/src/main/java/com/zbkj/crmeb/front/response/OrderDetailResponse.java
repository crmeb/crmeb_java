package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单列表响应对象
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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderDetailResponse对象", description="订单列表响应对象")
public class OrderDetailResponse implements Serializable {

    private static final long serialVersionUID = 1387727608277207652L;

    private StoreOrder storeOrder;
    private List<StoreOrderInfoOldVo> cartInfo;
    private String statusPic;
    private Integer offlinePayStatus;


    @ApiModelProperty(value = "订单id")
    private Integer id;

    @ApiModelProperty(value = "订单编号")
    private String orderId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "支付状态")
    private Boolean paid;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "订单状态（0：待发货；1：待收货；2：已收货，待评价；3：已完成；）")
    private Integer status;

    @ApiModelProperty(value = "订单状态")
    private String orderStatus;

    @ApiModelProperty(value = "订单商品总数")
    private Integer totalNum;

    @ApiModelProperty(value = "支付邮费")
    private BigDecimal payPostage;

    @ApiModelProperty(value = "0 未退款 1 申请中 2 已退款 3 退款中")
    private Integer refundStatus;

    @ApiModelProperty(value = "快递名称/送货人姓名")
    private String deliveryName;

    @ApiModelProperty(value = "发货类型")
    private String deliveryType;

    @ApiModelProperty(value = "快递单号/手机号")
    private String deliveryId;

    @ApiModelProperty(value = "拼团id 0没有拼团")
    private Integer pinkId;

    @ApiModelProperty(value = "砍价id")
    private Integer bargainId;

    @ApiModelProperty(value = "核销码")
    private String verifyCode;

    @ApiModelProperty(value = "门店id")
    private Integer storeId;

    @ApiModelProperty(value = "配送方式 1=快递 ，2=门店自提")
    private Integer shippingType;

    @ApiModelProperty(value = "活动类型")
    private String activityType;

    @ApiModelProperty(value = "订单详情对象列表")
    private List<OrderInfoResponse> orderInfoList;

    @ApiModelProperty(value = "订单类型:0-普通订单，1-视频号订单")
    private Integer type;
}
