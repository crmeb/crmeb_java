package com.zbkj.crmeb.wechat.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;

import com.zbkj.crmeb.wechat.model.RoutineQrcode;
import com.zbkj.crmeb.wechat.dao.RoutineQrcodeDao;
import com.zbkj.crmeb.wechat.request.RoutineQrcodeSearchRequest;
import com.zbkj.crmeb.wechat.service.RoutineQrcodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @Description RoutineQrcodeServiceImpl 接口实现
* @since 2020-04-18
*/
@Service
public class RoutineQrcodeServiceImpl extends ServiceImpl<RoutineQrcodeDao, RoutineQrcode> implements RoutineQrcodeService {

    @Resource
    private RoutineQrcodeDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-04-18
    * @return List<RoutineQrcode>
    */
    @Override
    public List<RoutineQrcode> getList(RoutineQrcodeSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

}

