package com.zbkj.crmeb.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.store.dao.StoreProductCateDao;
import com.zbkj.crmeb.store.model.StoreProductCate;
import com.zbkj.crmeb.store.request.StoreProductCateSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductCateService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @description StoreProductCateServiceImpl 接口实现
* @date 2020-05-27
*/
@Service
public class StoreProductCateServiceImpl extends ServiceImpl<StoreProductCateDao, StoreProductCate>
        implements StoreProductCateService {

    @Resource
    private StoreProductCateDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-27
    * @return List<StoreProductCate>
    */
    @Override
    public List<StoreProductCate> getList(StoreProductCateSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreProductCate 类的多条件查询
        LambdaQueryWrapper<StoreProductCate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StoreProductCate model = new StoreProductCate();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<StoreProductCate> getByProductId(Integer productId) {
        LambdaQueryWrapper<StoreProductCate> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreProductCate::getProductId, productId);
        return dao.selectList(lqw);
    }

//    @Override
//    public Integer updateByProductId(StoreProductCate storeProductCate) {
//        LambdaUpdateWrapper<StoreProductCate> luw = new LambdaUpdateWrapper<>();
//        luw.set(StoreProductCate::getProductId, storeProductCate.getProductId());
//        luw.set(StoreProductCate::getCateId, storeProductCate.getCateId());
//        luw.set(StoreProductCate::getAddTime, storeProductCate.getAddTime());
//        return dao.update(storeProductCate, luw);
//    }
}

