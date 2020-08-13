package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreBargainUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.request.StoreBargainUserSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @description StoreBargainUserService 接口
* @date 2020-05-28
*/
public interface StoreBargainUserService extends IService<StoreBargainUser> {

    List<StoreBargainUser> getList(StoreBargainUserSearchRequest request, PageParamRequest pageParamRequest);
}
