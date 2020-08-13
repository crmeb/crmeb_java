package com.zbkj.crmeb.system.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.system.request.SystemLogRequest;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.system.service.SystemLogService;
import com.zbkj.crmeb.system.model.SystemLog;


/**
 * 管理员操作记录表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/system/log")
@Api(tags = "设置 -- 管理员操作记录")
public class SystemLogController {

    @Autowired
    private SystemLogService systemLogService;

    /**
     * 分页显示管理员操作记录表
     * @param systemLogRequest 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemLog>>  getList(@ModelAttribute SystemLogRequest systemLogRequest, @ModelAttribute PageParamRequest pageParamRequest){
        SystemLog systemLog = new SystemLog();
        BeanUtils.copyProperties(systemLogRequest, systemLog);
        CommonPage<SystemLog> systemLogCommonPage = CommonPage.restPage(systemLogService.getList(systemLogRequest, pageParamRequest));
        return CommonResult.success(systemLogCommonPage);
    }

    /**
     * 新增管理员操作记录表
     * @param systemLogRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@ModelAttribute SystemLogRequest systemLogRequest){
        SystemLog systemLog = new SystemLog();
        BeanUtils.copyProperties(systemLogRequest, systemLog);

        if(systemLogService.save(systemLog)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除管理员操作记录表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(systemLogService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改管理员操作记录表
     * @param id integer id
     * @param systemLogRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @ModelAttribute SystemLogRequest systemLogRequest){
        SystemLog systemLog = new SystemLog();
        BeanUtils.copyProperties(systemLogRequest, systemLog);
        systemLog.setId(id);

        if(systemLogService.updateById(systemLog)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询管理员操作记录表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemLog> info(@RequestParam(value = "id") Integer id){
        SystemLog systemLog = systemLogService.getById(id);
        return CommonResult.success(systemLog);
   }
}



