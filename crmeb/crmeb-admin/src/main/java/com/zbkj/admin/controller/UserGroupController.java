package com.zbkj.admin.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.model.user.UserGroup;
import com.zbkj.common.request.UserGroupRequest;
import com.zbkj.service.service.UserGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 用户分组表 前端控制器
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
@RequestMapping("api/admin/user/group")
@Api(tags = "会员 -- 分组")
public class UserGroupController {

    @Autowired
    private UserGroupService userGroupService;

    /**
     * 分页显示用户分组表
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:user:group:list')")
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserGroup>>  getList(@Validated PageParamRequest pageParamRequest) {
        CommonPage<UserGroup> userGroupCommonPage = CommonPage.restPage(userGroupService.getList(pageParamRequest));
        return CommonResult.success(userGroupCommonPage);
    }

    /**
     * 新增用户分组表
     * @param userGroupRequest 新增参数
     */
    @PreAuthorize("hasAuthority('admin:user:group:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated UserGroupRequest userGroupRequest) {
        if (userGroupService.create(userGroupRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 删除用户分组表
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:user:group:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if (userGroupService.removeById(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改用户分组表
     * @param id integer id
     * @param userGroupRequest 修改参数
     */
    @PreAuthorize("hasAuthority('admin:user:group:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated UserGroupRequest userGroupRequest) {
        if (userGroupService.edit(id, userGroupRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 查询用户分组表信息
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:user:group:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<UserGroup> info(@RequestParam(value = "id") Integer id) {
        return CommonResult.success(userGroupService.getById(id));
   }
}



