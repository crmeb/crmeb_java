package com.zbkj.admin.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.UserTagRequest;
import com.zbkj.service.service.UserTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.zbkj.common.model.user.UserTag;


/**
 * 用户标签 前端控制器
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
@RequestMapping("api/admin/user/tag")
@Api(tags = "会员 -- 标签") //配合swagger使用
public class UserTagController {

    @Autowired
    private UserTagService userTagService;

    /**
     * 分页显示用户分标签
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:user:tag:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserTag>>  getList(@Validated PageParamRequest pageParamRequest) {
        CommonPage<UserTag> userTagCommonPage = CommonPage.restPage(userTagService.getList(pageParamRequest));
        return CommonResult.success(userTagCommonPage);
    }

    /**
     * 新增用户分标签
     * @param userTagRequest 新增参数
     */
    @PreAuthorize("hasAuthority('admin:user:tag:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated UserTagRequest userTagRequest) {
        if (userTagService.create(userTagRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 删除用户分标签
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:user:tag:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if (userTagService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改用户标签
     * @param id integer id
     * @param userTagRequest 修改参数
     */
    @PreAuthorize("hasAuthority('admin:user:tag:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated UserTagRequest userTagRequest) {
        if (userTagService.updateTag(id, userTagRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 查询用户标签
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:user:tag:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<UserTag> info(@RequestParam(value = "id") Integer id) {
        return CommonResult.success(userTagService.getById(id));
   }
}



