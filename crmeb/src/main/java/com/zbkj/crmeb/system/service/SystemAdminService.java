package com.zbkj.crmeb.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.request.SystemAdminAddRequest;
import com.zbkj.crmeb.system.request.SystemAdminLoginRequest;
import com.zbkj.crmeb.system.request.SystemAdminRequest;
import com.zbkj.crmeb.system.response.SystemAdminResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SystemAdminService 接口
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
public interface SystemAdminService extends IService<SystemAdmin> {
    List<SystemAdminResponse> getList(SystemAdminRequest request, PageParamRequest pageParamRequest);

    SystemAdminResponse getInfo(SystemAdminRequest request) throws Exception;

    /**
     * PC登录
     */
    SystemAdminResponse login(SystemAdminLoginRequest request, String ip) throws Exception;

    /**
     * 根据Token获取对应用户信息
     */
    SystemAdminResponse getInfoByToken(String token) throws Exception;

    /**
     * 用户登出
     */
    Boolean logout(String token) throws Exception;

    /**
     * 新增管理员
     */
    SystemAdminResponse saveAdmin(SystemAdminAddRequest systemAdminAddRequest) throws Exception;

    /**
     * 更新管理员
     */
    SystemAdminResponse updateAdmin(SystemAdminRequest systemAdminRequest) throws Exception;

    Integer getAdminId();

    SystemAdmin getInfo(Integer adminId);

    SystemAdmin getInfo();

    SystemAdminResponse weChatAuthorizeLogin(String code, String ip) throws Exception;

    Boolean unBind();

    void bind(String wxCode, Integer adminId);

    Boolean updateStatus(Integer id, Boolean status);

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
     * 获取登录页图片
     * @return Map
     */
    Map<String, Object> getLoginPic();
}
