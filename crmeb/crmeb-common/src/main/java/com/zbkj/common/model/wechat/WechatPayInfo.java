package com.zbkj.common.model.wechat;

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
 * 微信订单表
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_wechat_pay_info")
@ApiModel(value="WechatPayInfo对象", description="微信订单表")
public class WechatPayInfo implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "公众号唯一标识")
    private String appId;

    @ApiModelProperty(value = "商户号")
    private String mchId;

    @ApiModelProperty(value = "设备号,PC网页或公众号内支付可以传-WEB")
    private String deviceInfo;

    @ApiModelProperty(value = "用户的唯一标识")
    private String openId;

    @ApiModelProperty(value = "随机字符串")
    private String nonceStr;

    @ApiModelProperty(value = "签名")
    private String sign;

    @ApiModelProperty(value = "签名类型，默认为MD5，支持HMAC-SHA256和MD5")
    private String signType;

    @ApiModelProperty(value = "商品描述")
    private String body;

    @ApiModelProperty(value = "商品详细描述，对于使用单品优惠的商户，该字段必须按照规范上传")
    private String detail;

    @ApiModelProperty(value = "附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用")
    private String attach;

    @ApiModelProperty(value = "商户订单号,要求32个字符内")
    private String outTradeNo;

    @ApiModelProperty(value = "标价币种：CNY：人民币 GBP：英镑 HKD：港币 USD：美元 JPY：日元 CAD：加拿大元 AUD：澳大利亚元 EUR：欧元 NZD：新西兰元 KRW：韩元 THB：泰铢")
    private String feeType;

    @ApiModelProperty(value = "标价金额")
    private Integer totalFee;

    @ApiModelProperty(value = "终端IP")
    private String spbillCreateIp;

    @ApiModelProperty(value = "交易起始时间")
    private String timeStart;

    @ApiModelProperty(value = "交易结束时间")
    private String timeExpire;

    @ApiModelProperty(value = "通知地址")
    private String notifyUrl;

    @ApiModelProperty(value = "交易类型,取值为：JSAPI，NATIVE，APP等")
    private String tradeType;

    @ApiModelProperty(value = "商品ID")
    private String productId;

    @ApiModelProperty(value = "场景信息")
    private String sceneInfo;

    @ApiModelProperty(value = "错误代码")
    private String errCode;

    @ApiModelProperty(value = "预支付交易会话标识")
    private String prepayId;

    @ApiModelProperty(value = "二维码链接")
    private String codeUrl;

    @ApiModelProperty(value = "是否关注公众账号")
    private String isSubscribe;

    @ApiModelProperty(value = "交易状态")
    private String tradeState;

    @ApiModelProperty(value = "付款银行")
    private String bankType;

    @ApiModelProperty(value = "现金支付金额")
    private Integer cashFee;

    @ApiModelProperty(value = "代金券金额")
    private Integer couponFee;

    @ApiModelProperty(value = "微信支付订单号")
    private String transactionId;

    @ApiModelProperty(value = "支付完成时间")
    private String timeEnd;

    @ApiModelProperty(value = "交易状态描述")
    private String tradeStateDesc;


}
