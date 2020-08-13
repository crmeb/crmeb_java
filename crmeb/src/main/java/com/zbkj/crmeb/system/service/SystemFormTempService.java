package com.zbkj.crmeb.system.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemFormTemp;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.request.SystemFormCheckRequest;
import com.zbkj.crmeb.system.request.SystemFormTempSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @description SystemFormTempService 接口
* @date 2020-05-15
*/
public interface SystemFormTempService extends IService<SystemFormTemp> {

    List<SystemFormTemp> getList(SystemFormTempSearchRequest request, PageParamRequest pageParamRequest);

    void checkForm(SystemFormCheckRequest systemFormCheckRequest);
}