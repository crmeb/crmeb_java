package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.request.StoreCategorySearchRequest;
import com.zbkj.crmeb.store.response.StoreCategoryTreeList;

import java.util.List;

/**
* @author Mr.Zhang
* @description StoreCategoryService 接口
* @date 2020-05-28
*/
public interface StoreCategoryService extends IService<StoreCategory> {

    List<StoreCategory> getList(StoreCategorySearchRequest request, PageParamRequest pageParamRequest);

//    List<StoreCategoryTreeList> getTreeList(Integer pid, Integer isShow);
}
