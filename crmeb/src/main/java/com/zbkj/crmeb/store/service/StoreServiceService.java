package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreService;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Mr.Zhang
* @description StoreServiceService 接口
* @date 2020-06-10
*/
public interface StoreServiceService extends IService<StoreService> {

    List<StoreService> getList(PageParamRequest pageParamRequest);
}