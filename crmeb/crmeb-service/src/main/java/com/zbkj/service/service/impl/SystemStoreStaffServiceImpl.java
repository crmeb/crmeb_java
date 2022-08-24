package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.request.SystemStoreStaffRequest;
import com.zbkj.common.response.SystemStoreStaffResponse;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.system.SystemStore;
import com.zbkj.common.model.system.SystemStoreStaff;
import com.zbkj.common.model.user.User;
import com.zbkj.service.dao.SystemStoreStaffDao;
import com.zbkj.service.service.SystemStoreService;
import com.zbkj.service.service.SystemStoreStaffService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SystemStoreStaffServiceImpl 接口实现
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
@Service
public class SystemStoreStaffServiceImpl extends ServiceImpl<SystemStoreStaffDao, SystemStoreStaff> implements SystemStoreStaffService {

    @Resource
    private SystemStoreStaffDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemStoreService systemStoreService;

    /**
     * 列表
     * @param storeId 门店id
     * @param pageParamRequest 分页类参数
     * @return List<SystemStoreStaff>
     */
    @Override
    public PageInfo<SystemStoreStaffResponse> getList(Integer storeId, PageParamRequest pageParamRequest) {
        Page<SystemStore> systemStorePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SystemStoreStaff> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (storeId > 0) {
            lambdaQueryWrapper.eq(SystemStoreStaff::getStoreId, storeId);
        }
        ArrayList<SystemStoreStaffResponse> systemStoreStaffResponseArrayList = new ArrayList<>();
        List<SystemStoreStaff> systemStoreStaffList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(systemStoreStaffList)) {
            return new PageInfo<>();
        }

        //用户信息
        List<Integer> userIdList = systemStoreStaffList.stream().map(SystemStoreStaff::getUid).collect(Collectors.toList());
        HashMap<Integer, User> userList = null;
        if (userIdList.size() >= 1) {
            userList = userService.getMapListInUid(userIdList);
        }
        //门店信息
        List<Integer> storeIdList = systemStoreStaffList.stream().map(SystemStoreStaff::getStoreId).collect(Collectors.toList());
        HashMap<Integer, SystemStore> storeList = null;
        if (storeIdList.size() >= 1) {
            storeList = systemStoreService.getMapInId(storeIdList);
        }
        for (SystemStoreStaff systemStoreStaff : systemStoreStaffList) {
            SystemStoreStaffResponse systemStoreStaffResponse = new SystemStoreStaffResponse();
            BeanUtils.copyProperties(systemStoreStaff, systemStoreStaffResponse);
            if (CollUtil.isNotEmpty(userList) && userList.containsKey(systemStoreStaff.getUid())) {
                systemStoreStaffResponse.setUser(userList.get(systemStoreStaff.getUid()));
            }
            if (CollUtil.isNotEmpty(storeList) && storeList.containsKey(systemStoreStaff.getStoreId())) {
                systemStoreStaffResponse.setSystemStore(storeList.get(systemStoreStaff.getStoreId()));
            }
            systemStoreStaffResponseArrayList.add(systemStoreStaffResponse);
        }
        return CommonPage.copyPageInfo(systemStorePage, systemStoreStaffResponseArrayList);
    }

    /**
     * 查询核销员列表
     * @param userIds uidList
     * @return storeList
     */
    private List<SystemStoreStaff> getByAdminUserIds(List<Integer> userIds) {
        LambdaQueryWrapper<SystemStoreStaff> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.in(SystemStoreStaff::getUid, userIds);
        List<SystemStoreStaff> existStaffs = dao.selectList(lambdaQueryWrapper);
        return existStaffs.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 添加核销员 唯一验证
     *
     * @param request 当前添加参数
     * @return 添加结果
     */
    @Override
    public Boolean saveUnique(SystemStoreStaffRequest request) {
        List<Integer> userIds = new ArrayList<>();
        userIds.add(request.getUid());
        List<SystemStoreStaff> existStaffs = getByAdminUserIds(userIds);
        if (CollUtil.isNotEmpty(existStaffs) && existStaffs.size() > 0) throw new CrmebException(Constants.RESULT_VERIFICATION_USER_EXIST);
        SystemStoreStaff systemStoreStaff = new SystemStoreStaff();
        BeanUtils.copyProperties(request, systemStoreStaff);
        return dao.insert(systemStoreStaff) > 0;
    }

    /**
     * 更新核销员信息
     * @param id 核销员id
     * @param systemStoreStaffRequest 更新参数
     */
    @Override
    public Boolean edit(Integer id, SystemStoreStaffRequest systemStoreStaffRequest) {
        SystemStoreStaff systemStoreStaff = new SystemStoreStaff();
        BeanUtils.copyProperties(systemStoreStaffRequest, systemStoreStaff);
        systemStoreStaff.setId(id);
        return updateById(systemStoreStaff);
    }

    /**
     * 修改核销员状态
     * @param id 核销员id
     * @param status 状态
     * @return Boolean
     */
    @Override
    public Boolean updateStatus(Integer id, Integer status) {
        SystemStoreStaff systemStoreStaff = getById(id);
        if (ObjectUtil.isNull(systemStoreStaff)) {
            throw new CrmebException("核销员不存在");
        }
        if (systemStoreStaff.getStatus().equals(status)) {
            return true;
        }
        systemStoreStaff.setStatus(status);
        return updateById(systemStoreStaff);
    }
}

