package com.zbkj.crmeb.system.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.system.request.SystemNoticeRequest;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.system.service.SystemNoticeService;
import com.zbkj.crmeb.system.model.SystemNotice;


/**
 * 通知模板表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/system/notice")
@Api(tags = "设置 -- 通知记录 -- 通知模板")
public class SystemNoticeController {

    @Autowired
    private SystemNoticeService systemNoticeService;

    /**
     * 分页显示通知模板表
     * @param systemNoticeRequest 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemNotice>>  getList(@ModelAttribute SystemNoticeRequest systemNoticeRequest, @ModelAttribute PageParamRequest pageParamRequest){
        SystemNotice systemNotice = new SystemNotice();
        BeanUtils.copyProperties(systemNoticeRequest, systemNotice);
        CommonPage<SystemNotice> systemNoticeCommonPage = CommonPage.restPage(systemNoticeService.getList(systemNoticeRequest, pageParamRequest));
        return CommonResult.success(systemNoticeCommonPage);
    }

    /**
     * 新增通知模板表
     * @param systemNoticeRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@ModelAttribute SystemNoticeRequest systemNoticeRequest){
        SystemNotice systemNotice = new SystemNotice();
        BeanUtils.copyProperties(systemNoticeRequest, systemNotice);

        if(systemNoticeService.save(systemNotice)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除通知模板表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(systemNoticeService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改通知模板表
     * @param id integer id
     * @param systemNoticeRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @ModelAttribute SystemNoticeRequest systemNoticeRequest){
        SystemNotice systemNotice = new SystemNotice();
        BeanUtils.copyProperties(systemNoticeRequest, systemNotice);
        systemNotice.setId(id);

        if(systemNoticeService.updateById(systemNotice)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询通知模板表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemNotice> info(@RequestParam(value = "id") Integer id){
        SystemNotice systemNotice = systemNoticeService.getById(id);
        return CommonResult.success(systemNotice);
   }
}



