package com.zbkj.crmeb.system.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemGroupData;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.request.SystemFormCheckRequest;
import com.zbkj.crmeb.system.request.SystemGroupDataRequest;
import com.zbkj.crmeb.system.request.SystemGroupDataSearchRequest;

import java.util.HashMap;
import java.util.List;

/**
* @author Mr.Zhang
* @description SystemGroupDataService 接口
* @date 2020-05-15
*/
public interface SystemGroupDataService extends IService<SystemGroupData> {

    List<SystemGroupData> getList(SystemGroupDataSearchRequest request, PageParamRequest pageParamRequest);

    boolean create(SystemGroupDataRequest systemGroupDataRequest);

    boolean update(Integer id, SystemGroupDataRequest request);

    <T> List<T> getListByGid(Integer gid, Class<T> cls);

    List<HashMap<String, Object>> getListMapByGid(Integer gid);

    <T> T getNormalInfo(Integer groupDataId, Class<T> cls);
}