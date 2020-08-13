package com.zbkj.crmeb.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.zbkj.crmeb.store.dao.StoreProductCouponDao;
import com.zbkj.crmeb.store.model.StoreProductCoupon;
import com.zbkj.crmeb.store.request.StoreProductCouponSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductCouponService;
import org.springframework.beans.BeanUtils;
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
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-08-07
    * @return List<StoreProductCoupon>
    */
    @Override
    public List<StoreProductCoupon> getList(StoreProductCouponSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreProductCoupon 类的多条件查询
        LambdaQueryWrapper<StoreProductCoupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StoreProductCoupon model = new StoreProductCoupon();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

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

