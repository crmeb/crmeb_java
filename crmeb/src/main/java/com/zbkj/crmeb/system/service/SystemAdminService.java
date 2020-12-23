package com.zbkj.crmeb.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.request.SystemAdminAddRequest;
import com.zbkj.crmeb.system.request.SystemAdminRequest;
import com.zbkj.crmeb.system.response.SystemAdminResponse;

import java.util.List;

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

    SystemAdminResponse login(SystemAdminRequest request, String ip) throws Exception;

    /**
     * 根据Token获取对应用户信息
     *
     * @param token
     * @return
     */
    SystemAdminResponse getInfoByToken(String token) throws Exception;

    /**
     * 用户登出
     *
     * @param token
     * @return
     * @throws Exception
     */
    Boolean logout(String token) throws Exception;

    /**
     * 新增管理员
     *
     * @param systemAdminAddRequest
     * @return
     */
    SystemAdminResponse saveAdmin(SystemAdminAddRequest systemAdminAddRequest) throws Exception;

    /**
     * 更新管理员
     *
     * @param systemAdminRequest
     * @return
     * @throws Exception
     */
    SystemAdminResponse updateAdmin(SystemAdminRequest systemAdminRequest) throws Exception;

    Integer getAdminId();

    SystemAdmin getInfo(Integer adminId);

    SystemAdmin getInfo();

    SystemAdminResponse weChatAuthorizeLogin(String code, String ip) throws Exception;

    Boolean unBind();

    void bind(String wxCode, Integer adminId);

    Boolean updateStatus(Integer id, Boolean status);
}
