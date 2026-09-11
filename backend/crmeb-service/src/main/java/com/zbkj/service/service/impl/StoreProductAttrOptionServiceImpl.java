package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.product.StoreProductAttrOption;
import com.zbkj.service.dao.StoreProductAttrOptionDao;
import com.zbkj.service.service.StoreProductAttrOptionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StoreProductAttrOptionServiceImpl extends ServiceImpl<StoreProductAttrOptionDao, StoreProductAttrOption>
        implements StoreProductAttrOptionService {

    @Resource
    private StoreProductAttrOptionDao dao;

    /**
     * 商品变更导致的删除
     */
    @Override
    public Boolean deleteByProductUpdate(Integer proId) {
        LambdaUpdateWrapper<StoreProductAttrOption> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(StoreProductAttrOption::getIsDel, 1);
        wrapper.eq(StoreProductAttrOption::getProductId, proId);
        wrapper.eq(StoreProductAttrOption::getIsDel, 0);
        return update(wrapper);
    }

    /**
     * 根据规格ID获取属性列表
     */
    @Override
    public List<StoreProductAttrOption> findListByAttrId(Integer attrId) {
        LambdaQueryWrapper<StoreProductAttrOption> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreProductAttrOption::getAttrId, attrId);
        lqw.eq(StoreProductAttrOption::getIsDel, 0);
        lqw.orderByAsc(StoreProductAttrOption::getSort).orderByAsc(StoreProductAttrOption::getId);
        return dao.selectList(lqw);
    }
}
