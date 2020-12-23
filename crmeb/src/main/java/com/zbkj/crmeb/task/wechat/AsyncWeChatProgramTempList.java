package com.zbkj.crmeb.task.wechat;

import com.utils.DateUtil;
import com.zbkj.crmeb.wechat.service.WechatProgramPublicTempService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *  小程序公共模板库
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
public class AsyncWeChatProgramTempList {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(AsyncWeChatProgramTempList.class);

    @Autowired
    private WechatProgramPublicTempService wechatProgramPublicTempService;

    @Value("${server.asyncWeChatProgramTempList}")
    private Boolean asyncWeChatProgramTempList;

    @Scheduled(fixedDelay = 1000L * 60 * 60 * 24) //1天同步一次数据
    public void init(){
        logger.info("---AsyncWeChatProgramTempList task------produce Data with fixed rate task: Execution Time - {}", DateUtil.nowDate());
        try {
            if(asyncWeChatProgramTempList){
                wechatProgramPublicTempService.async();
            }

        }catch (Exception e){
            e.printStackTrace();
            logger.error("AsyncWeChatProgramTempList.task" + " | msg : " + e.getMessage());
        }

    }
}
