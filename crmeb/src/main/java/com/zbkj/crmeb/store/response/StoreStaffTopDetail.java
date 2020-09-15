package com.zbkj.crmeb.store.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Classname StoreStaffTopDetail
 * @Description 核销订单top数据对象
 * @Date 8/29/20 3:37 下午
 * @Created by stivepeim
 */
@Data
public class StoreStaffTopDetail {
    private Integer completeCount; // 订单完成订单数量
    private Integer evaluatedCount;// 待评价订单数量
    private Integer monthCount;
    private BigDecimal monthPrice;
    private Integer orderCount;
    private Integer proCount;
    private BigDecimal proPrice;
    private Integer receivedCount;
    private Integer refundCount;
    private BigDecimal sumPrice;
    private Integer todayCount;
    private BigDecimal todayPrice;
    private Integer unpaidCount;
    private Integer unshippedCount;
    private Integer verificationCount;// 待核销数量
}
