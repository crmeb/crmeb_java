package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreServiceLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.request.StoreServiceLogSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @description StoreServiceLogService 接口
* @date 2020-06-10
*/
public interface StoreServiceLogService extends IService<StoreServiceLog> {

    List<StoreServiceLog> getList(StoreServiceLogSearchRequest request, PageParamRequest pageParamRequest);
}