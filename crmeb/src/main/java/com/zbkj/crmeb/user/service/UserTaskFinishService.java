package com.zbkj.crmeb.user.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemUserTask;
import com.zbkj.crmeb.user.model.UserTaskFinish;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Mr.Zhang
* @Description UserTaskFinishService 接口
* @since 2020-04-28
*/
public interface UserTaskFinishService extends IService<UserTaskFinish> {
    boolean saveByTaskList(Integer userId, List<SystemUserTask> systemUserTasks);

    boolean deleteByUser(Integer userId);
}