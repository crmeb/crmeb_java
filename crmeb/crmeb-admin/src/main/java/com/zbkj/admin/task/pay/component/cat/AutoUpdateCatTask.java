package com.zbkj.admin.task.pay.component.cat;

import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.PayComponentCatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 自动更新自定义交易组件类目定时任务
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
@Component("AutoUpdateCatTask")
public class AutoUpdateCatTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(AutoUpdateCatTask.class);

    @Autowired
    private PayComponentCatService catService;

    /**
     * 每天凌晨一点执行
     */
    public void autoUpdateCat() {
        // cron : 0 0 1 * * ?
        logger.info("---AutoUpdateCatTask task------produce Data with fixed rate task: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            catService.autoUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("OrderAutoCancelTask.task" + " | msg : " + e.getMessage());
        }
    }

}
