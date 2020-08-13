package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreCombination;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.request.StoreCombinationSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @description StoreCombinationService 接口
* @date 2020-05-28
*/
public interface StoreCombinationService extends IService<StoreCombination> {

    List<StoreCombination> getList(StoreCombinationSearchRequest request, PageParamRequest pageParamRequest);
}
