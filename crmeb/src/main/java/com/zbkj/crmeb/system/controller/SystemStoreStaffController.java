package com.zbkj.crmeb.system.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemStoreStaff;
import com.zbkj.crmeb.system.response.SystemStoreStaffResponse;
import com.zbkj.crmeb.system.request.SystemStoreStaffRequest;
import com.zbkj.crmeb.system.service.SystemStoreStaffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 门店店员表 前端控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/system/store/staff")
@Api(tags = "设置 -- 提货点 -- 核销员")
public class SystemStoreStaffController {

    @Autowired
    private SystemStoreStaffService systemStoreStaffService;

    /**
     * 分页显示门店店员表
     * @param storeId 门店id
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemStoreStaffResponse>>  getList(@RequestParam(name = "storeId", required = false, defaultValue = "0") Integer storeId,
                                                                       @ModelAttribute PageParamRequest pageParamRequest){
        CommonPage<SystemStoreStaffResponse> systemStoreStaffCommonPage =
                CommonPage.restPage(systemStoreStaffService.getList(storeId, pageParamRequest));
        return CommonResult.success(systemStoreStaffCommonPage);
    }

    /**
     * 新增门店店员表
     * @param systemStoreStaffRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @ModelAttribute SystemStoreStaffRequest systemStoreStaffRequest){
        if(systemStoreStaffService.saveUnique(systemStoreStaffRequest)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除门店店员表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(systemStoreStaffService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改门店店员表
     * @param id integer id
     * @param systemStoreStaffRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @ModelAttribute SystemStoreStaffRequest systemStoreStaffRequest){
        SystemStoreStaff systemStoreStaff = new SystemStoreStaff();
        BeanUtils.copyProperties(systemStoreStaffRequest, systemStoreStaff);
        systemStoreStaff.setId(id);

        if(systemStoreStaffService.updateById(systemStoreStaff)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改门店店员表
     * @param id integer id
     * @param status 状态
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "修改状态")
    @RequestMapping(value = "/update/status", method = RequestMethod.GET)
    public CommonResult<String> updateStatus(@RequestParam Integer id, @RequestParam Integer status){
        SystemStoreStaff systemStoreStaff = new SystemStoreStaff();
        systemStoreStaff.setId(id);
        systemStoreStaff.setStatus(status);
        if(systemStoreStaffService.updateById(systemStoreStaff)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询门店店员表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemStoreStaff> info(@RequestParam(value = "id") Integer id){
        SystemStoreStaff systemStoreStaff = systemStoreStaffService.getById(id);
        return CommonResult.success(systemStoreStaff);
   }
}



