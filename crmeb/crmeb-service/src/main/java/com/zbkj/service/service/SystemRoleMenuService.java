package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.system.SystemRoleMenu;

import java.util.List;

/**
 * SystemRoleMenuService 接口
 * 角色菜单关联服务
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
public interface SystemRoleMenuService extends IService<SystemRoleMenu> {

    /**
     * 通过角色id删除
     * @param rid 角色id
     */
    Boolean deleteByRid(Integer rid);

    /**
     * 通过角色id获取菜单列表
     * @param rid 角色id
     * @return List
     */
    List<Integer> getMenuListByRid(Integer rid);
}
