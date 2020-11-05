package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 计算订单价格Request对象
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
public class OrderComputedRequest {

    @ApiModelProperty(value = "地址id")
    private Integer addressId;

    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;

    @ApiModelProperty(value = "支付类型")
    private String payType;

    @ApiModelProperty(value = "快递类型")
    private Integer shippingType;

    @ApiModelProperty(value = "抵扣积分")
    private Boolean useIntegral;

}
