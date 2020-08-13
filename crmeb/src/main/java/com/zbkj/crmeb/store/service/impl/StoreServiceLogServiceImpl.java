package com.zbkj.crmeb.store.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;

import com.zbkj.crmeb.store.model.StoreServiceLog;
import com.zbkj.crmeb.store.dao.StoreServiceLogDao;
import com.zbkj.crmeb.store.request.StoreServiceLogSearchRequest;
import com.zbkj.crmeb.store.service.StoreServiceLogService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @Description StoreServiceLogServiceImpl 接口实现
* @since 2020-06-10
*/
@Service
public class StoreServiceLogServiceImpl extends ServiceImpl<StoreServiceLogDao, StoreServiceLog> implements StoreServiceLogService {

    @Resource
    private StoreServiceLogDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-06-10
    * @return List<StoreServiceLog>
    */
    @Override
    public List<StoreServiceLog> getList(StoreServiceLogSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreServiceLog 类的多条件查询
        LambdaQueryWrapper<StoreServiceLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StoreServiceLog model = new StoreServiceLog();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        lambdaQueryWrapper.orderByAsc(StoreServiceLog::getId);
        return dao.selectList(lambdaQueryWrapper);
    }
}

