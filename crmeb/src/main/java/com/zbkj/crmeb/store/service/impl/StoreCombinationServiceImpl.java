package com.zbkj.crmeb.store.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;

import com.zbkj.crmeb.store.model.StoreCombination;
import com.zbkj.crmeb.store.dao.StoreCombinationDao;
import com.zbkj.crmeb.store.request.StoreCombinationSearchRequest;
import com.zbkj.crmeb.store.service.StoreCombinationService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @description StoreCombinationServiceImpl 接口实现
* @date 2020-05-28
*/
@Service
public class StoreCombinationServiceImpl extends ServiceImpl<StoreCombinationDao, StoreCombination>
        implements StoreCombinationService {

    @Resource
    private StoreCombinationDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-28
    * @return List<StoreCombination>
    */
    @Override
    public List<StoreCombination> getList(StoreCombinationSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreCombination 类的多条件查询
        LambdaQueryWrapper<StoreCombination> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StoreCombination model = new StoreCombination();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

}

