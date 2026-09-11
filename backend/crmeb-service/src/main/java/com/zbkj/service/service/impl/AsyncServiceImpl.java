package com.zbkj.service.service.impl;

import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.service.service.AsyncService;
import com.zbkj.service.service.SystemConfigService;
import com.zbkj.service.service.UserBrokerageRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
/**
 * 异步调用服务实现类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class AsyncServiceImpl implements AsyncService {

    private final Logger logger = LoggerFactory.getLogger(AsyncServiceImpl.class);

    @Lazy
    @Autowired
    private SystemConfigService systemConfigService;
    @Lazy
    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;



    /**
     * 佣金冻结
     *
     * @param orderId 订单ID
     * @param nodeType 节点类型
     */
    @Async
    @Override
    public void brokerageFreezeByNode(String orderId, String nodeType) {
        String node = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_BROKERAGE_SHARE_NODE);
        String freezeDay = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_EXTRACT_TIME);
        if (node.equals(nodeType)) {
            logger.info("佣金冻结节点触发");
            userBrokerageRecordService.brokerageFrozen(orderId, Integer.parseInt(freezeDay));
        }
    }
}
