package com.zbkj.crmeb.user.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.user.request.UserGroupRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.user.service.UserGroupService;
import com.zbkj.crmeb.user.model.UserGroup;


/**
 * 用户分组表 前端控制器
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
@RequestMapping("api/admin/user/group")
@Api(tags = "会员 -- 分组")
public class UserGroupController {

    @Autowired
    private UserGroupService userGroupService;

    /**
     * 分页显示用户分组表
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserGroup>>  getList(@Validated PageParamRequest pageParamRequest){
        CommonPage<UserGroup> userGroupCommonPage = CommonPage.restPage(userGroupService.getList(pageParamRequest));
        return CommonResult.success(userGroupCommonPage);
    }

    /**
     * 新增用户分组表
     * @param userGroupRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated UserGroupRequest userGroupRequest){
        UserGroup userGroup = new UserGroup();
        BeanUtils.copyProperties(userGroupRequest, userGroup);

        if(userGroupService.save(userGroup)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除用户分组表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(userGroupService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改用户分组表
     * @param id integer id
     * @param userGroupRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated UserGroupRequest userGroupRequest){
        UserGroup userGroup = new UserGroup();
        BeanUtils.copyProperties(userGroupRequest, userGroup);
        userGroup.setId(id);

        if(userGroupService.updateById(userGroup)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询用户分组表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<UserGroup> info(@RequestParam(value = "id") Integer id){
        UserGroup userGroup = userGroupService.getById(id);
        return CommonResult.success(userGroup);
   }
}



