package com.zbkj.crmeb.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.category.vo.CategoryTreeVo;
import com.zbkj.crmeb.system.model.SystemRole;
import com.zbkj.crmeb.system.request.SystemRoleSearchRequest;

import java.util.List;

/**
 * SystemRoleService 接口
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
public interface SystemRoleService extends IService<SystemRole> {

    List<SystemRole> getList(SystemRoleSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 根据id集合获取对应权限列表
     * @param ids id集合
     * @return 对应的权限列表
     */
    List<SystemRole> getListInIds(List<Integer> ids);

    Boolean checkAuth(String uri);

    List<CategoryTreeVo> menu();

    Boolean updateStatus(Integer id, Boolean status);
}
