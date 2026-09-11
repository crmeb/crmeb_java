package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.wechat.video.PayComponentProductAuditInfo;

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
public interface PayComponentProductAuditInfoService extends IService<PayComponentProductAuditInfo> {

    /**
     * 获取最后一条商品审核信息
     * @param productId 商品id
     * @param auditId 审核单id
     * @return PayComponentProductAuditInfo
     */
    PayComponentProductAuditInfo getByProductIdAndAuditId(Integer productId, String auditId);
}
