package com.zbkj.crmeb.system.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemUserLevel;
import com.zbkj.crmeb.system.request.SystemUserLevelRequest;
import com.zbkj.crmeb.system.request.SystemUserLevelSearchRequest;
import com.zbkj.crmeb.system.service.SystemUserLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 设置用户等级表 前端控制器
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
@RequestMapping("api/admin/system/user/level")
@Api(tags = "设置 -- 会员等级")
public class SystemUserLevelController {

    @Autowired
    private SystemUserLevelService systemUserLevelService;

    /**
     * 分页显示设置用户等级表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemUserLevel>>  getList(@ModelAttribute SystemUserLevelSearchRequest request, @ModelAttribute PageParamRequest pageParamRequest){
        CommonPage<SystemUserLevel> systemUserLevelCommonPage = CommonPage.restPage(systemUserLevelService.getList(request, pageParamRequest));
        return CommonResult.success(systemUserLevelCommonPage);
    }

    /**
     * 新增设置用户等级表
     * @param request SystemUserLevelRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated SystemUserLevelRequest request){
        if(systemUserLevelService.create(request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除设置用户等级表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        SystemUserLevel systemUserLevel = new SystemUserLevel();
        systemUserLevel.setId(id);
        systemUserLevel.setIsDel(true);
        if(systemUserLevelService.updateById(systemUserLevel)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改设置用户等级表
     * @param id integer id
     * @param request 修改参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id,
                                       @RequestBody @Validated SystemUserLevelRequest request){
        if(systemUserLevelService.update(id, request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询设置用户等级表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemUserLevel> info(@RequestParam(value = "id") Integer id){
        SystemUserLevel systemUserLevel = systemUserLevelService.getById(id);
        return CommonResult.success(systemUserLevel);
    }

    /**
     * 是否使用
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "是否使用")
    @RequestMapping(value = "/use", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="id", dataType = "Integer", required = true),
            @ApiImplicitParam(name="value", value="是否使用, 1=是，0=否", dataType = "Boolean", required = true)
    })
    public CommonResult<String> use(@RequestParam(value = "id") Integer id,
                                    @RequestParam(value = "value") Boolean value){
        SystemUserLevel systemUserLevel = new SystemUserLevel();
        systemUserLevel.setId(id);
        systemUserLevel.setIsShow(value);
        if(systemUserLevelService.updateById(systemUserLevel)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }
}



