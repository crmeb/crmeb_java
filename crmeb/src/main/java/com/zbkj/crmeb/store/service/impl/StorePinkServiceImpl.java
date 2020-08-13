package com.zbkj.crmeb.store.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;

import com.zbkj.crmeb.store.model.StorePink;
import com.zbkj.crmeb.store.dao.StorePinkDao;
import com.zbkj.crmeb.store.request.StorePinkSearchRequest;
import com.zbkj.crmeb.store.service.StorePinkService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @description StorePinkServiceImpl 接口实现
* @date 2020-05-28
*/
@Service
public class StorePinkServiceImpl extends ServiceImpl<StorePinkDao, StorePink> implements StorePinkService {

    @Resource
    private StorePinkDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-28
    * @return List<StorePink>
    */
    @Override
    public List<StorePink> getList(StorePinkSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StorePink 类的多条件查询
        LambdaQueryWrapper<StorePink> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StorePink model = new StorePink();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

}

