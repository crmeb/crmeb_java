package com.zbkj.crmeb.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.system.dao.SystemStoreStaffDao;
import com.zbkj.crmeb.system.model.SystemStore;
import com.zbkj.crmeb.system.model.SystemStoreStaff;
import com.zbkj.crmeb.system.response.SystemStoreStaffResponse;
import com.zbkj.crmeb.system.request.SystemStoreStaffRequest;
import com.zbkj.crmeb.system.service.SystemStoreService;
import com.zbkj.crmeb.system.service.SystemStoreStaffService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
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
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
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
     * @author Mr.Zhang
     * @since 2020-04-17
     * @return List<SystemStoreStaff>
     */
    @Override
    public PageInfo<SystemStoreStaffResponse> getList(Integer storeId, PageParamRequest pageParamRequest) {
        Page<SystemStore> systemStorePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SystemStoreStaff> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(storeId > 0){
            lambdaQueryWrapper.eq(SystemStoreStaff::getStoreId, storeId);
        }
        ArrayList<SystemStoreStaffResponse> systemStoreStaffResponseArrayList = new ArrayList<>();
        List<SystemStoreStaff> systemStoreStaffList = dao.selectList(lambdaQueryWrapper);
        if(systemStoreStaffList.size() < 1){
            return new PageInfo<>();
        }

        //用户信息
        List<Integer> userIdList = systemStoreStaffList.stream().map(SystemStoreStaff::getUid).collect(Collectors.toList());
        HashMap<Integer, User> userList = null;
        if(userIdList.size() >= 1){
            userList = userService.getMapListInUid(userIdList);
        }

        //门店信息
        List<Integer> storeIdList = systemStoreStaffList.stream().map(SystemStoreStaff::getStoreId).collect(Collectors.toList());
        HashMap<Integer, SystemStore> storeList = null;
        if(storeIdList.size() >= 1){
            storeList = systemStoreService.getMapInId(storeIdList);
        }

        for (SystemStoreStaff systemStoreStaff : systemStoreStaffList) {
            SystemStoreStaffResponse systemStoreStaffResponse = new SystemStoreStaffResponse();
            BeanUtils.copyProperties(systemStoreStaff, systemStoreStaffResponse);

            if(null != userList && userList.containsKey(systemStoreStaff.getUid())){
                systemStoreStaffResponse.setUser(userList.get(systemStoreStaff.getUid()));
            }

            if(null != storeList && storeList.containsKey(systemStoreStaff.getStoreId())){
                systemStoreStaffResponse.setSystemStore(storeList.get(systemStoreStaff.getStoreId()));
            }

            systemStoreStaffResponseArrayList.add(systemStoreStaffResponse);
        }

        return CommonPage.copyPageInfo(systemStorePage, systemStoreStaffResponseArrayList);
    }

    /**
     * 根据id集合查询数据，返回 map
     * @param clerkIdList List<Integer> id集合
     * @author Mr.Zhang
     * @since 2020-04-17
     * @return HashMap<Integer, SystemStoreStaff>
     */
    @Override
    public HashMap<Integer, SystemStoreStaff> getMapInId(List<Integer> clerkIdList){
        HashMap<Integer, SystemStoreStaff> map = new HashMap<>();
        if(clerkIdList.size() < 1){
            return map;
        }
        LambdaQueryWrapper<SystemStoreStaff> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.in(SystemStoreStaff::getId, clerkIdList);
        List<SystemStoreStaff> systemStoreStaffList = dao.selectList(lambdaQueryWrapper);
        if(systemStoreStaffList.size() < 1){
            return map;
        }
        for (SystemStoreStaff systemStoreStaff : systemStoreStaffList) {
            map.put(systemStoreStaff.getId(), systemStoreStaff);
        }
        return map;
    }

    /**
     * 根据用户id获取核销信息
     *
     * @param userIds 用户id集合
     * @return 核销人员id集合
     */
    @Override
    public List<SystemStoreStaff> getByAdminUserIds(List<Integer> userIds) {
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
    public boolean saveUnique(SystemStoreStaffRequest request) {
        List<Integer> userIds = new ArrayList<>();
        userIds.add(request.getUid());
        List<SystemStoreStaff> existStaffs = getByAdminUserIds(userIds);
        if(null != existStaffs && existStaffs.size() > 0) throw new CrmebException(Constants.RESULT_VERIFICATION_USER_EXIST);
        SystemStoreStaff systemStoreStaff = new SystemStoreStaff();
        BeanUtils.copyProperties(request, systemStoreStaff);
        return dao.insert(systemStoreStaff) > 0;
    }
}

