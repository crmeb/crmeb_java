package com.zbkj.crmeb.system.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.system.request.SystemNoticeAdminRequest;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.system.service.SystemNoticeAdminService;
import com.zbkj.crmeb.system.model.SystemNoticeAdmin;


/**
 * 通知记录表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/system/notice/admin")
@Api(tags = "设置 -- 通知记录")
public class SystemNoticeAdminController {

    @Autowired
    private SystemNoticeAdminService systemNoticeAdminService;

    /**
     * 分页显示通知记录表
     * @param systemNoticeAdminRequest 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemNoticeAdmin>>  getList(@ModelAttribute SystemNoticeAdminRequest systemNoticeAdminRequest, @ModelAttribute PageParamRequest pageParamRequest){
        SystemNoticeAdmin systemNoticeAdmin = new SystemNoticeAdmin();
        BeanUtils.copyProperties(systemNoticeAdminRequest, systemNoticeAdmin);
        CommonPage<SystemNoticeAdmin> systemNoticeAdminCommonPage = CommonPage.restPage(systemNoticeAdminService.getList(systemNoticeAdminRequest, pageParamRequest));
        return CommonResult.success(systemNoticeAdminCommonPage);
    }

    /**
     * 新增通知记录表
     * @param systemNoticeAdminRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@ModelAttribute SystemNoticeAdminRequest systemNoticeAdminRequest){
        SystemNoticeAdmin systemNoticeAdmin = new SystemNoticeAdmin();
        BeanUtils.copyProperties(systemNoticeAdminRequest, systemNoticeAdmin);

        if(systemNoticeAdminService.save(systemNoticeAdmin)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除通知记录表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(systemNoticeAdminService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改通知记录表
     * @param id integer id
     * @param systemNoticeAdminRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @ModelAttribute SystemNoticeAdminRequest systemNoticeAdminRequest){
        SystemNoticeAdmin systemNoticeAdmin = new SystemNoticeAdmin();
        BeanUtils.copyProperties(systemNoticeAdminRequest, systemNoticeAdmin);
        systemNoticeAdmin.setId(id);

        if(systemNoticeAdminService.updateById(systemNoticeAdmin)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询通知记录表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemNoticeAdmin> info(@RequestParam(value = "id") Integer id){
        SystemNoticeAdmin systemNoticeAdmin = systemNoticeAdminService.getById(id);
        return CommonResult.success(systemNoticeAdmin);
   }
}



