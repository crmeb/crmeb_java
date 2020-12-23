package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.request.StoreOrderStatusRequest;
import com.zbkj.crmeb.store.request.StoreOrderStatusSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.store.service.StoreOrderStatusService;
import com.zbkj.crmeb.store.model.StoreOrderStatus;


/**
 * 订单操作记录表 前端控制器
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
@RequestMapping("api/admin/store/order/status")
@Api(tags = "订单 -- 操作记录") //配合swagger使用

public class StoreOrderStatusController {

    @Autowired
    private StoreOrderStatusService storeOrderStatusService;

    /**
     * 分页显示订单操作记录表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreOrderStatus>>  getList(
            @Validated StoreOrderStatusSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreOrderStatus> storeOrderStatusCommonPage = CommonPage.restPage(storeOrderStatusService.getList(request, pageParamRequest));
        return CommonResult.success(storeOrderStatusCommonPage);
    }

    /**
     * 新增订单操作记录表
     * @param storeOrderStatusRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated StoreOrderStatusRequest storeOrderStatusRequest){
        StoreOrderStatus storeOrderStatus = new StoreOrderStatus();
        BeanUtils.copyProperties(storeOrderStatusRequest, storeOrderStatus);

        if(storeOrderStatusService.save(storeOrderStatus)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除订单操作记录表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeOrderStatusService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改订单操作记录表
     * @param id integer id
     * @param storeOrderStatusRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated StoreOrderStatusRequest storeOrderStatusRequest){
        StoreOrderStatus storeOrderStatus = new StoreOrderStatus();
        BeanUtils.copyProperties(storeOrderStatusRequest, storeOrderStatus);
        storeOrderStatus.setOid(id);

        if(storeOrderStatusService.updateById(storeOrderStatus)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询订单操作记录表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreOrderStatus> info(@RequestParam(value = "id") Integer id){
        StoreOrderStatus storeOrderStatus = storeOrderStatusService.getById(id);
        return CommonResult.success(storeOrderStatus);
   }
}



