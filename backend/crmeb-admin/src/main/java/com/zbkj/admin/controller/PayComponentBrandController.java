package com.zbkj.admin.controller;

import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PayComponentBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 组件品牌表 前端控制器
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
@RequestMapping("api/admin/pay/component/brand")
@Api(tags = "自定义交易组件—组件品牌") //配合swagger使用
public class PayComponentBrandController {

    @Autowired
    private PayComponentBrandService payComponentBrandService;

    /**
     * 获取品牌
     * 获取到的是已申请成功的品牌列表
     *
     */
    @ApiOperation(value = "获取品牌（测试用，前端不调用）")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public CommonResult<Object> get(){
        payComponentBrandService.updateData();
        return CommonResult.success();
    }
}



