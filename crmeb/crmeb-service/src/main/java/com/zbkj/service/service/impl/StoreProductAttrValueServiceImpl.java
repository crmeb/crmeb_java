package com.zbkj.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.product.StoreProductAttrValue;
import com.zbkj.service.dao.StoreProductAttrValueDao;
import com.zbkj.service.service.StoreProductAttrValueService;
import com.zbkj.service.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * StoreProductAttrValueServiceImpl 接口实现
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
public class StoreProductAttrValueServiceImpl extends ServiceImpl<StoreProductAttrValueDao, StoreProductAttrValue>
        implements StoreProductAttrValueService {

    @Resource
    private StoreProductAttrValueDao dao;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     *
     * @param productId 商品id
     * @param attrId 属性id
     * @return 商品属性集合
     */
    @Override
    public List<StoreProductAttrValue> getListByProductIdAndAttrId(Integer productId, String attrId, Integer type) {
        LambdaQueryWrapper<StoreProductAttrValue> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(StoreProductAttrValue::getProductId, productId);
        lambdaQueryWrapper.eq(StoreProductAttrValue::getType, type);
        if(null != attrId){
            lambdaQueryWrapper.eq(StoreProductAttrValue::getId, attrId);
        }
        lambdaQueryWrapper.eq(StoreProductAttrValue::getIsDel, false);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据产品属性查询
     * @param storeProductAttrValue 商品属性参数
     * @return 查询到的属性结果
     */
    @Override
    public List<StoreProductAttrValue> getByEntity(StoreProductAttrValue storeProductAttrValue) {
        LambdaQueryWrapper<StoreProductAttrValue> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(storeProductAttrValue);
        return dao.selectList(lqw);
    }

    /**
     * 根据商品id删除AttrValue
     * @param productId 商品id
     * @param type 类型区分是是否添加营销
     * @reture 删除结果
     */
    @Override
    public boolean removeByProductId(Integer productId,int type) {
        LambdaQueryWrapper<StoreProductAttrValue> lambdaQW = Wrappers.lambdaQuery();
        lambdaQW.eq(StoreProductAttrValue::getProductId, productId).eq(StoreProductAttrValue::getType,type);
        return dao.delete(lambdaQW) > 0;
    }

    /**
     * 根据id、类型查询
     * @param id ID
     * @param type 类型
     * @return StoreProductAttrValue
     */
    @Override
    public StoreProductAttrValue getByIdAndProductIdAndType(Integer id, Integer productId, Integer type) {
        LambdaQueryWrapper<StoreProductAttrValue> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreProductAttrValue::getId, id);
        lqw.eq(StoreProductAttrValue::getProductId, productId);
        lqw.eq(StoreProductAttrValue::getType, type);
        lqw.eq(StoreProductAttrValue::getIsDel, false);
        return dao.selectOne(lqw);
    }

    /**
     * 根据sku查询
     * @param productId 商品id
     * @param suk   sku
     * @param type  规格类型
     * @return StoreProductAttrValue
     */
    @Override
    public StoreProductAttrValue getByProductIdAndSkuAndType(Integer productId, String suk, Integer type) {
        LambdaQueryWrapper<StoreProductAttrValue> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreProductAttrValue::getProductId, productId);
        lqw.eq(StoreProductAttrValue::getSuk, suk);
        lqw.eq(StoreProductAttrValue::getType, type);
        lqw.eq(StoreProductAttrValue::getIsDel, false);
        return dao.selectOne(lqw);
    }

    /**
     * 添加(退货)/扣减库存
     * @param id 秒杀商品id
     * @param num 数量
     * @param operationType 类型：add—添加，sub—扣减
     * @param type 活动类型 0=商品，1=秒杀，2=砍价，3=拼团
     * @return Boolean
     */
    @Override
    public Boolean operationStock(Integer id, Integer num, String operationType, Integer type) {
        UpdateWrapper<StoreProductAttrValue> updateWrapper = new UpdateWrapper<>();
        if ("add".equals(operationType)) {
            updateWrapper.setSql(StrUtil.format("stock = stock + {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales - {}", num));
            if (type > 0) {
                updateWrapper.setSql(StrUtil.format("quota = quota + {}", num));
            }
        }
        if ("sub".equals(operationType)) {
            updateWrapper.setSql(StrUtil.format("stock = stock - {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales + {}", num));
            if (type > 0) {
                updateWrapper.setSql(StrUtil.format("quota = quota - {}", num));
                // 扣减时加乐观锁保证库存不为负
                updateWrapper.last(StrUtil.format("and (quota - {} >= 0)", num));
            } else {
                // 扣减时加乐观锁保证库存不为负
                updateWrapper.last(StrUtil.format("and (stock - {} >= 0)", num));
            }
        }
        updateWrapper.eq("id", id);
        updateWrapper.eq("type", type);
        boolean update = update(updateWrapper);
        if (!update) {
            throw new CrmebException("更新商品attrValue失败，attrValueId = " + id);
        }
        return update;
    }

    /**
     * 删除商品规格属性值
     * @param productId 商品id
     * @param type 商品类型
     * @return Boolean
     */
    @Override
    public Boolean deleteByProductIdAndType(Integer productId, Integer type) {
        LambdaUpdateWrapper<StoreProductAttrValue> luw = Wrappers.lambdaUpdate();
        luw.set(StoreProductAttrValue::getIsDel, true);
        luw.eq(StoreProductAttrValue::getProductId, productId);
        luw.eq(StoreProductAttrValue::getType, type);
        return update(luw);
    }

    /**
     * 获取商品规格列表
     * @param productId 商品id
     * @param type 商品类型
     * @return List
     */
    @Override
    public List<StoreProductAttrValue> getListByProductIdAndType(Integer productId, Integer type) {
        LambdaQueryWrapper<StoreProductAttrValue> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreProductAttrValue::getProductId, productId);
        lqw.eq(StoreProductAttrValue::getType, type);
        lqw.eq(StoreProductAttrValue::getIsDel, false);
        return dao.selectList(lqw);
    }
}

