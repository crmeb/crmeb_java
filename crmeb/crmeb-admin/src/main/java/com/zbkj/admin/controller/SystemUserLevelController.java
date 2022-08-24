package com.zbkj.admin.controller;

import com.zbkj.common.response.CommonResult;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.request.SystemUserLevelRequest;
import com.zbkj.common.request.SystemUserLevelUpdateShowRequest;
import com.zbkj.service.service.SystemUserLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 设置用户等级表 前端控制器
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
@RequestMapping("api/admin/system/user/level")
@Api(tags = "设置 -- 会员等级")
public class SystemUserLevelController {

    @Autowired
    private SystemUserLevelService systemUserLevelService;

    /**
     * 分页显示设置用户等级表
     */
    @PreAuthorize("hasAuthority('admin:system:user:level:list')")
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<SystemUserLevel>> getList() {
        return CommonResult.success(systemUserLevelService.getList());
    }

    /**
     * 新增等级
     */
    @PreAuthorize("hasAuthority('admin:system:user:level:save')")
    @ApiOperation(value = "新增等级")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<Object> save(@RequestBody @Validated SystemUserLevelRequest request) {
        if (systemUserLevelService.create(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 删除等级
     * @param id 等级id
     */
    @PreAuthorize("hasAuthority('admin:system:user:level:delete')")
    @ApiOperation(value = "删除等级")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<Object> delete(@PathVariable(value = "id") Integer id) {
        if (systemUserLevelService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 更新等级
     */
    @PreAuthorize("hasAuthority('admin:system:user:level:update')")
    @ApiOperation(value = "更新等级")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public CommonResult<Object> update(@PathVariable(value = "id") Integer id,
                                       @RequestBody @Validated SystemUserLevelRequest request) {
        if (systemUserLevelService.update(id, request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 使用/禁用
     */
    @PreAuthorize("hasAuthority('admin:system:user:level:use')")
    @ApiOperation(value = "使用/禁用")
    @RequestMapping(value = "/use", method = RequestMethod.POST)
    public CommonResult<Object> use(@RequestBody @Validated SystemUserLevelUpdateShowRequest request) {
        if (systemUserLevelService.updateShow(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



