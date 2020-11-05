package com.zbkj.crmeb.task.sms;
import com.utils.DateUtil;
import com.zbkj.crmeb.sms.model.SmsRecord;
import com.zbkj.crmeb.sms.service.SmsRecordService;
import com.zbkj.crmeb.sms.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Classname AsyncSmsSendResult
 * @Description 发送短信
 * @Date 2020/8/18
 * @Created by stivepeim
 */
@Component
@Configuration //读取配置
@EnableScheduling // 2.开启定时任务
public class AsyncSmsSendResult {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(AsyncSmsSendResult.class);

    @Autowired
    private SmsRecordService smsRecordsService;

    @Scheduled(fixedDelay = 1000 * 60L) // todo 后面更改为 一分钟同步一次数据
    public void init(){
        logger.info("---AsyncSmsResult task------produce Data with fixed rate task: Execution Time - {}", DateUtil.nowDate());
        try {
            smsRecordsService.consumeSmsStatus();

        }catch (Exception e){
            e.printStackTrace();
            logger.error("AsyncSmsSend.task" + " | msg : " + e.getMessage());
        }

    }
}
