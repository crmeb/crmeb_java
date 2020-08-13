package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreBargain;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.request.StoreBargainSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @description StoreBargainService 接口
* @date 2020-05-28
*/
public interface StoreBargainService extends IService<StoreBargain> {

    List<StoreBargain> getList(StoreBargainSearchRequest request, PageParamRequest pageParamRequest);
}
