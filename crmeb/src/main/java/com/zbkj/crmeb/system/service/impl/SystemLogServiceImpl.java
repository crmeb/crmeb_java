package com.zbkj.crmeb.system.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.zbkj.crmeb.system.model.SystemLog;
import com.zbkj.crmeb.system.dao.SystemLogDao;
import com.zbkj.crmeb.system.request.SystemLogRequest;
import com.zbkj.crmeb.system.service.SystemLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemLogServiceImpl 接口实现
* @since 2020-04-13
*/
@Service
public class SystemLogServiceImpl extends ServiceImpl<SystemLogDao, SystemLog> implements SystemLogService {

    @Resource
    private SystemLogDao dao;


    @Override
    public List<SystemLog> getList(SystemLogRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

}

