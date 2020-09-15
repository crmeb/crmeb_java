package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.request.StoreOrderStaticsticsRequest;
import com.zbkj.crmeb.store.response.StoreOrderVerificationConfirmResponse;
import com.zbkj.crmeb.store.response.StoreStaffDetail;
import com.zbkj.crmeb.store.response.StoreStaffTopDetail;

import java.util.List;

/**
 * @Classname StoreOrderVerification
 * @Description 订单核销业务
 * @Date 9/1/20 10:25 上午
 * @Created by stivepeim
 */
public interface StoreOrderVerification {
    /**
     * 获取订单核销数据
     */
    StoreStaffTopDetail getOrderVerificationData();

    /**
     * 核销月详情
     * @return 月详情
     */
    List<StoreStaffDetail> getOrderVerificationDetail(StoreOrderStaticsticsRequest request);

    /**
     * 根据核销码核销订单
     * @param vCode 核销码
     * @return 核销结果
     */
    boolean verificationOrderByCode(String vCode);

    /**
     * 根据核销码查询待核销订单
     * @param vCode 核销码
     * @return 待核销订单详情
     */
    StoreOrderVerificationConfirmResponse getVerificationOrderByCode(String vCode);
}
