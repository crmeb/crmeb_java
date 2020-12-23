package com.zbkj.crmeb.store.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 核销订单top数据对象
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
