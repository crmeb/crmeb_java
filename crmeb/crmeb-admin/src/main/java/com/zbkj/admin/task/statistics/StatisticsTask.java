package com.zbkj.admin.task.statistics;

import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.StatisticsTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统计定时任务（每天零点）
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Component("StatisticsTask")
public class StatisticsTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(StatisticsTask.class);

    @Autowired
    private StatisticsTaskService statisticsTaskService;

    /**
     * 每天1点执行
     */
    public void statistics() {
        // cron : 0 0 0 */1 * ?
        logger.info("---StatisticsTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            statisticsTaskService.autoStatistics();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("StatisticsTask.task" + " | msg : " + e.getMessage());
        }
    }

}
