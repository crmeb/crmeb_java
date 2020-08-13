package com.zbkj.crmeb.wechat.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.wechat.request.RoutineAccessTokenRequest;
import com.zbkj.crmeb.wechat.request.RoutineAccessTokenSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.wechat.service.RoutineAccessTokenService;
import com.zbkj.crmeb.wechat.model.RoutineAccessToken;


/**
 * 小程序access_token表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/wechat/routine/access/token")
@Api(tags = "微信开放平台 -- 小程序access_token")
public class RoutineAccessTokenController {

    @Autowired
    private RoutineAccessTokenService routineAccessTokenService;

    /**
     * 分页显示小程序access_token表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<RoutineAccessToken>>  getList(@Validated RoutineAccessTokenSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<RoutineAccessToken> routineAccessTokenCommonPage = CommonPage.restPage(routineAccessTokenService.getList(request, pageParamRequest));
        return CommonResult.success(routineAccessTokenCommonPage);
    }

    /**
     * 新增小程序access_token表
     * @param routineAccessTokenRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated RoutineAccessTokenRequest routineAccessTokenRequest){
        RoutineAccessToken routineAccessToken = new RoutineAccessToken();
        BeanUtils.copyProperties(routineAccessTokenRequest, routineAccessToken);

        if(routineAccessTokenService.save(routineAccessToken)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除小程序access_token表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(routineAccessTokenService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改小程序access_token表
     * @param id integer id
     * @param routineAccessTokenRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated RoutineAccessTokenRequest routineAccessTokenRequest){
        RoutineAccessToken routineAccessToken = new RoutineAccessToken();
        BeanUtils.copyProperties(routineAccessTokenRequest, routineAccessToken);
        routineAccessToken.setId(id);

        if(routineAccessTokenService.updateById(routineAccessToken)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询小程序access_token表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<RoutineAccessToken> info(@RequestParam(value = "id") Integer id){
        RoutineAccessToken routineAccessToken = routineAccessTokenService.getById(id);
        return CommonResult.success(routineAccessToken);
   }
}



