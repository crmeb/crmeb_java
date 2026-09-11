package com.zbkj.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.SystemStoreStaff;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.front.service.MobileAdminPermissionService;
import com.zbkj.service.service.SystemStoreStaffService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 移动端商家管理权限服务实现
 */
@Service
public class MobileAdminPermissionServiceImpl implements MobileAdminPermissionService {

    @Autowired
    private UserService userService;

    @Autowired
    private SystemStoreStaffService systemStoreStaffService;

    @Override
    public SystemStoreStaff requireEnabledStaff() {
        Integer uid = userService.getUserId();
        if (uid == null || uid <= 0) {
            throw new CrmebException(CommonResultCode.UNAUTHORIZED, "请先登录");
        }
        LambdaQueryWrapper<SystemStoreStaff> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SystemStoreStaff::getUid, uid);
        wrapper.eq(SystemStoreStaff::getStatus, 1);
        wrapper.last("limit 1");
        SystemStoreStaff staff = systemStoreStaffService.getOne(wrapper);
        if (staff == null) {
            throw new CrmebException(CommonResultCode.FORBIDDEN, "没有商家管理权限");
        }
        return staff;
    }

    @Override
    public SystemStoreStaff requireWriteOffStaff() {
        Integer uid = userService.getUserId();
        if (uid == null || uid <= 0) {
            throw new CrmebException(CommonResultCode.UNAUTHORIZED, "请先登录");
        }
        LambdaQueryWrapper<SystemStoreStaff> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SystemStoreStaff::getUid, uid);
        wrapper.eq(SystemStoreStaff::getVerifyStatus, 1);
        wrapper.last("limit 1");
        SystemStoreStaff staff = systemStoreStaffService.getOne(wrapper);
        if (staff == null) {
            throw new CrmebException(CommonResultCode.FORBIDDEN, "没有订单核销权限");
        }
        return staff;
    }
}
