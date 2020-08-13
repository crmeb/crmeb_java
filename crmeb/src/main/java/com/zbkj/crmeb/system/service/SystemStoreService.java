package com.zbkj.crmeb.system.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.front.request.StoreNearRequest;
import com.zbkj.crmeb.front.response.StoreNearResponse;
import com.zbkj.crmeb.system.model.SystemStore;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.request.SystemStoreRequest;

import java.util.HashMap;
import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemStoreService 接口
* @since 2020-04-13
*/
public interface SystemStoreService extends IService<SystemStore> {
    List<SystemStore> getList(String keywords, int status, PageParamRequest pageParamRequest);

    /**
     * 根据基本参数获取
     * @param systemStore 基本参数
     * @return 门店自提结果
     */
    SystemStore getByCondition(SystemStore systemStore);

    boolean updateStatus(Integer id, boolean status);
    boolean delete(Integer id);

    HashMap<String, Integer> getCount();

    HashMap<Integer, SystemStore> getMapInId(List<Integer> storeIdList);

    StoreNearResponse getNearList(StoreNearRequest request, PageParamRequest pageParamRequest);

    boolean create(SystemStoreRequest request);

    boolean update(Integer id, SystemStoreRequest request);
}