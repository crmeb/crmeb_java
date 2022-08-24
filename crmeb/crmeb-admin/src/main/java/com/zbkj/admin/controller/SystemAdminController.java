package com.zbkj.admin.controller;

import com.zbkj.common.model.system.SystemAdmin;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemAdminAddRequest;
import com.zbkj.common.request.SystemAdminRequest;
import com.zbkj.common.request.SystemAdminUpdateRequest;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.response.SystemAdminResponse;
import com.zbkj.service.service.SystemAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * 后台管理员表 前端控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/system/admin")
@Api(tags = "后台用户服务")
public class SystemAdminController {

    @Autowired
    private SystemAdminService systemAdminService;

    /**
     * 分页显示后台管理员表
     * @param systemAdminRequest 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:system:admin:list')")
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<SystemAdminResponse>> getList(@Validated SystemAdminRequest systemAdminRequest, PageParamRequest pageParamRequest) {
        CommonPage<SystemAdminResponse> systemAdminCommonPage = CommonPage.restPage(systemAdminService.getList(systemAdminRequest, pageParamRequest));
        return CommonResult.success(systemAdminCommonPage);
    }

    /**
     * 新增后台管理员
     * @param systemAdminAddRequest 新增参数
     */
    @PreAuthorize("hasAuthority('admin:system:admin:save')")
    @ApiOperation(value = "新增后台管理员")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<SystemAdminResponse> save(@RequestBody SystemAdminAddRequest systemAdminAddRequest) {
        if (systemAdminService.saveAdmin(systemAdminAddRequest)) {
            return CommonResult.success("添加管理员成功");
        }
        return CommonResult.failed("添加管理员失败");
    }

    /**
     * 删除后台管理员表
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:system:admin:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if (systemAdminService.removeById(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 修改后台管理员表
     * @param systemAdminRequest 修改参数
     */
    @PreAuthorize("hasAuthority('admin:system:admin:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody SystemAdminUpdateRequest systemAdminRequest) {
        if (systemAdminService.updateAdmin(systemAdminRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 后台管理员详情
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:system:admin:info')")
    @ApiOperation(value = "后台管理员详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemAdmin> info(@RequestParam(value = "id") @Valid Integer id) {
        return CommonResult.success(systemAdminService.getDetail(id));
    }

    /**
     * 修改后台管理员状态
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:system:admin:update:status')")
    @ApiOperation(value = "修改后台管理员状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.GET)
    public CommonResult<Object> updateStatus(@RequestParam(value = "id") @Valid Integer id, @RequestParam(value = "status") @Valid Boolean status) {
        if (systemAdminService.updateStatus(id, status)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    /**
     * 修改后台管理员是否接收状态
     */
    @PreAuthorize("hasAuthority('admin:system:admin:update:sms')")
    @ApiOperation(value = "修改后台管理员是否接收状态")
    @RequestMapping(value = "/update/isSms", method = RequestMethod.GET)
    public CommonResult<Object> updateIsSms(@RequestParam(value = "id") @Valid Integer id) {
        if (systemAdminService.updateIsSms(id)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }
}



