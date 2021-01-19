package com.zbkj.crmeb.task.order;

import com.utils.DateUtil;
import com.zbkj.crmeb.store.service.OrderTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 用户订单完成task任务
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
@Component
@Configuration //读取配置
@EnableScheduling // 2.开启定时任务
public class OrderCompleteTask {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderCompleteTask.class);

    @Autowired
    private OrderTaskService orderTaskService;

    @Scheduled(fixedDelay = 1000 * 60L) //1分钟同步一次数据
    public void init(){
        logger.info("---OrderCompleteTask task------produce Data with fixed rate task: Execution Time - {}", DateUtil.nowDateTime());
        try {
            orderTaskService.complete();

        }catch (Exception e){
            e.printStackTrace();
            logger.error("OrderCompleteTask.task" + " | msg : " + e.getMessage());
        }

    }
}
