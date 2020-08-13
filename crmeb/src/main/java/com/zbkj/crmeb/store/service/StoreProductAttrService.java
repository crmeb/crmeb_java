package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.model.StoreProductAttr;
import com.zbkj.crmeb.store.request.StoreProductAttrSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @description StoreProductAttrService 接口
* @date 2020-05-27
*/
public interface StoreProductAttrService extends IService<StoreProductAttr> {

    List<StoreProductAttr> getList(StoreProductAttrSearchRequest request, PageParamRequest pageParamRequest);

    Boolean batchSave(List<StoreProductAttr> storeProductAttrs);

    Boolean batchUpdate(List<StoreProductAttr> storeProductAttrs);

    List<StoreProductAttr> getByProductId(int productId);

    void removeByProductId(Integer productId);

}
