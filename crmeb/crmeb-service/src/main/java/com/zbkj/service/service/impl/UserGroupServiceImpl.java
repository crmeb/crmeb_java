package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.user.UserGroup;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.UserGroupRequest;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.service.dao.UserGroupDao;
import com.zbkj.service.service.UserGroupService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserGroupServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class UserGroupServiceImpl extends ServiceImpl<UserGroupDao, UserGroup> implements UserGroupService {

    @Resource
    private UserGroupDao dao;

    /**
    * 列表
    * @param pageParamRequest 分页类参数
    * @return List<UserGroup>
    */
    @Override
    public List<UserGroup> getList(PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

    /**
     * 根据id in，返回字符串拼接
     * @param groupIdValue String 分组id
     * @return List<UserTag>
     */
    @Override
    public String getGroupNameInId(String groupIdValue) {
        LambdaQueryWrapper<UserGroup> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(UserGroup::getId, CrmebUtil.stringToArray(groupIdValue)).orderByDesc(UserGroup::getId);
        List<UserGroup> userTags = dao.selectList(lambdaQueryWrapper);
        if(null == userTags){
            return "无";
        }

        return userTags.stream().map(UserGroup::getGroupName).distinct().collect(Collectors.joining(","));
    }

    /**
     * 新增用户分组
     * @param userGroupRequest 分组参数
     */
    @Override
    public Boolean create(UserGroupRequest userGroupRequest) {
        if (isExistName(userGroupRequest.getGroupName(), null)) {
            throw new CrmebException("分组名称已存在");
        }
        UserGroup userGroup = new UserGroup();
        BeanUtils.copyProperties(userGroupRequest, userGroup);
        return save(userGroup);
    }

    /**
     * 是否存在用户分组
     * @param groupName 分组名称
     * @param id 分组ID
     */
    private Boolean isExistName(String groupName, Integer id) {
        LambdaQueryWrapper<UserGroup> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserGroup::getId);
        lqw.eq(UserGroup::getGroupName, groupName);
        if (ObjectUtil.isNotNull(id)) {
            lqw.ne(UserGroup::getId, id);
        }
        lqw.last(" limit 1");
        UserGroup userGroup = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(userGroup);
    }

    /**
     * 修改用户分组
     * @param id 分组id
     * @param userGroupRequest 修改参数
     */
    @Override
    public Boolean edit(Integer id, UserGroupRequest userGroupRequest) {
        if (isExistName(userGroupRequest.getGroupName(), id)) {
            throw new CrmebException("分组名称已存在");
        }
        UserGroup userGroup = new UserGroup();
        BeanUtils.copyProperties(userGroupRequest, userGroup);
        userGroup.setId(id);
        return updateById(userGroup);
    }

}

