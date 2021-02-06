package com.zbkj.crmeb.task.brokerage;


import com.utils.DateUtil;
import com.zbkj.crmeb.task.order.OrderReceiptTask;
import com.zbkj.crmeb.user.service.UserBrokerageRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 佣金冻结期解冻task
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
public class BrokerageFrozenTask {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderReceiptTask.class);

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

//    @Scheduled(fixedDelay = 1000 * 60 * 60L) //1小时同步一次数据
    @Scheduled(fixedDelay = 1000 * 60L) //1小时同步一次数据
    public void init(){
        logger.info("---BrokerageFrozenTask task------produce Data with fixed rate task: Execution Time - {}", DateUtil.nowDateTime());
        try {
            userBrokerageRecordService.brokerageThaw();

        }catch (Exception e){
            e.printStackTrace();
            logger.error("BrokerageFrozenTask.task" + " | msg : " + e.getMessage());
        }

    }
}
