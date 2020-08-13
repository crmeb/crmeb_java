package com.zbkj.crmeb.wechat.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;

import com.zbkj.crmeb.wechat.model.RoutineAccessToken;
import com.zbkj.crmeb.wechat.dao.RoutineAccessTokenDao;
import com.zbkj.crmeb.wechat.request.RoutineAccessTokenSearchRequest;
import com.zbkj.crmeb.wechat.service.RoutineAccessTokenService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @Description RoutineAccessTokenServiceImpl 接口实现
* @since 2020-04-18
*/
@Service
public class RoutineAccessTokenServiceImpl extends ServiceImpl<RoutineAccessTokenDao, RoutineAccessToken> implements RoutineAccessTokenService {

    @Resource
    private RoutineAccessTokenDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-04-18
    * @return List<RoutineAccessToken>
    */
    @Override
    public List<RoutineAccessToken> getList(RoutineAccessTokenSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

}

