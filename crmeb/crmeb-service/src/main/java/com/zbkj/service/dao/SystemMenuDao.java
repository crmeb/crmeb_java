package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.system.SystemMenu;

import java.util.List;

/**
 * <p>
 * 系统菜单表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2021-11-17
 */
public interface SystemMenuDao extends BaseMapper<SystemMenu> {

    /**
     * 通过角色数据获取权限
     * @param userId 用户id
     * @return List
     */
    List<SystemMenu> findPermissionByUserId(Integer userId);

    /**
     * 获取用户路由
     * @param userId 用户id
     * @return List
     */
    List<SystemMenu> getMenusByUserId(Integer userId);
}
