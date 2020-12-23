package com.zbkj.crmeb.wechat.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.wechat.model.WechatProgramMyTemp;
import com.zbkj.crmeb.wechat.request.WechatProgramMyTempRequest;
import com.zbkj.crmeb.wechat.request.WechatProgramMyTempSearchRequest;
import com.zbkj.crmeb.wechat.service.WechatProgramMyTempService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 小程序我的模板 前端控制器
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
@RequestMapping("api/admin/wechat/program/my/temp")
@Api(tags = "微信开放平台 -- 小程序 -- 我的模板") //配合swagger使用

public class WechatProgramMyTempController {

    @Autowired
    private WechatProgramMyTempService wechatProgramMyTempService;

    /**
     * 分页显示小程序我的模板
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-08-28
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<WechatProgramMyTemp>> getList(@Validated WechatProgramMyTempSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<WechatProgramMyTemp> wechatProgramMyTempCommonPage = CommonPage.restPage(wechatProgramMyTempService.getList(request, pageParamRequest));
        return CommonResult.success(wechatProgramMyTempCommonPage);
    }

    /**
     * 新增小程序我的模板
     * @param wechatProgramMyTempRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-08-28
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated WechatProgramMyTempRequest wechatProgramMyTempRequest){
        if(wechatProgramMyTempService.create(wechatProgramMyTempRequest)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 禁用/禁用小程序我的模板
     * @param id Integer
     * @param status boolean
     * @author Mr.Zhang
     * @since 2020-08-28
     */
    @ApiOperation(value = "禁用/禁用")
    @RequestMapping(value = "update/status", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id, @RequestParam(value = "status") boolean status){
        if(wechatProgramMyTempService.updateStatus(id, status)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 更新应用场景
     * @param id Integer
     * @param type String
     * @author Mr.Zhang
     * @since 2020-08-28
     */
    @ApiOperation(value = "禁用/禁用")
    @RequestMapping(value = "update/type", method = RequestMethod.GET)
    public CommonResult<String> updateType(@RequestParam(value = "id") Integer id, @RequestParam(value = "type") String type){
        if(wechatProgramMyTempService.updateType(id, type)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改小程序我的模板
     * @param id integer id
     * @param wechatProgramMyTempRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-08-28
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated WechatProgramMyTempRequest wechatProgramMyTempRequest){
        if(wechatProgramMyTempService.update(id, wechatProgramMyTempRequest)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询小程序我的模板信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-08-28
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<WechatProgramMyTemp> info(@RequestParam(value = "id") Integer id){
        WechatProgramMyTemp wechatProgramMyTemp = wechatProgramMyTempService.info(id);
        return CommonResult.success(wechatProgramMyTemp);
    }

    /**
     * 同步到小程序
     * @author Mr.Zhang
     * @since 2020-08-28
     */
    @ApiOperation(value = "同步到小程序")
    @RequestMapping(value = "/async", method = RequestMethod.GET)
    public CommonResult<WechatProgramMyTemp> async(){
        //查询模板信息
        List<WechatProgramMyTemp> list = wechatProgramMyTempService.getAll();
        if(null != list && list.size() > 0){
            for (WechatProgramMyTemp wechatProgramMyTemp : list) {
                wechatProgramMyTempService.async(wechatProgramMyTemp);
            }
        }
        return CommonResult.success();
    }
}



