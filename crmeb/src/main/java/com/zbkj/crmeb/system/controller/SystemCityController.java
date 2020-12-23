package com.zbkj.crmeb.system.controller;

import com.common.CommonResult;
import com.zbkj.crmeb.system.model.SystemCity;
import com.zbkj.crmeb.system.request.SystemCityRequest;
import com.zbkj.crmeb.system.request.SystemCitySearchRequest;
import com.zbkj.crmeb.system.service.SystemCityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 城市表 前端控制器
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
@RequestMapping("api/admin/system/city")
@Api(tags = "城市管理")
public class SystemCityController {

    @Autowired
    private SystemCityService systemCityService;

    /**
     * 分页显示城市表
     * @param request 搜索条件
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<Object>  getList(@Validated SystemCitySearchRequest request){
        return CommonResult.success(systemCityService.getList(request));
    }

//    /**
//     * 新增城市表
//     * @param systemCityRequest 新增参数
//     * @author Mr.Zhang
//     * @since 2020-04-17
//     */
//    @ApiOperation(value = "新增")
//    @RequestMapping(value = "/save", method = RequestMethod.POST)
//    public CommonResult<String> save(@Validated SystemCityRequest systemCityRequest){
//        SystemCity systemCity = new SystemCity();
//        BeanUtils.copyProperties(systemCityRequest, systemCity);
//
//        if(systemCityService.save(systemCity)){
//            return CommonResult.success();
//        }else{
//            return CommonResult.failed();
//        }
//    }
//
//    /**
//     * 删除城市表
//     * @param id Integer
//     * @author Mr.Zhang
//     * @since 2020-04-17
//     */
//    @ApiOperation(value = "删除")
//    @RequestMapping(value = "/delete", method = RequestMethod.GET)
//    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
//        if(systemCityService.removeById(id)){
//            return CommonResult.success();
//        }else{
//            return CommonResult.failed();
//        }
//    }

    /**
     * 修改城市
     * @param id integer id
     * @param request 修改参数
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated SystemCityRequest request){
        if(systemCityService.update(id, request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改状态
     * @param id integer id
     * @param status boolean 状态
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "修改状态")
    @RequestMapping(value = "/update/status", method = RequestMethod.POST)
    public CommonResult<String> updateStatus(@RequestParam Integer id, @RequestParam Boolean status){
        if(systemCityService.updateStatus(id, status)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询城市表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemCity> info(@RequestParam(value = "id") Integer id){
        SystemCity systemCity = systemCityService.getById(id);
        return CommonResult.success(systemCity);
    }

    /**
     * 获取tree结构的列表
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    @ApiOperation(value = "获取tree结构的列表")
    @RequestMapping(value = "/list/tree", method = RequestMethod.GET)
    public CommonResult<Object> getListTree(){
        return CommonResult.success(systemCityService.getListTree());
    }
}



