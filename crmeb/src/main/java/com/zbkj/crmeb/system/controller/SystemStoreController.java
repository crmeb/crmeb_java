package com.zbkj.crmeb.system.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.system.request.SystemStoreRequest;
import com.zbkj.crmeb.system.request.SystemStoreSearchRequest;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.system.service.SystemStoreService;
import com.zbkj.crmeb.system.model.SystemStore;

import java.util.HashMap;


/**
 * 门店自提 前端控制器
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
@RequestMapping("api/admin/system/store")
@Api(tags = "设置 -- 提货点 -- 提货点")
public class SystemStoreController {

    @Autowired
    private SystemStoreService systemStoreService;

    /**
     * 分页显示门店自提
     * @param request SystemStoreSearchRequest 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemStore>>  getList(@Validated SystemStoreSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<SystemStore> expressCommonPage = CommonPage.restPage(systemStoreService.getList(request.getKeywords(), request.getStatus(), pageParamRequest));
        return CommonResult.success(expressCommonPage);
    }

    /**
     * 数量
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "数量")
    @RequestMapping(value = "/getCount", method = RequestMethod.GET)
    public CommonResult<HashMap<String, Integer>>  getCount(){
        return CommonResult.success(systemStoreService.getCount());
    }

    /**
     * 新增门店自提
     * @param request SystemStoreRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated SystemStoreRequest request){
        if(systemStoreService.create(request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }


    /**
     * 删除门店自提
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(systemStoreService.delete(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改门店自提
     * @param id integer id
     * @param request 修改参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated SystemStoreRequest request){
        if(systemStoreService.update(id, request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改门店显示状态
     * @param id integer id
     * @param status 状态
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "修改门店显示状态")
    @RequestMapping(value = "/update/status", method = RequestMethod.GET)
    public CommonResult<String> updateStatus(@RequestParam Integer id, @RequestParam boolean status){
        if(systemStoreService.updateStatus(id, status)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询门店自提信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemStore> info(@RequestParam(value = "id") Integer id){
        SystemStore systemStore = systemStoreService.getById(id);
        systemStore.setLatitude(systemStore.getLatitude() + "," + systemStore.getLongitude());
        return CommonResult.success(systemStore);
    }

    /**
     * 彻底删除
     */
    @ApiOperation(value = "彻底删除")
    @RequestMapping(value = "/completely/delete", method = RequestMethod.GET)
    public CommonResult<Object> completeLyDelete(@RequestParam(value = "id") Integer id){
        if (systemStoreService.completeLyDelete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 恢复
     */
    @ApiOperation(value = "提货点恢复")
    @RequestMapping(value = "/recovery", method = RequestMethod.GET)
    public CommonResult<Object> recovery(@RequestParam(value = "id") Integer id){
        if (systemStoreService.recovery(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



