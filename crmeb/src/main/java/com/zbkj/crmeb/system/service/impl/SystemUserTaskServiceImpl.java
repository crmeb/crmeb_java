package com.zbkj.crmeb.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.zbkj.crmeb.system.model.SystemUserLevel;
import com.zbkj.crmeb.system.model.SystemUserTask;
import com.zbkj.crmeb.system.dao.SystemUserTaskDao;
import com.zbkj.crmeb.system.request.SystemUserTaskRequest;
import com.zbkj.crmeb.system.service.SystemUserLevelService;
import com.zbkj.crmeb.system.service.SystemUserTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.user.service.UserTaskFinishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Mr.Zhang
* @Description SystemUserTaskServiceImpl 接口实现
* @since 2020-04-13
*/
@Service
public class SystemUserTaskServiceImpl extends ServiceImpl<SystemUserTaskDao, SystemUserTask> implements SystemUserTaskService {

    @Resource
    private SystemUserTaskDao dao;

    @Autowired
    private SystemUserLevelService systemUserLevelService;

    @Autowired
    private UserTaskFinishService userTaskFinishService;

    @Override
    public List<SystemUserTask> getList(SystemUserTaskRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

    /**
     * 完成任务
     * @param userId integer id
     * @param levelId integer 等级
     * @author Mr.Zhang
     * @since 2020-04-10
     * @return boolean
     */
    @Override
    public boolean complete(Integer userId, Integer levelId) {
        List<SystemUserLevel> systemUserLevels = systemUserLevelService.getGradeListByLevelId(levelId);
        if(systemUserLevels == null){
            return false;
        }

        //从集合中取出ID
        List<Integer> idList = systemUserLevels.stream().map(SystemUserLevel::getId).distinct().collect(Collectors.toList());

        //获取任务信息
        LambdaQueryWrapper<SystemUserTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(SystemUserTask::getId);
        lambdaQueryWrapper.in(SystemUserTask::getId, idList);
        List<SystemUserTask> systemUserTasks = dao.selectList(lambdaQueryWrapper);
        if(systemUserTasks == null){
            return false;
        }

        //完成任务
        return userTaskFinishService.saveByTaskList(userId, systemUserTasks);
    }


}

