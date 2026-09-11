package com.zbkj.common.request.onepass;

import com.zbkj.common.constants.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Auther: 大粽子
 * @Date: 2023/8/26 15:09
 * @Description: 一号通 创建商家寄件
 */
@Data
public class OnePassShipmentCreateOrderRequest implements Serializable {

    // 字符串	是	--	快递公司编码，在[获取商家寄件快递公司]接口中获得
    @ApiModelProperty(value = "(必填) 快递公司编码，在[获取商家寄件快递公司]接口中获得")
//    @NotNull(message = "快递公司编码 不能为空")
    private String kuaidicom;

    // 字符串	是	--	收件人姓名
    @ApiModelProperty(value = "(必填) 收件人姓名")
//    @NotNull(message = "收件人姓名 不能为空")
    private String manName;

    // 字符串	是	--	收件人手机号
    @ApiModelProperty(value = "(必填) 收件人手机号")
//    @NotNull(message = "收件人手机号 不能为空")
    private String phone;

    // 字符串	是	--	收件人所在完整地址
    @ApiModelProperty(value = "(必填) 收件人所在完整地址")
//    @NotNull(message = "收件人所在完整地址 不能为空")
    private String address;

    // 字符串	是	--	寄件人姓名
    @ApiModelProperty(value = "(必填) 寄件人姓名")
//    @NotNull(message = "寄件人姓名 不能为空")
    private String sendRealName;

    // 字符串	是	--	寄件人手机号
    @ApiModelProperty(value = "(必填) 寄件人手机号")
//    @NotNull(message = "寄件人手机号 不能为空")
    private String sendPhone;

    // 字符串	是	--	寄件人所在完整地址
    @ApiModelProperty(value = "(必填) 寄件人所在完整地址")
//    @NotNull(message = "寄件人所在完整地址 不能为空")
    private String sendAddress;

    // 字符串	否	--	订单信息回调地址
    @ApiModelProperty(value = "订单信息回调地址")
    private String callBackUrl;

    // 字符串	否	--	物品名称,例：文件。当kuaidicom=jd、yuantong时必填
    @ApiModelProperty(value = "(条件必填) 物品名称,例：文件。当kuaidicom=jd、yuantong时必填")
    private String cargo;

    // 字符串	否	--	业务类型，默认为标准快递，各快递公司业务类型对照参考
    @ApiModelProperty(value = "(必填) 业务类型，默认为标准快递，各快递公司业务类型对照参考")
    private String serviceType;

    //字符串	否	--	支付方式，SHIPPER: 寄付（默认）。不支持到付
    @ApiModelProperty(value = "支付方式，SHIPPER: 寄付（默认）。不支持到付")
    private String payment;

    // 字符串	否	--	物品总重量KG，不需带单位，例：1.5
    @ApiModelProperty(value = "物品总重量KG，不需带单位，例：1.5")
    private String weight;

    // 字符串	否	--	备注
    @ApiModelProperty(value = "备注")
    private String remark;

    // 字符串	否	--	预约日期，例如：0=今天/1=明天/2=后天
    @ApiModelProperty(value = "预约日期，例如：0=今天/1=明天/2=后天")
    private String dayType;

    // 字符串	否	--	预约起始时间（HH:mm），例如：09:00，顺丰必填
    @ApiModelProperty(value = "预约起始时间（HH:mm），例如：09:00，顺丰必填")
    private String pickupStartTime;

    // 字符串	否	--	预约截止时间（HH:mm），例如：10:00，顺丰必填，预约起始时间和预约截止时间间隔需≥1小时
    @ApiModelProperty(value = "预约截止时间（HH:mm），例如：10:00，顺丰必填，预约起始时间和预约截止时间间隔需≥1小时")
    private String pickupEndTime;

    // 字符串	否	--	渠道ID，如有多个同品牌运力，请联系商务提供后传入
    @ApiModelProperty(value = "渠道ID，如有多个同品牌运力，请联系商务提供后传入")
    private String channelSw;

    // 字符串	否	--	保价额度，单位：元
    @ApiModelProperty(value = "保价额度，单位：元")
    private String valinsPay;

    // 字符串	否	--	寄件人实名信息（圆通、极兔支持 ）
    @ApiModelProperty(value = "寄件人实名信息（圆通、极兔支持 ）")
    private String realName;

    // 字符串	否	--	寄件人证件类型，1：居民身份证 ；2：港澳居民来往内地通行证 ；3：台湾居民来往大陆通行证 ；4：中国公民护照（圆通、极兔支持 ）
    @ApiModelProperty(value = "寄件人证件类型，1：居民身份证 ；2：港澳居民来往内地通行证 ；3：台湾居民来往大陆通行证 ；4：中国公民护照（圆通、极兔支持 ）")
    private String sendIdCardType;

    // 字符串	否	--	寄件人证件号码 （圆通、极兔支持 ）
    @ApiModelProperty(value = "寄件人证件号码 （圆通、极兔支持 ）")
    private String sendIdCard;

    // 字符串	否	--	是否口令签收，Y：需要 N: 不需要，默认值为N（德邦快递专属参数）
    @ApiModelProperty(value = "是否口令签收，Y：需要 N: 不需要，默认值为N（德邦快递专属参数）")
    private String passwordSigning;

    // 字符串	否	--	是否开启订阅功能 0：不开启(默认) 1：开启 说明开启订阅功能时：pollCallBackUrl必须填入 此功能只针对有快递单号的单
    @ApiModelProperty(value = "是否开启订阅功能 0：不开启(默认) 1：开启 说明开启订阅功能时：pollCallBackUrl必须填入 此功能只针对有快递单号的单")
    private String op;

    // 字符串	是	--	面单返回类型，默认为空，不返回面单内容。10：设备打印，20：生成图片短链回调。
    @ApiModelProperty(value = "(必填) 面单返回类型，默认为空，不返回面单内容。10：设备打印，20：生成图片短链回调。")
//    @NotNull(message = "面单返回类型，默认为空，不返回面单内容。10：设备打印，20：生成图片短链回调。 不能为空")
    private String returnType;

    // 字符串	是	--	设备码，returnType为10时必填
    @ApiModelProperty(value = "(条件必填) 设备码，returnType为10时必填")
//    @NotNull(message = "设备码，returnType为10时必填 不能为空")
    private String siid;

    // 字符串	是	--	模板编码，模板id在[获取商家寄件快递公司]接口中查看
    @ApiModelProperty(value = "(必填) 模板编码，模板id在[获取商家寄件快递公司]接口中查看")
//    @NotNull(message = "模板编码，模板id在[获取商家寄件快递公司]接口中查看 不能为空")
    private String tempid;
}
