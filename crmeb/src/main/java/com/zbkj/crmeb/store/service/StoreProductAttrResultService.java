package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.model.StoreProductAttrResult;
import com.zbkj.crmeb.store.request.StoreProductAttrResultSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @description StoreProductAttrResultService 接口
* @date 2020-05-27
*/
public interface StoreProductAttrResultService extends IService<StoreProductAttrResult> {

    List<StoreProductAttrResult> getList(StoreProductAttrResultSearchRequest request, PageParamRequest pageParamRequest);

    StoreProductAttrResult getByProductId(int productId);

    Integer updateByProductId(StoreProductAttrResult storeProductAttrResult);

    void deleteByProductId(int productId, int type);

    /**
     * 根据商品属性值集合查询
     * @param storeProductAttrResult 查询参数
     * @return  查询结果
     */
    List<StoreProductAttrResult> getByEntity(StoreProductAttrResult storeProductAttrResult);
}
