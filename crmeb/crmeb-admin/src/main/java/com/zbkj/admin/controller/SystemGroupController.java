package com.zbkj.admin.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemGroupRequest;
import com.zbkj.common.request.SystemGroupSearchRequest;
import com.zbkj.service.service.SystemGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.zbkj.common.model.system.SystemGroup;


/**
 * 组合数据表 前端控制器
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
@RequestMapping("api/admin/system/group")
@Api(tags = "设置 -- 组合数据")
public class SystemGroupController {

    @Autowired
    private SystemGroupService systemGroupService;

    /**
     * 分页显示组合数据表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:system:group:list')")
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemGroup>>  getList(@Validated SystemGroupSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        CommonPage<SystemGroup> systemGroupCommonPage = CommonPage.restPage(systemGroupService.getList(request, pageParamRequest));
        return CommonResult.success(systemGroupCommonPage);
    }

    /**
     * 新增组合数据
     * @param systemGroupRequest 新增参数
     */
    @PreAuthorize("hasAuthority('admin:system:group:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated SystemGroupRequest systemGroupRequest) {
        if (systemGroupService.add(systemGroupRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 删除组合数据表
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:system:group:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if (systemGroupService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改组合数据表
     * @param id integer id
     * @param systemGroupRequest 修改参数
     */
    @PreAuthorize("hasAuthority('admin:system:group:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated SystemGroupRequest systemGroupRequest) {
        if (systemGroupService.edit(id, systemGroupRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 查询组合数据表信息
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:system:group:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemGroup> info(@RequestParam(value = "id") Integer id) {
        return CommonResult.success(systemGroupService.getById(id));
   }
}



