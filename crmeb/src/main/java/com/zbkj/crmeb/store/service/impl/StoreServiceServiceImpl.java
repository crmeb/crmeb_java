package com.zbkj.crmeb.store.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;

import com.zbkj.crmeb.store.model.StoreService;
import com.zbkj.crmeb.store.dao.StoreServiceDao;
import com.zbkj.crmeb.store.service.StoreServiceService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @Description StoreServiceServiceImpl 接口实现
* @since 2020-06-10
*/
@Service
public class StoreServiceServiceImpl extends ServiceImpl<StoreServiceDao, StoreService> implements StoreServiceService {

    @Resource
    private StoreServiceDao dao;


    /**
    * 列表
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-06-10
    * @return List<StoreService>
    */
    @Override
    public List<StoreService> getList(PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreService> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(StoreService::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

}

