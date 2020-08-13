package com.zbkj.crmeb.wechat.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.wechat.request.RoutineQrcodeRequest;
import com.zbkj.crmeb.wechat.request.RoutineQrcodeSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.wechat.service.RoutineQrcodeService;
import com.zbkj.crmeb.wechat.model.RoutineQrcode;


/**
 * 小程序二维码管理表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/wechat/routine/qrcode")
@Api(tags = "微信开放平台 -- 小程序二维码管理")
public class RoutineQrcodeController {

    @Autowired
    private RoutineQrcodeService routineQrcodeService;

    /**
     * 分页显示小程序二维码管理表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<RoutineQrcode>>  getList(@Validated RoutineQrcodeSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<RoutineQrcode> routineQrcodeCommonPage = CommonPage.restPage(routineQrcodeService.getList(request, pageParamRequest));
        return CommonResult.success(routineQrcodeCommonPage);
    }

    /**
     * 新增小程序二维码管理表
     * @param routineQrcodeRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated RoutineQrcodeRequest routineQrcodeRequest){
        RoutineQrcode routineQrcode = new RoutineQrcode();
        BeanUtils.copyProperties(routineQrcodeRequest, routineQrcode);

        if(routineQrcodeService.save(routineQrcode)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除小程序二维码管理表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(routineQrcodeService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改小程序二维码管理表
     * @param id integer id
     * @param routineQrcodeRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated RoutineQrcodeRequest routineQrcodeRequest){
        RoutineQrcode routineQrcode = new RoutineQrcode();
        BeanUtils.copyProperties(routineQrcodeRequest, routineQrcode);
        routineQrcode.setId(id);

        if(routineQrcodeService.updateById(routineQrcode)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询小程序二维码管理表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<RoutineQrcode> info(@RequestParam(value = "id") Integer id){
        RoutineQrcode routineQrcode = routineQrcodeService.getById(id);
        return CommonResult.success(routineQrcode);
   }
}



