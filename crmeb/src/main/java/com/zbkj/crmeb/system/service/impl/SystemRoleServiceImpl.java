package com.zbkj.crmeb.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.category.vo.CategoryTreeVo;
import com.zbkj.crmeb.system.dao.SystemRoleDao;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.model.SystemRole;
import com.zbkj.crmeb.system.request.SystemRoleSearchRequest;
import com.zbkj.crmeb.system.service.SystemAdminService;
import com.zbkj.crmeb.system.service.SystemRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * SystemRoleServiceImpl 接口实现
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
public class SystemRoleServiceImpl extends ServiceImpl<SystemRoleDao, SystemRole> implements SystemRoleService {

    @Resource
    private SystemRoleDao dao;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private CategoryService categoryService;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-04-18
    * @return List<SystemRole>
    */
    @Override
    public List<SystemRole> getList(SystemRoleSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SystemRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq("level", request.getLevel());
        if(null != request.getStatus())
        lambdaQueryWrapper.eq(SystemRole::getStatus, request.getStatus());
//        queryWrapper.eq("rules", request.getRules());
        if(null != request.getRoleName())
        lambdaQueryWrapper.like(SystemRole::getRoleName, request.getRoleName());
        lambdaQueryWrapper.orderByAsc(SystemRole::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据id集合获取对应权限列表
     * @param ids id集合
     * @return 对应的权限列表
     */
    @Override
    public List<SystemRole> getListInIds(List<Integer> ids) {
//        LambdaQueryWrapper<SystemRole> lqw = new LambdaQueryWrapper<>();
//        lqw.in(SystemRole::getId, ids);
        return dao.selectBatchIds(ids);
    }

    /**
     * 检测是否有访问菜单接口取的权限
     * @param uri String 请求参数
     * @author Mr.Zhang
     * @since 2020-07-06
     * @return Boolean
     */
    @Override
    public Boolean checkAuth(String uri) {
        //查询当前路由是否配置了权限，如果没有配置则直接通过
        if(!categoryService.checkUrl(uri)){
            return true;
        }

        List<Integer> categoryIdList = getRoleListInRoleId();
        if(categoryIdList.size() < 1){
            return false;
        }

        //查询分类，根据in id和 路由
        return categoryService.checkAuth(categoryIdList, uri);
    }

    /**
     * 带结构的无线级分类
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    @Override
    public List<CategoryTreeVo> menu() {
        List<Integer> categoryIdList = getRoleListInRoleId();
        return categoryService.getListTree(Constants.CATEGORY_TYPE_MENU, 1, categoryIdList);
    }

    @Override
    public Boolean updateStatus(Integer id, Boolean status) {
        SystemRole role = getById(id);
        if (ObjectUtil.isNull(role)) {
            throw new CrmebException("身份不存在");
        }
        if (role.getStatus().equals(status)) {
            return true;
        }
        role.setStatus(status);
        return updateById(role);
    }

    private List<Integer> getRoleListInRoleId(){
        //获取当前用户的所有权限
        SystemAdmin systemAdmin = systemAdminService.getInfo();
        if(null == systemAdmin || StringUtils.isBlank(systemAdmin.getRoles())){
            throw new CrmebException("没有权限访问！");
        }

        //获取用户权限组
        List<SystemRole> systemRoleList = getVoListInId(systemAdmin.getRoles());
        if(systemRoleList.size() < 1){
            throw new CrmebException("没有权限访问！");
        }

        //获取用户权限规则
        List<Integer> categoryIdList = new ArrayList<>();
        for (SystemRole systemRole : systemRoleList) {
            if(StringUtils.isBlank(systemRole.getRules())){
                continue;
            }

            categoryIdList.addAll(CrmebUtil.stringToArray(systemRole.getRules()));
        }

        return categoryIdList;
    }

    private List<SystemRole> getVoListInId(String roles) {
        LambdaQueryWrapper<SystemRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SystemRole::getId, CrmebUtil.stringToArray(roles));
        return dao.selectList(lambdaQueryWrapper);
    }

}

