package com.zbkj.admin.controller;

import com.zbkj.common.response.CommonResult;
import com.zbkj.common.model.system.SystemMenu;
import com.zbkj.common.request.SystemMenuRequest;
import com.zbkj.common.request.SystemMenuSearchRequest;
import com.zbkj.common.vo.MenuCheckVo;
import com.zbkj.service.service.SystemMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 系统菜单管理 前端控制器
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
@RequestMapping("api/admin/system/menu")
@Api(tags = "系统菜单管理")
public class SystemMenuController {

    @Autowired
    private SystemMenuService systemMenuService;

    /**
     * 菜单列表
     * @param request 搜索条件
     */
    @PreAuthorize("hasAuthority('admin:system:menu:list')")
    @ApiOperation(value = "菜单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<SystemMenu>> getList(@Validated SystemMenuSearchRequest request) {
        return CommonResult.success(systemMenuService.getAdminList(request));
    }

    /**
     * 新增菜单
     * @param systemMenuRequest 新增菜单
     */
    @PreAuthorize("hasAuthority('admin:system:menu:add')")
    @ApiOperation(value = "新增菜单")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated SystemMenuRequest systemMenuRequest) {
        if (systemMenuService.add(systemMenuRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 删除菜单
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:system:menu:delete')")
    @ApiOperation(value = "删除菜单")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id) {
        if (systemMenuService.deleteById(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("hasAuthority('admin:system:menu:update')")
    @ApiOperation(value = "修改菜单")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated SystemMenuRequest systemMenuRequest) {
        if (systemMenuService.edit(systemMenuRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 菜单详情
     */
    @PreAuthorize("hasAuthority('admin:system:menu:info')")
    @ApiOperation(value = "菜单详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<SystemMenu> info(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(systemMenuService.getInfo(id));
   }

    /**
     * 修改菜单显示状态
     */
    @PreAuthorize("hasAuthority('admin:system:menu:show:status')")
    @ApiOperation(value = "修改菜单显示状态")
    @RequestMapping(value = "/updateShowStatus/{id}", method = RequestMethod.POST)
    public CommonResult<Object> updateShowStatus(@PathVariable(value = "id") Integer id) {
        if (systemMenuService.updateShowStatus(id)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    /**
     * 菜单缓存树
     */
    @PreAuthorize("hasAuthority('admin:system:menu:cache:tree')")
    @ApiOperation(value = "菜单缓存树")
    @RequestMapping(value = "/cache/tree", method = RequestMethod.GET)
    public CommonResult<List<MenuCheckVo>> getCacheTree() {
        return CommonResult.success(systemMenuService.getCacheTree());
    }
}



