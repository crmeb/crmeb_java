package com.zbkj.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.admin.model.ScheduleJobLog;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ScheduleJobLogSearchRequest;

/**
 * @author HZW
 * @description ScheduleJobLogService 接口
 * @date 2021-11-30
 */
public interface ScheduleJobLogService extends IService<ScheduleJobLog> {

    /**
     * 自动删除日志
     */
    void autoDeleteLog();

    /**
     * 日志分页列表
     *
     * @param request     搜索参数
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    PageInfo<ScheduleJobLog> findLogPageList(ScheduleJobLogSearchRequest request, PageParamRequest pageRequest);
}