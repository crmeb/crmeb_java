package com.zbkj.crmeb.front.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author stivepeim
 * @title: OrderDataResponse
 * @projectName crmeb
 * @description: H5订单头部数量
 * @date 2020/7/2015:43
 */
@Data
public class OrderDataResponse {
    private int completeCount;
    private int evaluatedCount;
    private int orderCount;
    private int receivedCount;
    private int refundCount;
    private BigDecimal sumPrice;
    private int unPaidCount;
    private int unShippedCount;
}
