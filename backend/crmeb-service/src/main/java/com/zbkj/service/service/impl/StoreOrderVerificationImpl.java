package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.PayConstants;
import com.zbkj.common.constants.TaskConstants;
import com.zbkj.common.constants.WeChatConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.combination.StorePink;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.system.SystemAdmin;
import com.zbkj.common.response.StoreOrderVerificationConfirmResponse;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.LoginUserVo;
import com.zbkj.service.dao.StoreOrderDao;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
/**
 * StoreOrderVerificationImpl 接口实现 核销订单
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class StoreOrderVerificationImpl implements StoreOrderVerification {

    @Resource
    private StoreOrderDao dao;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private WechatOrderShippingService wechatOrderShippingService;
    @Autowired
    private StorePinkService storePinkService;
    /**
     * 根据核销码核销订单(相当于收货)
     *
     * @param vCode 核销码
     * @return 核销结果
     */
    @Override
    public boolean verificationOrderByCode(String vCode) {
        StoreOrderVerificationConfirmResponse existOrder = getVerificationOrderByCode(vCode);
        // 判断当前用户是否有权限核销
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        SystemAdmin currentAdmin = loginUserVo.getUser();
        // 添加核销人员后执行核销操作
        StoreOrder storeOrder = new StoreOrder();
        BeanUtils.copyProperties(existOrder,storeOrder);
        if (ObjectUtil.isNotNull(storeOrder.getCombinationId()) && storeOrder.getCombinationId() > 0) {
            StorePink storePink = storePinkService.getById(storeOrder.getPinkId());
            if (storePink.getStatus() != 2) throw new CrmebException("当前订单正在拼团中不能核销！");
        }
        storeOrder.setStatus(Constants.ORDER_STATUS_INT_BARGAIN);
        storeOrder.setClerkId(currentAdmin.getId());
        storeOrder.setUpdateTime(DateUtil.date());
        boolean saveStatus = dao.updateById(storeOrder) > 0;

        // 小程序订阅消息发送
        if(saveStatus){
            //后续操作放入redis
            redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER, storeOrder.getId());
            // 小程序发货管理
            if (storeOrder.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT) && storeOrder.getIsChannel().equals(1)) {
                String shippingSwitch = systemConfigService.getValueByKey(WeChatConstants.CONFIG_WECHAT_ROUTINE_SHIPPING_SWITCH);
                if (StrUtil.isNotBlank(shippingSwitch) && shippingSwitch.equals("1")) {
                    wechatOrderShippingService.uploadVerifyShippingInfo(storeOrder.getOrderId());
                }
            }
        }
        return saveStatus;
    }

    /**
     * 根据核销码查询待核销订单
     *
     * @param vCode 核销码
     * @return 待核销订单详情
     */
    @Override
    public StoreOrderVerificationConfirmResponse getVerificationOrderByCode(String vCode) {
        StoreOrderVerificationConfirmResponse response = new StoreOrderVerificationConfirmResponse();
        StoreOrder storeOrderPram = new StoreOrder().setVerifyCode(vCode).setPaid(true).setRefundStatus(0);
        StoreOrder existOrder = storeOrderService.getByEntityOne(storeOrderPram);
        if(null == existOrder) throw new CrmebException(Constants.RESULT_VERIFICATION_ORDER_NOT_FUND.replace("${vCode}",vCode));
        if(existOrder.getStatus() > 0) throw new CrmebException(Constants.RESULT_VERIFICATION_ORDER_VED.replace("${vCode}",vCode));
        BeanUtils.copyProperties(existOrder, response);
        response.setStoreOrderInfoVos(storeOrderInfoService.getOrderListByOrderId(existOrder.getId()));
        return response;
    }
}
