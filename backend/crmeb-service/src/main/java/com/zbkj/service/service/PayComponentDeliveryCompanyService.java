package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.wechat.video.PayComponentDeliveryCompany;

import java.util.List;

/**
 *
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public interface PayComponentDeliveryCompanyService extends IService<PayComponentDeliveryCompany> {

    /**
     * 更新物流公司数据
     */
    void updateData();

    /**
     * 获取组件物流公司列表
     * @return List
     */
    List<PayComponentDeliveryCompany> getList();

    /**
     * 通过快递公司ID获取
     * @param deliveryId 快递公司ID
     * @return PayComponentDeliveryCompany
     */
    PayComponentDeliveryCompany getByDeliveryId(String deliveryId);
}
