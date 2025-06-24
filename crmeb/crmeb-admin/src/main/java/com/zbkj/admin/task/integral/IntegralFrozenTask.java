package com.zbkj.admin.task.integral;


import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.UserIntegralRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 积分冻结期解冻task
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
@Component("IntegralFrozenTask")
public class IntegralFrozenTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(IntegralFrozenTask.class);

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    /**
     * 1分钟同步一次数据
     */
    public void integralFrozen() {
        // cron : 0 */1 * * * ?
        logger.info("---IntegralFrozenTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            userIntegralRecordService.integralThaw();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("IntegralFrozenTask.task" + " | msg : " + e.getMessage());
        }
    }

}
