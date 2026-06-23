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
 * 微信上传物流信息详情Dto对象
 *
 * @author Hzw
 * @version 1.0.0
 * @Date 2023/11/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WechatUploadShippingInfoDto", description="微信上传物流信息详情Dto对象")
public class WechatUploadShippingInfoDto implements Serializable {

    private static final long serialVersionUID = -8804881346870986621L;

    @ApiModelProperty(value = "物流单号，物流快递发货时必填，示例值: 323244567777 字符字节限制: [1, 128]")
    @JsonProperty(value = "tracking_no")
    @JSONField(name = "tracking_no")
    private String trackingNo;

    @ApiModelProperty(value = "物流公司编码，快递公司ID，参见「查询物流公司编码列表」，物流快递发货时必填")
    //  示例值: DHL 字符字节限制: [1, 128]
    @JsonProperty(value = "express_company")
    @JSONField(name = "express_company")
    private String expressCompany;

    @ApiModelProperty(value = "商品信息，例如：微信红包抱枕*1个，限120个字以内", required = true)
    @JsonProperty(value = "item_desc")
    @JSONField(name = "item_desc")
    private String itemDesc;

    @ApiModelProperty(value = "联系方式，当发货的物流公司为顺丰时，联系方式为必填，收件人或寄件人联系方式二选一")
    private WechatShippingContactDto contact;

}
