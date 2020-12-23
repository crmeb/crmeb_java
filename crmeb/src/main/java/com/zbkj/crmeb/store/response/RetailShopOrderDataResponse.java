package com.zbkj.crmeb.store.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 分销用户订单数据
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
public class RetailShopOrderDataResponse {
    // 总订单价
    @ApiModelProperty(value = "订单总价")
    private BigDecimal orderPrice;
    // 订单数量
    @ApiModelProperty(value = "订单数量")
    private Integer orderCount;
    // 用户id
//    private Integer uid;
}
