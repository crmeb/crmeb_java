package com.zbkj.admin.task.log;

import com.zbkj.common.utils.DateUtil;
import com.zbkj.service.service.WechatExceptionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 自动删除不需要的历史日志
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
@Component
@Configuration //读取配置
@EnableScheduling // 2.开启定时任务
public class AutoDeleteLogTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(AutoDeleteLogTask.class);

    @Autowired
    private WechatExceptionsService wechatExceptionsService;

    /**
     * 每天0点执行
     */
    @Scheduled(cron = "0 0 0 */1 * ?")
    public void autoDeleteLog() {
        // cron : 0 0 0 */1 * ?
        logger.info("---AutoDeleteLogTask------bargain stop status change task: Execution Time - {}", DateUtil.nowDateTime());
        try {
            wechatExceptionsService.autoDeleteLog();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("AutoDeleteLogTask" + " | msg : " + e.getMessage());
        }
    }

}
