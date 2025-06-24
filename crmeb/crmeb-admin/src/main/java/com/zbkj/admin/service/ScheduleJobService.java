package com.zbkj.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.admin.model.ScheduleJob;
import com.zbkj.common.request.ScheduleJobRequest;

import java.util.List;

/**
 * ScheduleJobService 接口
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
public interface ScheduleJobService extends IService<ScheduleJob> {

    /**
     * 所有定时任务列表
     */
    List<ScheduleJob> getAll();

    /**
     * 添加定时任务
     *
     * @param request 参数
     */
    Boolean add(ScheduleJobRequest request);

    /**
     * 定时任务编辑
     *
     * @param request 编辑参数
     */
    Boolean edit(ScheduleJobRequest request);

    /**
     * 暂停定时任务
     *
     * @param jobId 定时任务ID
     */
    Boolean suspend(Integer jobId);

    /**
     * 启动定时任务
     *
     * @param jobId 定时任务ID
     */
    Boolean start(Integer jobId);

    /**
     * 删除定时任务
     *
     * @param jobId 定时任务ID
     */
    Boolean delete(Integer jobId);

    /**
     * 立即触发定时任务（一次）
     *
     * @param jobId 定时任务ID
     */
    Boolean trig(Integer jobId);
}
