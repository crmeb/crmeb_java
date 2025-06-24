package com.zbkj.admin.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.admin.dao.ScheduleJobDao;
import com.zbkj.admin.model.ScheduleJob;
import com.zbkj.admin.quartz.ScheduleConstants;
import com.zbkj.admin.quartz.ScheduleManager;
import com.zbkj.admin.service.ScheduleJobService;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.request.ScheduleJobRequest;
import org.quartz.CronTrigger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * ScheduleJobServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class ScheduleJobServiceImpl extends ServiceImpl<ScheduleJobDao, ScheduleJob> implements ScheduleJobService {

    @Resource
    private ScheduleJobDao dao;

    @Autowired
    private ScheduleManager scheduleManager;

    /**
     * 项目启动时，初始化定时器
     */
    @PostConstruct
    public void init() {
        getAll().forEach(scheduleJob -> {
            CronTrigger trigger = scheduleManager.getCronTrigger(scheduleJob);
            // 如果定时任务不存在，则创建定时任务
            if (trigger == null) {
                scheduleManager.createScheduleJob(scheduleJob);
            } else if (ScheduleConstants.NORMAL.equals(scheduleJob.getStatus())) {
                scheduleManager.resumeJob(scheduleJob);
            } else if (ScheduleConstants.PAUSE.equals(scheduleJob.getStatus())) {
                scheduleManager.pauseJob(scheduleJob);
            }
        });
    }

    /**
     * 获取所有的job
     *
     * @return List<ScheduleJob>
     */
    @Order
    public List<ScheduleJob> getAll() {
        LambdaQueryWrapper<ScheduleJob> lqw = Wrappers.lambdaQuery();
        lqw.eq(ScheduleJob::getIsDelte, false);
        lqw.orderByDesc(ScheduleJob::getJobId);
        return dao.selectList(lqw);
    }

    /**
     * 添加定时任务
     *
     * @param request 参数
     */
    @Override
    public Boolean add(ScheduleJobRequest request) {
        ScheduleJob scheduleJob = new ScheduleJob();
        BeanUtils.copyProperties(request, scheduleJob);
        scheduleJob.setJobId(null);
        scheduleJob.setStatus(ScheduleConstants.PAUSE);
        scheduleJob.setIsDelte(false);
        boolean save = save(scheduleJob);
        if (save) {
            scheduleManager.createScheduleJob(scheduleJob);
        }
        return save;
    }

    /**
     * 定时任务编辑
     *
     * @param request 编辑参数
     */
    @Override
    public Boolean edit(ScheduleJobRequest request) {
        if (ObjectUtil.isNull(request.getJobId())) {
            throw new CrmebException("定时任务ID不能为空");
        }
        ScheduleJob scheduleJob = getByIdException(request.getJobId());
        if (scheduleJob.getStatus().equals(ScheduleConstants.NORMAL)) {
            throw new CrmebException("请先暂停定时任务");
        }
        BeanUtils.copyProperties(request, scheduleJob);
        boolean update = updateById(scheduleJob);
        if (update) {
            scheduleManager.updateScheduleJob(scheduleJob);
        }
        return update;
    }

    /**
     * 暂停定时任务
     *
     * @param jobId 定时任务ID
     */
    @Override
    public Boolean suspend(Integer jobId) {
        ScheduleJob scheduleJob = getByIdException(jobId);
        if (scheduleJob.getStatus().equals(ScheduleConstants.PAUSE)) {
            throw new CrmebException("定时任务已暂停，请勿重复操作");
        }
        scheduleJob.setStatus(ScheduleConstants.PAUSE);
        boolean update = updateById(scheduleJob);
        if (update) {
            scheduleManager.pauseJob(scheduleJob);
        }
        return update;
    }

    /**
     * 启动定时任务
     *
     * @param jobId 定时任务ID
     */
    @Override
    public Boolean start(Integer jobId) {
        ScheduleJob scheduleJob = getByIdException(jobId);
        if (scheduleJob.getStatus().equals(ScheduleConstants.NORMAL)) {
            throw new CrmebException("定时任务已启动，请勿重复操作");
        }
        scheduleJob.setStatus(ScheduleConstants.NORMAL);
        boolean update = updateById(scheduleJob);
        if (update) {
            scheduleManager.resumeJob(scheduleJob);
        }
        return update;
    }

    /**
     * 删除定时任务
     *
     * @param jobId 定时任务ID
     */
    @Override
    public Boolean delete(Integer jobId) {
        ScheduleJob scheduleJob = getByIdException(jobId);
        if (scheduleJob.getStatus().equals(ScheduleConstants.NORMAL)) {
            throw new CrmebException("请先暂停定时任务");
        }
        scheduleJob.setIsDelte(true);
        boolean delete = updateById(scheduleJob);
        if (delete) {
            scheduleManager.deleteScheduleJob(scheduleJob);
        }
        return delete;
    }

    /**
     * 立即触发定时任务（一次）
     *
     * @param jobId 定时任务ID
     */
    @Override
    public Boolean trig(Integer jobId) {
        ScheduleJob scheduleJob = getByIdException(jobId);
        scheduleManager.run(scheduleJob);
        return Boolean.TRUE;
    }

    private ScheduleJob getByIdException(Integer jobId) {
        ScheduleJob scheduleJob = getById(jobId);
        if (ObjectUtil.isNull(scheduleJob) || scheduleJob.getIsDelte()) {
            throw new CrmebException("定时任务不存在");
        }
        return scheduleJob;
    }
}

