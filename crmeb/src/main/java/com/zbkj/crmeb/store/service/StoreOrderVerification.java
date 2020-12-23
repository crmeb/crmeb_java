package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.request.StoreOrderStaticsticsRequest;
import com.zbkj.crmeb.store.response.StoreOrderVerificationConfirmResponse;
import com.zbkj.crmeb.store.response.StoreStaffDetail;
import com.zbkj.crmeb.store.response.StoreStaffTopDetail;

import java.util.List;

/**
 * 订单核销业务
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
