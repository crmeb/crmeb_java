package com.zbkj.crmeb.user.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.user.request.UserTagRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.user.service.UserTagService;
import com.zbkj.crmeb.user.model.UserTag;


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
@RequestMapping("api/admin/user/tag")
@Api(tags = "会员 -- 标签") //配合swagger使用

public class UserTagController {

    @Autowired
    private UserTagService userTagService;

    /**
     * 分页显示用户分组表
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-06-05
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserTag>>  getList(@Validated PageParamRequest pageParamRequest){
        CommonPage<UserTag> userTagCommonPage = CommonPage.restPage(userTagService.getList(pageParamRequest));
        return CommonResult.success(userTagCommonPage);
    }

    /**
     * 新增用户分组表
     * @param userTagRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-06-05
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated UserTagRequest userTagRequest){
        UserTag userTag = new UserTag();
        BeanUtils.copyProperties(userTagRequest, userTag);

        if(userTagService.save(userTag)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除用户分组表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-06-05
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(userTagService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改用户分组表
     * @param id integer id
     * @param userTagRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-06-05
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated UserTagRequest userTagRequest){
        UserTag userTag = new UserTag();
        BeanUtils.copyProperties(userTagRequest, userTag);
        userTag.setId(id);

        if(userTagService.updateById(userTag)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询用户分组表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-06-05
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<UserTag> info(@RequestParam(value = "id") Integer id){
        UserTag userTag = userTagService.getById(id);
        return CommonResult.success(userTag);
   }
}



