package com.zbkj.crmeb.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.store.dao.StoreProductAttrResultDao;
import com.zbkj.crmeb.store.model.StoreProductAttrResult;
import com.zbkj.crmeb.store.request.StoreProductAttrResultSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductAttrResultService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * StoreProductAttrResultService实现类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class StoreProductAttrResultServiceImpl extends ServiceImpl<StoreProductAttrResultDao, StoreProductAttrResult>
        implements StoreProductAttrResultService {

    @Resource
    private StoreProductAttrResultDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-27
    * @return List<StoreProductAttrResult>
    */
    @Override
    public List<StoreProductAttrResult> getList(StoreProductAttrResultSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreProductAttrResult 类的多条件查询
        LambdaQueryWrapper<StoreProductAttrResult> lambdaQueryWrapper = Wrappers.lambdaQuery();
        StoreProductAttrResult model = new StoreProductAttrResult();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

    @Override
    public StoreProductAttrResult getByProductId(int productId) {
        LambdaQueryWrapper<StoreProductAttrResult> lw = Wrappers.lambdaQuery();
        lw.eq(StoreProductAttrResult::getProductId, productId);
        List<StoreProductAttrResult> results = dao.selectList(lw);
        if(results.size() > 1){
            return results.get(results.size()-1);
        }else if(results.size() == 1){
            return results.get(0);
        }else{
            return null;
        }
    }

    @Override
    public Integer updateByProductId(StoreProductAttrResult storeProductAttrResult) {
        LambdaUpdateWrapper<StoreProductAttrResult> uw = Wrappers.lambdaUpdate();
        uw.eq(StoreProductAttrResult::getProductId, storeProductAttrResult.getProductId());
        uw.set(StoreProductAttrResult::getResult, storeProductAttrResult.getResult());
        uw.set(StoreProductAttrResult::getChangeTime, storeProductAttrResult.getChangeTime());
        if(null != storeProductAttrResult.getType()){
            uw.set(StoreProductAttrResult::getType, storeProductAttrResult.getType());
        }
        return dao.update(storeProductAttrResult, uw);
    }

    @Override
    public void deleteByProductId(int productId, int type) {
        LambdaQueryWrapper<StoreProductAttrResult> lmdQ = Wrappers.lambdaQuery();
        lmdQ.eq(StoreProductAttrResult::getProductId, productId).eq(StoreProductAttrResult::getType,type);
        dao.delete(lmdQ);
    }

    /**
     * 根据商品属性值集合查询
     *
     * @param storeProductAttrResult 查询参数
     * @return 查询结果
     */
    @Override
    public List<StoreProductAttrResult> getByEntity(StoreProductAttrResult storeProductAttrResult) {
        LambdaQueryWrapper<StoreProductAttrResult> lqw = Wrappers.lambdaQuery();
        lqw.setEntity(storeProductAttrResult);
        return dao.selectList(lqw);
    }
}

