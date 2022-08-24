package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.UserTagRequest;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.model.user.UserTag;
import com.zbkj.service.dao.UserTagDao;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.UserTagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserTagServiceImpl 接口实现
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
public class UserTagServiceImpl extends ServiceImpl<UserTagDao, UserTag> implements UserTagService {

    @Resource
    private UserTagDao dao;

    @Autowired
    private UserService userService;

    /**
    * 列表
    * @param pageParamRequest 分页类参数
    * @return List<UserTag>
    */
    @Override
    public List<UserTag> getList(PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

    /**
     * 根据id in 返回name字符串
     * @param tagIdValue String 标签id
     * @return List<UserTag>
     */
    @Override
    public String getGroupNameInId(String tagIdValue) {
        LambdaQueryWrapper<UserTag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(UserTag::getId, CrmebUtil.stringToArray(tagIdValue)).orderByDesc(UserTag::getId);
        List<UserTag> userTags = dao.selectList(lambdaQueryWrapper);
        if (null == userTags){
            return "无";
        }

        return userTags.stream().map(UserTag::getName).distinct().collect(Collectors.joining(","));
    }

    /**
     * 新增用户标签
     * @param userTagRequest 标签参数
     */
    @Override
    public Boolean create(UserTagRequest userTagRequest) {
        UserTag userTag = new UserTag();
        BeanUtils.copyProperties(userTagRequest, userTag);
        return save(userTag);
    }

    /**
     * 删除用户标签
     * @param id 标签id
     */
    @Override
    public Boolean delete(Integer id) {
        boolean remove = removeById(id);
        if (remove) {
            userService.clearGroupByGroupId(id+"");
        }
        return remove;
    }

    /**
     * 修改用户标签
     * @param id 标签id
     * @param userTagRequest 标签参数
     */
    @Override
    public Boolean updateTag(Integer id, UserTagRequest userTagRequest) {
        UserTag userTag = new UserTag();
        BeanUtils.copyProperties(userTagRequest, userTag);
        userTag.setId(id);
        return updateById(userTag);
    }

}

