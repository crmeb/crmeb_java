package com.zbkj.crmeb.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.PageParamRequest;

import com.utils.CrmebUtil;
import com.zbkj.crmeb.user.model.UserGroup;
import com.zbkj.crmeb.user.model.UserTag;
import com.zbkj.crmeb.user.dao.UserTagDao;
import com.zbkj.crmeb.user.service.UserTagService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Mr.Zhang
* @Description UserTagServiceImpl 接口实现
* @since 2020-06-05
*/
@Service
public class UserTagServiceImpl extends ServiceImpl<UserTagDao, UserTag> implements UserTagService {

    @Resource
    private UserTagDao dao;


    /**
    * 列表
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-06-05
    * @return List<UserTag>
    */
    @Override
    public List<UserTag> getList(PageParamRequest pageParamRequest) {
        return dao.selectList(null);
    }


    /**
     * 检测是否有标签已经废弃
     * @param tagIdValue String 标签id
     * @author Mr.Zhang
     * @since 2020-06-05
     * @return List<UserTag>
     */
    @Override
    public String clean(String tagIdValue) {
        LambdaQueryWrapper<UserTag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(UserTag::getId, CrmebUtil.stringToArray(tagIdValue));
        List<UserTag> userTags = dao.selectList(lambdaQueryWrapper);
        if(null == userTags){
            return null;
        }

        return userTags.stream().map(s -> s.getId().toString()).distinct().collect(Collectors.joining(","));
    }

    /**
     * 根据id in 返回name字符串
     * @param tagIdValue String 标签id
     * @author Mr.Zhang
     * @since 2020-06-05
     * @return List<UserTag>
     */
    @Override
    public String getGroupNameInId(String tagIdValue) {
        LambdaQueryWrapper<UserTag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(UserTag::getId, CrmebUtil.stringToArray(tagIdValue)).orderByDesc(UserTag::getId);
        List<UserTag> userTags = dao.selectList(lambdaQueryWrapper);
        if(null == userTags){
            return "无";
        }

        return userTags.stream().map(UserTag::getName).distinct().collect(Collectors.joining(","));
    }

}

