package com.zbkj.crmeb.payment.vo.wechat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 调用微信支付所需要的参数
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
@ApiModel(value="PayParamsVo对象", description="调用微信支付所需要的参数")
public class PayParamsVo {

    public PayParamsVo() {
    }

    public PayParamsVo(String orderNo, String fromType, String clientIp, String title, BigDecimal price, Integer userId, AttachVo attach) {
        this.orderNo = orderNo;
        this.fromType = fromType;
        this.clientIp = clientIp;
        this.title = title;
        this.price = price;
        this.userId = userId;
        this.attach = attach;
    }

    @ApiModelProperty(value = "对外使用订单号", required = true)
    private String orderNo;

    @ApiModelProperty(value = "支付二级类型 如微信新的 H5 小程序", required = true)
    private String fromType;

    @ApiModelProperty(value = "客户端IP", required = true)
    private String clientIp;

    @ApiModelProperty(value = "备注", required = true)
    private String title;

    @ApiModelProperty(value = "支付金额", required = true)
    private BigDecimal price;

    @ApiModelProperty(value = "用户id", required = true)
    private Integer userId;

    @ApiModelProperty(value = "appId", required = true)
    private String appId;

    @ApiModelProperty(value = "商户id", required = true)
    private String mchId;

    @ApiModelProperty(value = "签名key", required = true)
    private String appKey;

    @ApiModelProperty(value = "openId")
    private String openId;

    @ApiModelProperty(value = "附加信息")
    private AttachVo attach;
}
