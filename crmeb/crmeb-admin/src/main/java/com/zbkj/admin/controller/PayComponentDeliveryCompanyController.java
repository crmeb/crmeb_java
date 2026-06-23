package com.zbkj.admin.controller;

import com.zbkj.common.model.wechat.video.PayComponentDeliveryCompany;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PayComponentDeliveryCompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 组件快递公司表 前端控制器
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/pay/component/delivery/company")
@Api(tags = "自定义交易组件—组件快递公司")
public class PayComponentDeliveryCompanyController {

    @Autowired
    private PayComponentDeliveryCompanyService payComponentDeliveryCompanyService;

    /**
     * 获取组件快递
     */
    @PreAuthorize("hasAuthority('admin:pay:component:delivery:company:get')")
    @ApiOperation(value = "获取组件快递（测试用，前端不调用）")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public CommonResult<Object> get(){
        payComponentDeliveryCompanyService.updateData();
        return CommonResult.success();
    }

    /**
     * 获取组件快递列表
     */
    @PreAuthorize("hasAuthority('admin:pay:component:delivery:company:get:list')")
    @ApiOperation(value = "获取组件快递")
    @RequestMapping(value = "/get/list", method = RequestMethod.GET)
    public CommonResult<List<PayComponentDeliveryCompany>> getList(){
        return CommonResult.success(payComponentDeliveryCompanyService.getList());
    }
}



