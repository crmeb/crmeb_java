package com.zbkj.crmeb.system.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.request.SystemLogRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemLogService 接口
* @since 2020-04-13
*/
public interface SystemLogService extends IService<SystemLog> {
    List<SystemLog> getList(SystemLogRequest request, PageParamRequest pageParamRequest);
}