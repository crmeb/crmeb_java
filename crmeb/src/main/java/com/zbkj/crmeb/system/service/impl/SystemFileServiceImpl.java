package com.zbkj.crmeb.system.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.zbkj.crmeb.system.model.SystemFile;
import com.zbkj.crmeb.system.dao.SystemFileDao;
import com.zbkj.crmeb.system.request.SystemFileRequest;
import com.zbkj.crmeb.system.service.SystemFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemFileServiceImpl 接口实现
* @since 2020-04-13
*/
@Service
public class SystemFileServiceImpl extends ServiceImpl<SystemFileDao, SystemFile> implements SystemFileService {

    @Resource
    private SystemFileDao dao;


    @Override
    public List<SystemFile> getList(SystemFileRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

}

