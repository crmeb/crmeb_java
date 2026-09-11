package com.zbkj.common.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 微信物流联系方式Dto对象
 *
 * @author Hzw
 * @version 1.0.0
 * @Date 2023/11/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WechatShippingContactDto", description="微信物流联系方式Dto对象")
public class WechatShippingContactDto implements Serializable {

    private static final long serialVersionUID = -8804881346870986621L;

    @ApiModelProperty(value = "寄件人联系方式，寄件人联系方式，采用掩码传输，最后4位数字不能打掩码")
    // 示例值: 189****1234, 021-****1234, ****1234, 0**2-*1234, 02-****23-10, 123-8008
    // 值限制: 0 ≤ value ≤ 1024 字段加密: 使用APIv3定义的方式加密
    @JsonProperty(value = "consignor_contact")
    @JSONField(name = "consignor_contact")
    private String consignorContact;

    @ApiModelProperty(value = "收件人联系方式，收件人联系方式为，采用掩码传输，最后4位数字不能打掩码")
    //  示例值: 1891234, 021-****1234, ****1234, 02-*1234, 02-******23-10, ****123-8008
    //  值限制: 0 ≤ value ≤ 1024 字段加密: 使用APIv3定义的方式加密
    @JsonProperty(value = "receiver_contact")
    @JSONField(name = "receiver_contact")
    private String receiverContact;

}
