package com.zbkj.crmeb.task.wechat;

import com.utils.DateUtil;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configuration //读取配置
@EnableScheduling // 2.开启定时任务

public class AsyncWeChatProgramTempMessage {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(AsyncWeChatProgramTempMessage.class);

    @Autowired
    private TemplateMessageService templateMessageService;

    @Scheduled(fixedDelay = 1000 * 60L) //1分钟同步一次数据
    public void init(){
        logger.info("---AsyncWeChatProgramTempMessage task------produce Data with fixed rate task: Execution Time - {}", DateUtil.nowDate());
        try {
            templateMessageService.consumeProgram();

        }catch (Exception e){
            e.printStackTrace();
            logger.error("AsyncWeChatProgramTempMessage.task" + " | msg : " + e.getMessage());
        }

    }
}
