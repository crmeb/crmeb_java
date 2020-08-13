package com.zbkj.crmeb.system.service;

import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.system.model.SystemStoreStaff;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.model.SystemStoreStaffResponse;

import java.util.HashMap;
import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemStoreStaffService 接口
* @since 2020-04-17
*/
public interface SystemStoreStaffService extends IService<SystemStoreStaff> {

    PageInfo<SystemStoreStaffResponse> getList(Integer storeId, PageParamRequest pageParamRequest);

    HashMap<Integer, SystemStoreStaff> getMapInId(List<Integer> clerkIdList);
}