package com.zbkj.crmeb.store.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.system.model.SystemStore;
import com.zbkj.crmeb.user.model.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单详情响应对象
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
@ApiModel(value="StoreOrderInfoResponse对象", description="订单详情响应对象")
public class StoreOrderInfoResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单ID")
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "用户姓名")
    private String realName;

    @ApiModelProperty(value = "用户电话")
    private String userPhone;

    @ApiModelProperty(value = "详细地址")
    private String userAddress;

    @ApiModelProperty(value = "订单商品总数")
    private Integer totalNum;

    @ApiModelProperty(value = "订单总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "支付邮费")
    private BigDecimal payPostage;

    @ApiModelProperty(value = "优惠券金额")
    private BigDecimal couponPrice;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "订单状态（-1 : 申请退款 -2 : 退货成功 0：待发货；1：待收货；2：已收货；3：待评价；-1：已退款）")
    private Integer status;

    @ApiModelProperty(value = "0 未退款 1 申请中 2 已退款")
    private Integer refundStatus;

    @ApiModelProperty(value = "快递名称/送货人姓名")
    private String deliveryName;

    @ApiModelProperty(value = "发货类型")
    private String deliveryType;

    @ApiModelProperty(value = "快递单号/手机号")
    private String deliveryId;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "管理员备注")
    private String remark;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "使用积分")
    private Integer useIntegral;

    @ApiModelProperty(value = "给用户退了多少积分")
    private Integer backIntegral;

    @ApiModelProperty(value = "核销码")
    private String verifyCode;

    @ApiModelProperty(value = "配送方式 1=快递 ，2=门店自提")
    private Integer shippingType;

    @ApiModelProperty(value = "订单状态")
    private Map<String, String> statusStr;

    @ApiModelProperty(value = "支付方式")
    private String payTypeStr;

    @ApiModelProperty(value = "用户昵称")
    private String nikeName;

    @ApiModelProperty(value = "用户电话")
    private String phone;

    @ApiModelProperty(value = "订单商品详情")
    List<StoreOrderInfoOldVo> orderInfo;

    @ApiModelProperty(value = "提货点")
    private SystemStore systemStore;

    @ApiModelProperty(value = "推广人名称")
    private String spreadName;

    @ApiModelProperty(value = "商品总价")
    private BigDecimal proTotalPrice;
}
