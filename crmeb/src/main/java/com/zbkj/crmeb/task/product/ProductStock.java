package com.zbkj.crmeb.task.product;

import com.utils.DateUtil;
import com.zbkj.crmeb.store.service.OrderTaskService;
import com.zbkj.crmeb.store.service.StoreProductService;
import com.zbkj.crmeb.task.order.OrderTakeByUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author stivepeim
 * @title: ProductStock
 * @projectName crmeb
 * @description: 操作商品库存
 * @date 2020/8/2017:04
 */

@Component
@Configuration //读取配置
@EnableScheduling // 2.开启定时任务
public class ProductStock {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(ProductStock.class);

    @Autowired
    private StoreProductService storeProductService;

    @Scheduled(fixedDelay = 1000 * 60L) //1分钟同步一次数据
    public void init(){
        logger.info("---OrderTakeByUser task------produce Data with fixed rate task: Execution Time - {}", DateUtil.nowDateTime());
        try {
            storeProductService.consumeProductStock();

        }catch (Exception e){
            e.printStackTrace();
            logger.error("OrderTakeByUser.task" + " | msg : " + e.getMessage());
        }

    }
}
