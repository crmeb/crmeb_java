package com.zbkj.crmeb.system.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.system.request.SystemUserTaskRequest;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.system.service.SystemUserTaskService;
import com.zbkj.crmeb.system.model.SystemUserTask;


/**
 * 等级任务设置 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/system/user/task")
@Api(tags = "设置 -- 会员等级 -- 等级任务")
public class SystemUserTaskController {

    @Autowired
    private SystemUserTaskService systemUserTaskService;

    /**
     * 分页显示等级任务设置
     * @param systemUserTaskRequest 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemUserTask>>  getList(@ModelAttribute SystemUserTaskRequest systemUserTaskRequest, @ModelAttribute PageParamRequest pageParamRequest){
        SystemUserTask systemUserTask = new SystemUserTask();
        BeanUtils.copyProperties(systemUserTaskRequest, systemUserTask);
        CommonPage<SystemUserTask> systemUserTaskCommonPage = CommonPage.restPage(systemUserTaskService.getList(systemUserTaskRequest, pageParamRequest));
        return CommonResult.success(systemUserTaskCommonPage);
    }

    /**
     * 新增等级任务设置
     * @param systemUserTaskRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@ModelAttribute SystemUserTaskRequest systemUserTaskRequest){
        SystemUserTask systemUserTask = new SystemUserTask();
        BeanUtils.copyProperties(systemUserTaskRequest, systemUserTask);

        if(systemUserTaskService.save(systemUserTask)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除等级任务设置
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(systemUserTaskService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改等级任务设置
     * @param id integer id
     * @param systemUserTaskRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @ModelAttribute SystemUserTaskRequest systemUserTaskRequest){
        SystemUserTask systemUserTask = new SystemUserTask();
        BeanUtils.copyProperties(systemUserTaskRequest, systemUserTask);
        systemUserTask.setId(id);

        if(systemUserTaskService.updateById(systemUserTask)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询等级任务设置信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemUserTask> info(@RequestParam(value = "id") Integer id){
        SystemUserTask systemUserTask = systemUserTaskService.getById(id);
        return CommonResult.success(systemUserTask);
   }
}



