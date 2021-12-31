package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @program: crmeb
 * @author: 大粽子
 * @create: 2021-10-27 10:38
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderExcelVo", description = "产品导出")
public class OrderExcelVo implements Serializable {

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "实际支付金额")
    private String payPrice;

//    @ApiModelProperty(value = "支付方式")
//    private String payType;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

//    @ApiModelProperty(value = "订单状态（0：待发货；1：待收货；2：已收货，待评价；3：已完成；）")
//    private String status;

    @ApiModelProperty(value = "商品信息")
    private String productName;

    @ApiModelProperty(value = "订单状态")
    private String statusStr;

    @ApiModelProperty(value = "支付方式")
    private String payTypeStr;

//    @ApiModelProperty(value = "是否删除")
//    private String isDel;
//
//    @ApiModelProperty(value = "退款图片")
//    private String refundReasonWapImg;
//
//    @ApiModelProperty(value = "退款用户说明")
//    private String refundReasonWapExplain;
//
//    @ApiModelProperty(value = "退款时间")
//    private String refundReasonTime;
//
//    @ApiModelProperty(value = "前台退款原因")
//    private String refundReasonWap;
//
//    @ApiModelProperty(value = "不退款的理由")
//    private String refundReason;
//
//    @ApiModelProperty(value = "退款金额")
//    private String refundPrice;
//
//    @ApiModelProperty(value = "0 未退款 1 申请中 2 已退款")
//    private String refundStatus;
//
//    @ApiModelProperty(value = "核销码")
//    private String verifyCode;

    @ApiModelProperty(value = "订单类型")
    private String orderType;

//    @ApiModelProperty(value = "订单管理员备注")
//    private String remark;

    @ApiModelProperty(value = "用户姓名")
    private String realName;

//    @ApiModelProperty(value = "支付状态")
//    private String paid;
//
//    @ApiModelProperty(value = "订单类型:0-普通订单，1-视频号订单")
//    private String type;
//
//    @ApiModelProperty(value = "是否改价,0-否，1-是")
//    private String isAlterPrice;
}
