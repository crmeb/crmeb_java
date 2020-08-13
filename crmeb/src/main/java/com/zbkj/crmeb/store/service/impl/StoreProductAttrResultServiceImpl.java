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
* @author Mr.Zhang
* @description StoreProductAttrResultServiceImpl 接口实现
* @date 2020-05-27
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
        LambdaQueryWrapper<StoreProductAttrResult> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StoreProductAttrResult model = new StoreProductAttrResult();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

    @Override
    public StoreProductAttrResult getByProductId(int productId) {
        LambdaQueryWrapper<StoreProductAttrResult> lw = new LambdaQueryWrapper<>();
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
    public void deleteByProductId(int productId) {
        LambdaQueryWrapper<StoreProductAttrResult> lmdQ = new LambdaQueryWrapper<>();
        lmdQ.eq(StoreProductAttrResult::getProductId, productId);
        dao.delete(lmdQ);
    }
}

