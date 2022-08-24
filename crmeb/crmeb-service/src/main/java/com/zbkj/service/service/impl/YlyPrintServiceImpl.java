package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.request.YlyPrintRequest;
import com.zbkj.common.request.YlyPrintRequestGoods;
import com.zbkj.common.vo.StoreOrderInfoOldVo;
import com.zbkj.service.service.StoreOrderInfoService;
import com.zbkj.service.service.StoreOrderService;
import com.zbkj.service.service.SystemConfigService;
import com.zbkj.service.service.YlyPrintService;
import com.zbkj.service.util.YlyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 易联云打印订单 service
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class YlyPrintServiceImpl implements YlyPrintService {
    private static final Logger logger = LoggerFactory.getLogger(YlyPrintServiceImpl.class);
    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private YlyUtil ylyUtil;
    /**
     * 易联云打印商品信息
     *
     * @param orderId 订单id
     * @param isAuto 是否自动打印
     */
    @Override
    public void YlyPrint(String orderId,boolean isAuto) {
        if(ylyUtil.checkYlyPrintStatus()){
            throw new CrmebException("易联云 未开启打印");
        }
        // 判断是否开启自动打印
        if(isAuto && ylyUtil.checkYlyPrintAfterPaySuccess()){
            return;
        }
        StoreOrder exitOrder = storeOrderService.getByOderId(orderId);
        if(ObjectUtil.isNull(exitOrder)){
            throw new CrmebException("易联云 打印时未找到 订单信息");
        }
        if(!exitOrder.getPaid()){
            throw new CrmebException("易联云 打印时出错， 订单未支付");
        }
        List<StoreOrderInfoOldVo> exitOrderInfo = storeOrderInfoService.getOrderListByOrderId(exitOrder.getId());
        List<YlyPrintRequestGoods> goods = new ArrayList<>();
        for (StoreOrderInfoOldVo storeOrderInfo : exitOrderInfo) {
            goods.add(new YlyPrintRequestGoods(storeOrderInfo.getInfo().getProductName()
                    ,storeOrderInfo.getInfo().getPrice().toString(),
                    storeOrderInfo.getInfo().getPayNum()+"",
                    exitOrder.getPayPrice().toString()));
        }

        YlyPrintRequest ylyPrintRequest = new YlyPrintRequest();
        ylyPrintRequest.setBusinessName(systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_SITE_NAME));
        ylyPrintRequest.setOrderNo(exitOrder.getOrderId());
        ylyPrintRequest.setDate(DateUtil.format(exitOrder.getPayTime(), Constants.DATE_FORMAT));
        ylyPrintRequest.setName(exitOrder.getRealName());
        ylyPrintRequest.setPhone(exitOrder.getUserPhone());
        ylyPrintRequest.setAddress(exitOrder.getUserAddress());
        ylyPrintRequest.setNote(exitOrder.getMark());

        ylyPrintRequest.setGoods(goods);
        ylyPrintRequest.setAmount(exitOrder.getProTotalPrice().toString());
        ylyPrintRequest.setDiscount(exitOrder.getDeductionPrice().toString());
        ylyPrintRequest.setPostal(exitOrder.getPayPostage().toString());
        ylyPrintRequest.setDeduction(exitOrder.getCouponPrice().toString());
        ylyPrintRequest.setPayMoney(exitOrder.getPayPrice().toString());

        try {
            ylyUtil.ylyPrint(ylyPrintRequest);
            logger.info("易联云打印小票成功" + JSONObject.toJSONString(ylyPrintRequest));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("易联云打印小票失败 " + e.getMessage());
        }
    }


}
