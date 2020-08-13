package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.model.StoreProductCate;
import com.zbkj.crmeb.store.request.StoreProductCateSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @description StoreProductCateService 接口
* @date 2020-05-27
*/
public interface StoreProductCateService extends IService<StoreProductCate> {

    List<StoreProductCate> getList(StoreProductCateSearchRequest request, PageParamRequest pageParamRequest);

    List<StoreProductCate> getByProductId(Integer productId);

//    Integer updateByProductId(StoreProductCate storeProductCate);
}
