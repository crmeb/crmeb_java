package com.zbkj.crmeb.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.model.SystemCity;
import com.zbkj.crmeb.system.request.SystemCityRequest;
import com.zbkj.crmeb.system.request.SystemCitySearchRequest;

/**
* @author Mr.Zhang
* @Description SystemCityService 接口
* @since 2020-04-17
*/
public interface SystemCityAsyncService{

    void async(Integer id);
    void setListTree();
}
