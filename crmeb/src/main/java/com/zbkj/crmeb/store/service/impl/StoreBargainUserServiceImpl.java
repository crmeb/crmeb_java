package com.zbkj.crmeb.store.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;

import com.zbkj.crmeb.store.model.StoreBargainUser;
import com.zbkj.crmeb.store.dao.StoreBargainUserDao;
import com.zbkj.crmeb.store.request.StoreBargainUserSearchRequest;
import com.zbkj.crmeb.store.service.StoreBargainUserService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @description StoreBargainUserServiceImpl 接口实现
* @date 2020-05-28
*/
@Service
public class StoreBargainUserServiceImpl extends ServiceImpl<StoreBargainUserDao, StoreBargainUser> implements StoreBargainUserService {

    @Resource
    private StoreBargainUserDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-28
    * @return List<StoreBargainUser>
    */
    @Override
    public List<StoreBargainUser> getList(StoreBargainUserSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreBargainUser 类的多条件查询
        LambdaQueryWrapper<StoreBargainUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StoreBargainUser model = new StoreBargainUser();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

}

