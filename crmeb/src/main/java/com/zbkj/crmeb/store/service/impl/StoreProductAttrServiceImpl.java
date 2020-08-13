package com.zbkj.crmeb.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.store.dao.StoreProductAttrDao;
import com.zbkj.crmeb.store.model.StoreProductAttr;
import com.zbkj.crmeb.store.request.StoreProductAttrSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductAttrService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Mr.Zhang
* @description StoreProductAttrServiceImpl 接口实现
* @date 2020-05-27
*/
@Service
public class StoreProductAttrServiceImpl extends ServiceImpl<StoreProductAttrDao, StoreProductAttr>
        implements StoreProductAttrService {

    @Resource
    private StoreProductAttrDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-27
    * @return List<StoreProductAttr>
    */
    @Override
    public List<StoreProductAttr> getList(StoreProductAttrSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreProductAttr 类的多条件查询
        LambdaQueryWrapper<StoreProductAttr> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StoreProductAttr model = new StoreProductAttr();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

    @Override
    public Boolean batchSave(List<StoreProductAttr> storeProductAttrs) {
        if(null == storeProductAttrs) return false;
        int count ;
        for (StoreProductAttr attr : storeProductAttrs) {
            count = dao.insert(attr);
            if(count <= 0) throw new CrmebException("新增产品属性失败");
        }
        return true;
    }

    @Override
    public Boolean batchUpdate(List<StoreProductAttr> storeProductAttrs) {
        if(null == storeProductAttrs) return false;
            for (StoreProductAttr storeProductAttr : storeProductAttrs) {
                UpdateWrapper<StoreProductAttr> lwq = new UpdateWrapper<>();
                lwq.eq("product_id", storeProductAttr.getProductId());
                lwq.eq("attr_name", storeProductAttr.getAttrName());
                lwq.eq("attr_values", storeProductAttr.getAttrValues());
                lwq.eq("type", storeProductAttr.getType());
                if(dao.update(storeProductAttr,lwq) < 0) throw new CrmebException("更新产品属性失败");
            }
        return true;
    }

    @Override
    public List<StoreProductAttr> getByProductId(int productId) {
        LambdaQueryWrapper<StoreProductAttr> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreProductAttr::getProductId, productId);
        List<StoreProductAttr> storeProductAttrs = dao.selectList(lambdaQueryWrapper);
//        storeProductAttrs = storeProductAttrs.stream().map(e->{
//            e.setAttrValues("["+e.getAttrValues()+"]");
//            return e;
//        }).distinct().collect(Collectors.toList());
    return storeProductAttrs;
    }

    @Override
    public void removeByProductId(Integer productId) {
        LambdaQueryWrapper<StoreProductAttr> lambdaQW = new LambdaQueryWrapper<>();
        lambdaQW.eq(StoreProductAttr::getProductId, productId);
        dao.delete(lambdaQW);
    }
}

