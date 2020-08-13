package com.zbkj.crmeb.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.model.SystemCity;
import com.zbkj.crmeb.system.request.SystemCityRequest;
import com.zbkj.crmeb.system.request.SystemCitySearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemCityService 接口
* @since 2020-04-17
*/
public interface SystemCityService extends IService<SystemCity> {

    Object getList(SystemCitySearchRequest request);

    boolean updateStatus(Integer id, Boolean status);

    boolean update(Integer id, SystemCityRequest request);

    Object getListTree();

    String getStringNameInId(String cityIdList);

    List<Integer> getCityIdList();

    SystemCity getCityByCityId(Integer cityId);
}