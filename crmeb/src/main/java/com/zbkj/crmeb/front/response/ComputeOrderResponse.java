package com.zbkj.crmeb.front.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 计算订单价格 response
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
public class ComputeOrderResponse {
    private BigDecimal totalPrice;
        // 支付金额
        private BigDecimal payPrice;
        // 邮费金额
        private BigDecimal payPostage;
        // 优惠券金额
        private BigDecimal couponPrice;
        // 扣除金额
        private BigDecimal deductionPrice;
        // 使用金粉
        private Integer usedIntegral;
        // 剩余积分
        private Integer SurplusIntegral;
}
