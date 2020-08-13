package com.zbkj.crmeb.system.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemUserTask;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.request.SystemUserTaskRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemUserTaskService 接口
* @since 2020-04-13
*/
public interface SystemUserTaskService extends IService<SystemUserTask> {
    List<SystemUserTask> getList(SystemUserTaskRequest request, PageParamRequest pageParamRequest);

    boolean complete(Integer userId, Integer levelId);
}