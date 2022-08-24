package com.zbkj.service.service;

/**
 * 易联云打印订单 service
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface YlyPrintService {
    /**
     * 易联云打印商品信息
     * @param ordId 订单id
     * @param isAuto 是否自动打印
     */
    void YlyPrint(String ordId,boolean isAuto);
}
