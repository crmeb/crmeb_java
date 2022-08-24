package com.zbkj.admin.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.model.system.SystemStore;
import com.zbkj.common.request.SystemStoreRequest;
import com.zbkj.common.request.SystemStoreSearchRequest;
import com.zbkj.service.service.SystemStoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


/**
 * 门店自提 前端控制器
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
@RequestMapping("api/admin/system/store")
@Api(tags = "设置 -- 提货点 -- 提货点")
public class SystemStoreController {

    @Autowired
    private SystemStoreService systemStoreService;

    /**
     * 分页显示门店自提
     * @param request SystemStoreSearchRequest 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:system:store:list')")
    @ApiOperation(value = "门店自提分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemStore>>  getList(@Validated SystemStoreSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        CommonPage<SystemStore> expressCommonPage = CommonPage.restPage(systemStoreService.getList(request.getKeywords(), request.getStatus(), pageParamRequest));
        return CommonResult.success(expressCommonPage);
    }

    /**
     * 数量
     */
    @PreAuthorize("hasAuthority('admin:system:store:count')")
    @ApiOperation(value = "数量")
    @RequestMapping(value = "/getCount", method = RequestMethod.GET)
    public CommonResult<HashMap<String, Integer>>  getCount() {
        return CommonResult.success(systemStoreService.getCount());
    }

    /**
     * 新增门店自提
     * @param request SystemStoreRequest 新增参数
     */
    @PreAuthorize("hasAuthority('admin:system:store:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated SystemStoreRequest request) {
        if (systemStoreService.create(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }


    /**
     * 删除门店自提
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:system:store:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if (systemStoreService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改门店自提
     * @param id integer id
     * @param request 修改参数
     */
    @PreAuthorize("hasAuthority('admin:system:store:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated SystemStoreRequest request) {
        if (systemStoreService.update(id, request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改门店显示状态
     * @param id integer id
     * @param status 状态
     */
    @PreAuthorize("hasAuthority('admin:system:store:update:status')")
    @ApiOperation(value = "修改门店显示状态")
    @RequestMapping(value = "/update/status", method = RequestMethod.GET)
    public CommonResult<String> updateStatus(@RequestParam Integer id, @RequestParam Boolean status) {
        if (systemStoreService.updateStatus(id, status)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 门店自提详情
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:system:store:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemStore> info(@RequestParam(value = "id") Integer id) {
        return CommonResult.success(systemStoreService.getInfo(id));
    }

    /**
     * 彻底删除
     */
    @PreAuthorize("hasAuthority('admin:system:store:completely:delete')")
    @ApiOperation(value = "彻底删除")
    @RequestMapping(value = "/completely/delete", method = RequestMethod.GET)
    public CommonResult<Object> completeLyDelete(@RequestParam(value = "id") Integer id) {
        if (systemStoreService.completeLyDelete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 恢复
     */
    @PreAuthorize("hasAuthority('admin:system:store:recovery')")
    @ApiOperation(value = "提货点恢复")
    @RequestMapping(value = "/recovery", method = RequestMethod.GET)
    public CommonResult<Object> recovery(@RequestParam(value = "id") Integer id) {
        if (systemStoreService.recovery(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



