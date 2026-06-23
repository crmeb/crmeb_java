package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.wechat.video.PayComponentProductInfo;

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
public interface PayComponentProductInfoService extends IService<PayComponentProductInfo> {

    /**
     * 获取商品详情
     * @param proId 商品id
     * @return PayComponentProductInfo
     */
    PayComponentProductInfo getByProId(Integer proId);

    /**
     * 删除通过商品id
     * @param proId 商品id
     * @return Boolean
     */
    Boolean deleteByProId(Integer proId);
}
