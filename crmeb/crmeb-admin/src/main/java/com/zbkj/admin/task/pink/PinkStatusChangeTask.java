package com.zbkj.admin.task.pink;
import com.zbkj.common.utils.DateUtil;
import com.zbkj.service.service.StorePinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 拼团状态变化Task
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
public class PinkStatusChangeTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(PinkStatusChangeTask.class);

    @Autowired
    private StorePinkService storePinkService;

    @Scheduled(cron = "0 */1 * * * ?") //每分钟执行一次
    public void init(){
        logger.info("---PinkStatusChange------bargain stop status change task: Execution Time - {}", DateUtil.nowDateTime());
        try {
            storePinkService.detectionStatus();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("PinkStatusChange" + " | msg : " + e.getMessage());
        }

    }
}
