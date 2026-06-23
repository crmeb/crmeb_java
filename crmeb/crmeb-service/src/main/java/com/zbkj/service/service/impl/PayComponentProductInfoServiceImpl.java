package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.wechat.video.PayComponentProductInfo;
import com.zbkj.service.dao.PayComponentProductInfoDao;
import com.zbkj.service.service.PayComponentProductInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
@Service
public class PayComponentProductInfoServiceImpl extends ServiceImpl<PayComponentProductInfoDao, PayComponentProductInfo> implements PayComponentProductInfoService {

    @Resource
    private PayComponentProductInfoDao dao;

    /**
     * 获取商品详情
     * @param proId 商品id
     * @return PayComponentProductInfo
     */
    @Override
    public PayComponentProductInfo getByProId(Integer proId) {
        LambdaQueryWrapper<PayComponentProductInfo> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayComponentProductInfo::getProductId, proId);
        lqw.eq(PayComponentProductInfo::getIsDel, false);
        return dao.selectOne(lqw);
    }

    /**
     * 删除通过商品id
     * @param proId 商品id
     * @return Boolean
     */
    @Override
    public Boolean deleteByProId(Integer proId) {
        LambdaUpdateWrapper<PayComponentProductInfo> luw = Wrappers.lambdaUpdate();
        luw.set(PayComponentProductInfo::getIsDel, true);
        luw.eq(PayComponentProductInfo::getProductId, proId);
        luw.eq(PayComponentProductInfo::getIsDel, false);
        return update(luw);
    }
}

