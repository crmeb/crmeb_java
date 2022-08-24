package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.system.SystemAdmin;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemAdminAddRequest;
import com.zbkj.common.request.SystemAdminRequest;
import com.zbkj.common.request.SystemAdminUpdateRequest;
import com.zbkj.common.response.SystemAdminResponse;

import java.util.HashMap;
import java.util.List;

/**
 * SystemAdminService 接口
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface SystemAdminService extends IService<SystemAdmin> {

    /**
     * 后台管理员列表
     * @param request 请求参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<SystemAdminResponse> getList(SystemAdminRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增管理员
     */
    Boolean saveAdmin(SystemAdminAddRequest systemAdminAddRequest);

    /**
     * 更新管理员
     */
    Boolean updateAdmin(SystemAdminUpdateRequest systemAdminRequest);

    /**
     * 修改后台管理员状态
     * @param id 管理员id
     * @param status 状态
     * @return Boolean
     */
    Boolean updateStatus(Integer id, Boolean status);

    /**
     * 根据idList获取Map
     * @param adminIdList id数组
     * @return HashMap
     */
    HashMap<Integer, SystemAdmin> getMapInId(List<Integer> adminIdList);

    /**
     * 修改后台管理员是否接收状态
     * @param id 管理员id
     * @return Boolean
     */
    Boolean updateIsSms(Integer id);

    /**
     * 获取可以接收短信的管理员
     */
    List<SystemAdmin> findIsSmsList();

    /**
     * 管理员详情
     * @param id 管理员id
     * @return SystemAdmin
     */
    SystemAdmin getDetail(Integer id);

    /**
     * 通过用户名获取用户
     * @param username 用户名
     * @return SystemAdmin
     */
    SystemAdmin selectUserByUserName(String username);
}
