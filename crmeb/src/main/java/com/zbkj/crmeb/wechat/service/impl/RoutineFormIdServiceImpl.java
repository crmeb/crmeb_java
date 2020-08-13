package com.zbkj.crmeb.wechat.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;

import com.zbkj.crmeb.wechat.model.RoutineFormId;
import com.zbkj.crmeb.wechat.dao.RoutineFormIdDao;
import com.zbkj.crmeb.wechat.request.RoutineFormIdSearchRequest;
import com.zbkj.crmeb.wechat.service.RoutineFormIdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @Description RoutineFormIdServiceImpl 接口实现
* @since 2020-04-18
*/
@Service
public class RoutineFormIdServiceImpl extends ServiceImpl<RoutineFormIdDao, RoutineFormId> implements RoutineFormIdService {

    @Resource
    private RoutineFormIdDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-04-18
    * @return List<RoutineFormId>
    */
    @Override
    public List<RoutineFormId> getList(RoutineFormIdSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

}

