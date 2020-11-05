package com.zbkj.crmeb.front.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 价格计算集合
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
public class PriceGroupResponse {
    private BigDecimal storePostage;
    private BigDecimal storeFreePostage;
    private BigDecimal totalPrice;
    private BigDecimal costPrice;
    private BigDecimal vipPrice;
    private BigDecimal couponPrice;
    private BigDecimal payPrice;
    private BigDecimal payPostage;
    private BigDecimal deductionPrice;
    private Integer usedIntegral;
}
