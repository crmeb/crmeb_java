package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemGroupRequest;
import com.zbkj.common.request.SystemGroupSearchRequest;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.model.system.SystemGroup;
import com.zbkj.service.dao.SystemGroupDao;
import com.zbkj.service.service.SystemGroupService;
import com.zbkj.service.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * SystemGroupServiceImpl 接口实现
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
public class SystemGroupServiceImpl extends ServiceImpl<SystemGroupDao, SystemGroup> implements SystemGroupService {

    @Resource
    private SystemGroupDao dao;

    @Autowired
    private UserService userService;

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @return List<SystemGroup>
    */
    @Override
    public List<SystemGroup> getList(SystemGroupSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 SystemGroup 类的多条件查询
        LambdaQueryWrapper<SystemGroup> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.like(SystemGroup::getName, request.getKeywords());
        }
        lambdaQueryWrapper.orderByDesc(SystemGroup::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 新增组合数据
     * @param systemGroupRequest 新增参数
     */
    @Override
    public Boolean add(SystemGroupRequest systemGroupRequest) {
        SystemGroup systemGroup = new SystemGroup();
        BeanUtils.copyProperties(systemGroupRequest, systemGroup);
        return save(systemGroup);
    }

    /**
     * 删除组合数据表
     * @param id Integer
     */
    @Override
    public Boolean delete(Integer id) {
        boolean remove = removeById(id);
        if (remove) {
            // 删除用户对应已经存在的分组标签 虽然数据库用的是String类型但逻辑仅仅只存储一个数据，这里直接删除对应的用户分组id即可
            userService.clearGroupByGroupId(id.toString());
        }
        return remove;
    }

    /**
     * 修改组合数据表
     * @param id integer id
     * @param systemGroupRequest 修改参数
     */
    @Override
    public Boolean edit(Integer id, SystemGroupRequest systemGroupRequest) {
        SystemGroup systemGroup = new SystemGroup();
        BeanUtils.copyProperties(systemGroupRequest, systemGroup);
        systemGroup.setId(id);
        return updateById(systemGroup);
    }

}

