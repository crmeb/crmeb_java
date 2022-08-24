package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.product.StoreProductCoupon;
import com.zbkj.service.dao.StoreProductCouponDao;
import com.zbkj.service.service.StoreProductCouponService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * StoreProductCouponServiceImpl 接口实现
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
public class StoreProductCouponServiceImpl extends ServiceImpl<StoreProductCouponDao, StoreProductCoupon>
        implements StoreProductCouponService {

    @Resource
    private StoreProductCouponDao dao;
    /**
     *
     * @param productId 产品id
     */
    @Override
    public boolean deleteByProductId(Integer productId) {
        LambdaQueryWrapper<StoreProductCoupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreProductCoupon::getProductId, productId);
        return dao.delete(lambdaQueryWrapper) > 0;
    }

    /**
     * 根据商品id获取已关联优惠券信息
     * @param productId 商品id
     * @return 已关联优惠券
     */
    @Override
    public List<StoreProductCoupon> getListByProductId(Integer productId) {
        LambdaQueryWrapper<StoreProductCoupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreProductCoupon::getProductId, productId);
        return dao.selectList(lambdaQueryWrapper);
    }
}

