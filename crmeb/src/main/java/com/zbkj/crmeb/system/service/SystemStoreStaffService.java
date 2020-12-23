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
 * SystemStoreStaffService 接口
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
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