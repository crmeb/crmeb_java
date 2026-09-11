package com.zbkj.front.service;

import com.zbkj.common.model.system.SystemStoreStaff;

/**
 * 移动端商家管理权限服务
 */
public interface MobileAdminPermissionService {

    /**
     * 获取当前登录用户对应的启用店员身份。
     */
    SystemStoreStaff requireEnabledStaff();

    /**
     * 获取当前登录用户对应的可核销店员身份。
     */
    SystemStoreStaff requireWriteOffStaff();
}
