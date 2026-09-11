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

/**
 * 支付宝订单表
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
@TableName("eb_ali_pay_info")
@ApiModel(value="AliPayInfo对象", description="支付宝订单表")
public class AliPayInfo implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "支付宝分配给开发者的应用ID")
    private String appId;

    @ApiModelProperty(value = "发送请求的时间，格式'yyyy-MM-dd HH:mm:ss'")
    private String timestamp;

    @ApiModelProperty(value = "订单描述")
    private String body;

    @ApiModelProperty(value = "订单标题")
    private String subject;

    @ApiModelProperty(value = "商户订单号")
    private String outTradeNo;

    @ApiModelProperty(value = "订单相对超时时间")
    private String timeoutExpress;

    @ApiModelProperty(value = "订单绝对超时时间")
    private String timeExpire;

    @ApiModelProperty(value = "订单总金额。单位为元，精确到小数点后两位")
    private String totalAmount;

    @ApiModelProperty(value = "公用回传参数,本参数必须进行UrlEncode之后才可以发送给支付宝")
    private String passbackParams;

    @ApiModelProperty(value = "网关返回码")
    private String code;

    @ApiModelProperty(value = "网关返回码描述")
    private String msg;

    @ApiModelProperty(value = "业务返回码")
    private String subCode;

    @ApiModelProperty(value = "业务返回码描述")
    private String subMsg;

    @ApiModelProperty(value = "签名")
    private String sign;

    @ApiModelProperty(value = "该交易在支付宝系统中的交易流水号")
    private String tradeNo;

    @ApiModelProperty(value = "收款支付宝账号对应的支付宝唯一用户号")
    private String sellerId;

    @ApiModelProperty(value = "支付宝服务器主动通知商户服务器里指定的页面http/https路径")
    private String notifyUrl;


}
