package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.product.StoreProductGuarantee;
import com.zbkj.common.request.StoreProductGuaranteeRequest;
import com.zbkj.common.response.StoreProductGuaranteeResponse;

import java.util.List;

/**
 *  StoreProductGuaranteeService 接口
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public interface StoreProductGuaranteeService extends IService<StoreProductGuarantee> {

    /**
     * 保障服务列表
     *
     * @param isShow 是否显示
     * @return List
     */
    List<StoreProductGuaranteeResponse> getAdminList(Boolean isShow);

    /**
     * 新增保障服务
     * @param request 新增参数
     * @return Boolean
     */
    Boolean add(StoreProductGuaranteeRequest request);

    /**
     * 删除保障服务
     * @param id 保障服务ID
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 修改保障服务
     * @param request 修改参数
     * @return Boolean
     */
    Boolean edit(StoreProductGuaranteeRequest request);

    /**
     * 修改保障服务显示状态
     * @param id 保障服务ID
     * @return Boolean
     */
    Boolean updateShowStatus(Integer id);


    /**
     * 保障服务列表
     * @return List
     */
    List<StoreProductGuarantee> findByIdList(List<Integer> gidList);
}
