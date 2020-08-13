package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StorePink;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.request.StorePinkSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @description StorePinkService 接口
* @date 2020-05-28
*/
public interface StorePinkService extends IService<StorePink> {

    List<StorePink> getList(StorePinkSearchRequest request, PageParamRequest pageParamRequest);
}
