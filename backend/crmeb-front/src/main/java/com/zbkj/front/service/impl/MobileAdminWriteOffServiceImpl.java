package com.zbkj.front.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.PayConstants;
import com.zbkj.common.constants.TaskConstants;
import com.zbkj.common.constants.WeChatConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.combination.StorePink;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.system.SystemStoreStaff;
import com.zbkj.common.response.StoreOrderVerificationConfirmResponse;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.front.service.MobileAdminPermissionService;
import com.zbkj.front.service.MobileAdminWriteOffService;
import com.zbkj.service.service.StoreOrderService;
import com.zbkj.service.service.StoreOrderVerification;
import com.zbkj.service.service.StorePinkService;
import com.zbkj.service.service.SystemConfigService;
import com.zbkj.service.service.WechatOrderShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 移动端商家核销服务实现
 */
@Service
public class MobileAdminWriteOffServiceImpl implements MobileAdminWriteOffService {

    @Autowired
    private MobileAdminPermissionService mobileAdminPermissionService;

    @Autowired
    private StoreOrderVerification storeOrderVerification;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StorePinkService storePinkService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private WechatOrderShippingService wechatOrderShippingService;

    @Override
    public StoreOrderVerificationConfirmResponse confirm(String verifyCode) {
        mobileAdminPermissionService.requireWriteOffStaff();
        return storeOrderVerification.getVerificationOrderByCode(verifyCode);
    }

    @Override
    public Boolean writeOff(String verifyCode) {
        SystemStoreStaff staff = mobileAdminPermissionService.requireWriteOffStaff();
        StoreOrderVerificationConfirmResponse confirmResponse = storeOrderVerification.getVerificationOrderByCode(verifyCode);
        if (confirmResponse.getCombinationId() != null && confirmResponse.getCombinationId() > 0) {
            StorePink storePink = storePinkService.getById(confirmResponse.getPinkId());
            if (storePink != null && storePink.getStatus() != 2) {
                throw new CrmebException("当前订单正在拼团中不能核销！");
            }
        }

        StoreOrder storeOrder = new StoreOrder();
        storeOrder.setId(confirmResponse.getId());
        storeOrder.setStatus(Constants.ORDER_STATUS_INT_BARGAIN);
        storeOrder.setClerkId(staff.getId());
        storeOrder.setUpdateTime(DateUtil.date());
        boolean result = storeOrderService.updateById(storeOrder);
        if (result) {
            redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER, confirmResponse.getId());
            if (PayConstants.PAY_TYPE_WE_CHAT.equals(confirmResponse.getPayType())
                    && confirmResponse.getIsChannel() == 1
                    && "1".equals(systemConfigService.getValueByKey(WeChatConstants.CONFIG_WECHAT_ROUTINE_SHIPPING_SWITCH))) {
                wechatOrderShippingService.uploadVerifyShippingInfo(confirmResponse.getOrderId());
            }
        }
        return result;
    }
}
