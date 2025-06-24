package com.zbkj.admin.controller;

import com.zbkj.admin.model.ScheduleJob;
import com.zbkj.admin.model.ScheduleJobLog;
import com.zbkj.admin.service.ScheduleJobLogService;
import com.zbkj.admin.service.ScheduleJobService;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ScheduleJobLogSearchRequest;
import com.zbkj.common.request.ScheduleJobRequest;
import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 定时任务控制器
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
@Slf4j
@RestController
@RequestMapping("api/admin/schedule/job")
@Api(tags = "定时任务控制器")
public class ScheduleJobController {

    @Autowired
    private ScheduleJobService scheduleJobService;
    @Autowired
    private ScheduleJobLogService scheduleJobLogService;

    @PreAuthorize("hasAuthority('admin:schedule:job:list')")
    @ApiOperation(value = "定时任务列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<ScheduleJob>> getList() {
        return CommonResult.success(scheduleJobService.getAll());
    }

    @PreAuthorize("hasAuthority('admin:schedule:job:add')")
    @ApiOperation(value = "添加定时任务")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated ScheduleJobRequest request) {
        if (scheduleJobService.add(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('admin:schedule:job:update')")
    @ApiOperation(value = "定时任务编辑")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ScheduleJobRequest request) {
        if (scheduleJobService.edit(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('admin:schedule:job:suspend')")
    @ApiOperation(value = "暂停定时任务")
    @RequestMapping(value = "/suspend/{jobId}", method = RequestMethod.POST)
    public CommonResult<String> suspend(@PathVariable(value = "jobId") Integer jobId) {
        if (scheduleJobService.suspend(jobId)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('admin:schedule:job:start')")
    @ApiOperation(value = "启动定时任务")
    @RequestMapping(value = "/start/{jobId}", method = RequestMethod.POST)
    public CommonResult<String> start(@PathVariable(value = "jobId") Integer jobId) {
        if (scheduleJobService.start(jobId)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('admin:schedule:job:delete')")
    @ApiOperation(value = "删除定时任务")
    @RequestMapping(value = "/delete/{jobId}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "jobId") Integer jobId) {
        if (scheduleJobService.delete(jobId)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('admin:schedule:job:trig')")
    @ApiOperation(value = "立即执行定时任务（一次）")
    @RequestMapping(value = "/trig/{jobId}", method = RequestMethod.POST)
    public CommonResult<String> trig(@PathVariable(value = "jobId") Integer jobId) {
        if (scheduleJobService.trig(jobId)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('admin:schedule:job:log:list')")
    @ApiOperation(value = "定时任务日志分页列表")
    @RequestMapping(value = "/log/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ScheduleJobLog>> getLogList(@Validated ScheduleJobLogSearchRequest request,
                                                               @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(scheduleJobLogService.findLogPageList(request, pageParamRequest)));
    }
}



