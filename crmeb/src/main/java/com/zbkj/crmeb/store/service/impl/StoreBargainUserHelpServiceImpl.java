package com.zbkj.crmeb.store.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;

import com.zbkj.crmeb.store.model.StoreBargainUserHelp;
import com.zbkj.crmeb.store.dao.StoreBargainUserHelpDao;
import com.zbkj.crmeb.store.request.StoreBargainUserHelpSearchRequest;
import com.zbkj.crmeb.store.service.StoreBargainUserHelpService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @description StoreBargainUserHelpServiceImpl 接口实现
* @date 2020-05-28
*/
@Service
public class StoreBargainUserHelpServiceImpl extends ServiceImpl<StoreBargainUserHelpDao, StoreBargainUserHelp> implements StoreBargainUserHelpService {

    @Resource
    private StoreBargainUserHelpDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-28
    * @return List<StoreBargainUserHelp>
    */
    @Override
    public List<StoreBargainUserHelp> getList(StoreBargainUserHelpSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreBargainUserHelp 类的多条件查询
        LambdaQueryWrapper<StoreBargainUserHelp> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StoreBargainUserHelp model = new StoreBargainUserHelp();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

}

