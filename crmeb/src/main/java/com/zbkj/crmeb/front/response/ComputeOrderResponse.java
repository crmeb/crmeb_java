package com.zbkj.crmeb.front.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author stivepeim
 * @title: ComputeOrderResponse
 * @projectName crmeb
 * @description: 计算订单价格 response
 * @date 2020/7/2412:36
 */
@Data
public class ComputeOrderResponse {
    private BigDecimal totalPrice;
        private BigDecimal payPrice;
        private BigDecimal payPostage;
        private BigDecimal couponPrice;
        private BigDecimal deductionPrice;
        private BigDecimal usedIntegral;
        private BigDecimal SurplusIntegral;
}
