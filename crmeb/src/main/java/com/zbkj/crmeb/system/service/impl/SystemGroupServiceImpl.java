package com.zbkj.crmeb.system.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;

import com.zbkj.crmeb.system.model.SystemGroup;
import com.zbkj.crmeb.system.dao.SystemGroupDao;
import com.zbkj.crmeb.system.model.SystemGroupData;
import com.zbkj.crmeb.system.request.SystemGroupSearchRequest;
import com.zbkj.crmeb.system.service.SystemGroupService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * SystemGroupServiceImpl 接口实现
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
public class SystemGroupServiceImpl extends ServiceImpl<SystemGroupDao, SystemGroup> implements SystemGroupService {

    @Resource
    private SystemGroupDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-15
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

}

