package com.zbkj.crmeb.system.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.zbkj.crmeb.system.model.SystemNoticeAdmin;
import com.zbkj.crmeb.system.dao.SystemNoticeAdminDao;
import com.zbkj.crmeb.system.request.SystemNoticeAdminRequest;
import com.zbkj.crmeb.system.service.SystemNoticeAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemNoticeAdminServiceImpl 接口实现
* @since 2020-04-13
*/
@Service
public class SystemNoticeAdminServiceImpl extends ServiceImpl<SystemNoticeAdminDao, SystemNoticeAdmin> implements SystemNoticeAdminService {

    @Resource
    private SystemNoticeAdminDao dao;


    @Override
    public List<SystemNoticeAdmin> getList(SystemNoticeAdminRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

}

