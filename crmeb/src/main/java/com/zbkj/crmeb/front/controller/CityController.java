package com.zbkj.crmeb.front.controller;


import com.common.CommonResult;
import com.zbkj.crmeb.system.service.SystemCityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 城市服务
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController("CityFrontController")
@RequestMapping("api/front/city")
@Api(tags = "城市服务")
public class CityController {
    @Autowired
    private SystemCityService systemCityService;

    /**
     * 城市服务树形结构数据
     * @author Mr.Zhang
     * @since 2020-06-01
     */
    @ApiOperation(value = "树形结构")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<Object> register(){
        return CommonResult.success(systemCityService.getListTree());
    }
}



