package com.zbkj.crmeb.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.store.dao.StoreProductCouponDao;
import com.zbkj.crmeb.store.model.StoreProductCoupon;
import com.zbkj.crmeb.store.service.StoreProductCouponService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @description StoreProductCouponServiceImpl 接口实现
* @date 2020-08-07
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
     *
     * @param storeProductCoupons 优惠券关联信息
     * @return
     */
    @Override
    public void saveCoupons(List<StoreProductCoupon> storeProductCoupons) {
        for (StoreProductCoupon storeProductCoupon : storeProductCoupons) {
            dao.insert(storeProductCoupon);
        }
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

