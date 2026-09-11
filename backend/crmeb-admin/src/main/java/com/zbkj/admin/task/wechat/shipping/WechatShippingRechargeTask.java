package com.zbkj.admin.task.wechat.shipping;

import com.zbkj.service.service.WechatOrderShippingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 充值订单微信小程序发货管理定时任务
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/1/6
 */
@Component("WechatShippingRechargeTask")
public class WechatShippingRechargeTask {

    private static final Logger logger = LoggerFactory.getLogger(WechatShippingRechargeTask.class);

    @Autowired
    private WechatOrderShippingService wechatOrderShippingService;

    /**
     * 每1小时执行一次
     * 处理超过支付10分钟的充值订单
     *
     */
    public void uploadRechargeOrderShipping() {
        // cron : 0 0 */1 * * ?
        wechatOrderShippingService.batchUploadRechargeOrderShipping();
    }

}
