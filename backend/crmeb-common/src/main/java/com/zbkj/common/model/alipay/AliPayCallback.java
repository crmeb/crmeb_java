package com.zbkj.common.model.alipay;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付宝回调表
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_ali_pay_callback")
@ApiModel(value="AliPayCallback对象", description="支付宝回调表")
public class AliPayCallback implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "通知的类型")
    private String notifyType;

    @ApiModelProperty(value = "通知校验 ID")
    private String notifyId;

    @ApiModelProperty(value = "支付宝分配给开发者的应用 ID")
    private String appId;

    @ApiModelProperty(value = "编码格式，如 utf-8、gbk、gb2312 等")
    private String charset;

    @ApiModelProperty(value = "调用的接口版本，固定为：1.0")
    private String version;

    @ApiModelProperty(value = "商户生成签名字符串所使用的签名算法类型，目前支持 RSA2 和 RSA，推荐使用 RSA2")
    private String signType;

    @ApiModelProperty(value = "签名")
    private String sign;

    @ApiModelProperty(value = "支付宝交易凭证号")
    private String tradeNo;

    @ApiModelProperty(value = "原支付请求的商户订单号")
    private String outTradeNo;

    @ApiModelProperty(value = "交易目前所处的状态:WAIT_BUYER_PAY-交易创建，等待买家付款,TRADE_CLOSED-未付款交易超时关闭，或支付完成后全额退款,3-交易支付成功,4-交易结束，不可退款")
    private String tradeStatus;

    @ApiModelProperty(value = "本次交易支付的订单金额，单位为人民币（元）")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "商家在交易中实际收到的款项，单位为人民币（元）")
    private BigDecimal receiptAmount;

    @ApiModelProperty(value = "退款通知中，返回总退款金额，单位为人民币（元），支持两位小数")
    private BigDecimal refundFee;

    @ApiModelProperty(value = "商品的标题/交易标题/订单标题/订单关键字等，是请求时对应的参数，原样通知回来")
    private String subject;

    @ApiModelProperty(value = "该订单的备注、描述、明细等。对应请求时的 body 参数，原样通知回来")
    private String body;

    @ApiModelProperty(value = "公共回传参数，如果请求时传递了该参数，则返回给商户时会在异步通知时将该参数原样返回。本参数必须进行UrlEncode之后才可以发送给支付宝。")
    private String passbackParams;

    @ApiModelProperty(value = "通知的发送时间。格式为 yyyy-MM-dd HH:mm:ss")
    private Date notifyTime;

    @ApiModelProperty(value = "创建时间")
    private Date addTime;


}
