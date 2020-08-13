package com.zbkj.crmeb.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.zbkj.crmeb.system.model.SystemUserTask;
import com.zbkj.crmeb.user.model.UserTaskFinish;
import com.zbkj.crmeb.user.dao.UserTaskFinishDao;
import com.zbkj.crmeb.user.service.UserTaskFinishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author Mr.Zhang
* @Description UserTaskFinishServiceImpl 接口实现
* @since 2020-04-28
*/
@Service
public class UserTaskFinishServiceImpl extends ServiceImpl<UserTaskFinishDao, UserTaskFinish> implements UserTaskFinishService {

    @Resource
    private UserTaskFinishDao dao;



    /**
     * 批量完成任务
     * @param userId 用户id
     * @param list 任务列表
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @Override
    public boolean saveByTaskList(Integer userId, List<SystemUserTask> list) {
        ArrayList<UserTaskFinish> userTaskFinishes = new ArrayList<>();
        for (SystemUserTask systemUserTasks: list) {
            if(!checkTaskIsFinished(userId, systemUserTasks.getId())){
                //如果没有完成任务，则新加一条数据
                userTaskFinishes.add(new UserTaskFinish(systemUserTasks.getId(), userId, true));
            }
        }
        if(userTaskFinishes.size() > 0){
            return saveBatch(userTaskFinishes);
        }
        return true;
    }

    /**
     * 批量删除任务
     * @param userId 用户id
     * @author Mr.Zhang
     * @since 2020-04-29
     */
    @Override
    public boolean deleteByUser(Integer userId) {
        LambdaQueryWrapper<UserTaskFinish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserTaskFinish::getUid, userId);
        return remove(lambdaQueryWrapper);
    }

    /**
     * 检测任务是否已完成
     * @param userId 用户id
     * @param taskId 等级id
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    private boolean checkTaskIsFinished(Integer userId, Integer taskId) {
        LambdaQueryWrapper<UserTaskFinish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserTaskFinish::getUid, userId);
        lambdaQueryWrapper.eq(UserTaskFinish::getTaskId, taskId);
        UserTaskFinish userTaskFinish = getOne(lambdaQueryWrapper);
        if(userTaskFinish == null){
            //如果没有找到，那么说明没有完成
            return false;
        }

        if(!userTaskFinish.getStatus()){
            //status == false 更新当前ID的status
            userTaskFinish.setStatus(true);
            updateById(userTaskFinish);
        }
        return true;
    }
}

