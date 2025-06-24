package com.zbkj.admin.task.order;

import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.OrderTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统自动确认收货Task
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Component("OrderAutoReceiptTask")
public class OrderAutoReceiptTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderAutoReceiptTask.class);

    @Autowired
    private OrderTaskService orderTaskService;

    public void autoTakeDelivery() {
        // cron : 0 0 0 */1 * ?
        LOGGER.info("---OrderAutoReceiptTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            orderTaskService.autoTakeDelivery();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("OrderAutoReceiptTask.exception" + " | msg : " + e.getMessage());
        }
    }

}
