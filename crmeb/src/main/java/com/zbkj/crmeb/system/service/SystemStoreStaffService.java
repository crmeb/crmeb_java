package com.zbkj.crmeb.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.system.model.SystemStoreStaff;
import com.zbkj.crmeb.system.request.SystemStoreStaffRequest;
import com.zbkj.crmeb.system.response.SystemStoreStaffResponse;

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

    /**
     * 根据用户id获取核销信息
     * @param userIds    用户id集合
     * @return          核销信息
     */
    List<SystemStoreStaff> getByAdminUserIds(List<Integer> userIds);

    /**
     *      添加核销员 唯一验证
     * @param request  当前添加参数
     * @return                  添加结果
     */
    boolean saveUnique(SystemStoreStaffRequest request);
}