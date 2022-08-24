package com.zbkj.common.response;

import com.zbkj.common.vo.AliPayJsResultVo;
import com.zbkj.common.vo.WxPayJsResultVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 订单支付结果 Response
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
@ApiModel(value="OrderPayResultResponse对象", description="订单支付结果响应对象")
public class OrderPayResultResponse {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "支付状态")
    private Boolean status;

    @ApiModelProperty(value = "微信调起支付参数对象")
    private WxPayJsResultVo jsConfig;

    @ApiModelProperty(value = "支付类型")
    private String payType;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "订单支付宝支付")
    private String alipayRequest;

    @ApiModelProperty(value = "支付宝调起支付参数对象（app支付专用）")
    private AliPayJsResultVo aliPayConfig;
}
