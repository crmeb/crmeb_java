package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.product.StoreProductAttr;
import com.zbkj.service.dao.StoreProductAttrDao;
import com.zbkj.service.service.StoreProductAttrService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * StoreProductAttrServiceImpl 接口实现
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
public class StoreProductAttrServiceImpl extends ServiceImpl<StoreProductAttrDao, StoreProductAttr>
        implements StoreProductAttrService {

    @Resource
    private StoreProductAttrDao dao;

    /**
     * 根据基本属性查询商品属性详情
     *
     * @param storeProductAttr 商品属性
     * @return 查询商品属性集合
     */
    @Override
    public List<StoreProductAttr> getByEntity(StoreProductAttr storeProductAttr) {
        LambdaQueryWrapper<StoreProductAttr> lqw = Wrappers.lambdaQuery();
        if(null != storeProductAttr.getId()) lqw.eq(StoreProductAttr::getId,storeProductAttr.getId());
        if(StringUtils.isNotBlank(storeProductAttr.getAttrValues()))
            lqw.eq(StoreProductAttr::getAttrValues,storeProductAttr.getAttrValues());
        if(StringUtils.isNotBlank(storeProductAttr.getAttrName()))
            lqw.eq(StoreProductAttr::getAttrName,storeProductAttr.getAttrName());
        if(null != storeProductAttr.getProductId()) lqw.eq(StoreProductAttr::getProductId,storeProductAttr.getProductId());
        if(null != storeProductAttr.getType()) lqw.eq(StoreProductAttr::getType,storeProductAttr.getType());
        return dao.selectList(lqw);
    }

    /**
     * 根据id删除商品
     * @param productId 待删除商品id
     * @param type 类型区分是是否添加营销
     */
    @Override
    public void removeByProductId(Integer productId,int type) {
        LambdaQueryWrapper<StoreProductAttr> lambdaQW = Wrappers.lambdaQuery();
        lambdaQW.eq(StoreProductAttr::getProductId, productId).eq(StoreProductAttr::getType,type);
        dao.delete(lambdaQW);
    }

    /**
     * 删除商品规格
     * @param productId 商品id
     * @param type 商品类型
     * @return Boolean
     */
    @Override
    public Boolean deleteByProductIdAndType(Integer productId, Integer type) {
        LambdaUpdateWrapper<StoreProductAttr> luw = Wrappers.lambdaUpdate();
        luw.set(StoreProductAttr::getIsDel, true);
        luw.eq(StoreProductAttr::getProductId, productId);
        luw.eq(StoreProductAttr::getType, type);
        return update(luw);
    }

    /**
     * 获取商品规格列表
     * @param productId 商品id
     * @param type 商品类型
     * @return List
     */
    @Override
    public List<StoreProductAttr> getListByProductIdAndType(Integer productId, Integer type) {
        LambdaQueryWrapper<StoreProductAttr> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreProductAttr::getProductId, productId);
        lqw.eq(StoreProductAttr::getType, type);
        lqw.eq(StoreProductAttr::getIsDel, false);
        return dao.selectList(lqw);
    }
}

