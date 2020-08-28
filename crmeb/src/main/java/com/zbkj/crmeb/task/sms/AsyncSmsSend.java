package com.zbkj.crmeb.task.sms;

import com.utils.DateUtil;
import com.zbkj.crmeb.sms.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Classname AsyncSmsSend
 * @Description 发送短信
 * @Date 2020/6/17 3:21 下午
 * @Created by stivepeim
 */
@Component
@Configuration //读取配置
@EnableScheduling // 2.开启定时任务
public class AsyncSmsSend {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(AsyncSmsSend.class);

    @Autowired
    private SmsService smsService;

    @Scheduled(fixedDelay = 1000 * 5L) //5秒同步一次数据
    public void init(){
        logger.info("---AsyncSmsSend task------produce Data with fixed rate task: Execution Time - {}", DateUtil.nowDate());
        try {
            smsService.consume();

        }catch (Exception e){
            e.printStackTrace();
            logger.error("AsyncSmsSend.task" + " | msg : " + e.getMessage());
        }

    }
}
