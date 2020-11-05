package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.model.StoreProductDescription;
import com.zbkj.crmeb.store.request.StoreProductDescriptionSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @description StoreProductDescriptionService 接口
* @date 2020-05-27
*/
public interface StoreProductDescriptionService extends IService<StoreProductDescription> {

    List<StoreProductDescription> getList(StoreProductDescriptionSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 根据商品id和type删除对应描述
     * @param productId 商品id
     * @param type      类型
     */
    void deleteByProductId(int productId,int type);
}
