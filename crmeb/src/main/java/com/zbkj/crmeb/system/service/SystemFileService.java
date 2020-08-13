package com.zbkj.crmeb.system.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.request.SystemFileRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemFileService 接口
* @since 2020-04-13
*/
public interface SystemFileService extends IService<SystemFile> {
    List<SystemFile> getList(SystemFileRequest request, PageParamRequest pageParamRequest);
}