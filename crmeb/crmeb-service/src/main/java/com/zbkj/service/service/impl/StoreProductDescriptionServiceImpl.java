package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.product.StoreProductDescription;
import com.zbkj.service.dao.StoreProductDescriptionDao;
import com.zbkj.service.service.StoreProductDescriptionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * StoreProductDescriptionServiceImpl 接口实现
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
public class StoreProductDescriptionServiceImpl extends ServiceImpl<StoreProductDescriptionDao, StoreProductDescription> implements StoreProductDescriptionService {

    @Resource
    private StoreProductDescriptionDao dao;

    /**
     * 根据商品id和type删除对应描述
     * @param productId 商品id
     * @param type      类型
     */
    @Override
    public void deleteByProductId(int productId,int type) {
        LambdaQueryWrapper<StoreProductDescription> lmq = Wrappers.lambdaQuery();
        lmq.eq(StoreProductDescription::getProductId, productId).eq(StoreProductDescription::getType,type);
        dao.delete(lmq);
    }

    /**
     * 获取详情
     * @param productId 商品id
     * @param type 商品类型
     * @return StoreProductDescription
     */
    @Override
    public StoreProductDescription getByProductIdAndType(Integer productId, Integer type) {
        LambdaQueryWrapper<StoreProductDescription> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreProductDescription::getProductId, productId);
        lqw.eq(StoreProductDescription::getType,type);
        return dao.selectOne(lqw);
    }
}

