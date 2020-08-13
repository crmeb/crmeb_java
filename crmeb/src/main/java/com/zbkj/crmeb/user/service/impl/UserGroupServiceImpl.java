package com.zbkj.crmeb.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.user.dao.UserGroupDao;
import com.zbkj.crmeb.user.model.UserGroup;
import com.zbkj.crmeb.user.service.UserGroupService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Mr.Zhang
* @Description UserGroupServiceImpl 接口实现
* @since 2020-04-28
*/
@Service
public class UserGroupServiceImpl extends ServiceImpl<UserGroupDao, UserGroup> implements UserGroupService {

    @Resource
    private UserGroupDao dao;


    /**
    * 列表
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-04-28
    * @return List<UserGroup>
    */
    @Override
    public List<UserGroup> getList(PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

    /**
     * 检测是否有分组已经废弃
     * @param groupIdValue String 分组id
     * @author Mr.Zhang
     * @since 2020-06-05
     * @return List<UserTag>
     */
    @Override
    public String clean(String groupIdValue) {
        LambdaQueryWrapper<UserGroup> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(UserGroup::getId, CrmebUtil.stringToArray(groupIdValue));
        List<UserGroup> userTags = dao.selectList(lambdaQueryWrapper);
        if(null == userTags){
            return null;
        }

        return userTags.stream().map(s -> s.getId().toString()).distinct().collect(Collectors.joining(","));
    }

    /**
     * 根据id in，返回字符串拼接
     * @param groupIdValue String 分组id
     * @author Mr.Zhang
     * @since 2020-06-05
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

}

