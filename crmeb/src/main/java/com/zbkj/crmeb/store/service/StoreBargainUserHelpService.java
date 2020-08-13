package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreBargainUserHelp;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.request.StoreBargainUserHelpSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @description StoreBargainUserHelpService 接口
* @date 2020-05-28
*/
public interface StoreBargainUserHelpService extends IService<StoreBargainUserHelp> {

    List<StoreBargainUserHelp> getList(StoreBargainUserHelpSearchRequest request, PageParamRequest pageParamRequest);
}
