package com.zbkj.crmeb.system.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.zbkj.crmeb.system.model.SystemNotice;
import com.zbkj.crmeb.system.dao.SystemNoticeDao;
import com.zbkj.crmeb.system.request.SystemNoticeRequest;
import com.zbkj.crmeb.system.service.SystemNoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemNoticeServiceImpl 接口实现
* @since 2020-04-13
*/
@Service
public class SystemNoticeServiceImpl extends ServiceImpl<SystemNoticeDao, SystemNotice> implements SystemNoticeService {

    @Resource
    private SystemNoticeDao dao;


    @Override
    public List<SystemNotice> getList(SystemNoticeRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

}

