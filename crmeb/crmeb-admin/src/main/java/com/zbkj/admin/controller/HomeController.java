package com.zbkj.admin.controller;

import com.zbkj.common.response.CommonResult;
import com.zbkj.common.response.HomeRateResponse;
import com.zbkj.service.service.HomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * 统计 -- 主页 前端控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/statistics/home")
@Api(tags = "统计 -- 主页")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @PreAuthorize("hasAuthority('admin:statistics:home:index')")
    @ApiOperation(value = "首页数据")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public CommonResult<HomeRateResponse> indexDate() {
        return CommonResult.success(homeService.indexDate());
    }

    /**
     * 用户曲线图
     */
    @PreAuthorize("hasAuthority('admin:statistics:home:chart:user')")
    @ApiOperation(value = "用户曲线图")
    @RequestMapping(value = "/chart/user", method = RequestMethod.GET)
    public CommonResult<Map<Object, Object>> chartUser() {
        return CommonResult.success(homeService.chartUser());
    }

    /**
     * 用户购买统计
     */
    @PreAuthorize("hasAuthority('admin:statistics:home:chart:user:buy')")
    @ApiOperation(value = "用户购买统计")
    @RequestMapping(value = "/chart/user/buy", method = RequestMethod.GET)
    public CommonResult<Map<String, Integer>> chartUserBuy() {
        return CommonResult.success(homeService.chartUserBuy());
    }

    /**
     * 30天订单量趋势
     */
    @PreAuthorize("hasAuthority('admin:statistics:home:chart:order')")
    @ApiOperation(value = "30天订单量趋势")
    @RequestMapping(value = "/chart/order", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> chartOrder() {
        return CommonResult.success(homeService.chartOrder());
    }

    /**
     * 周订单量趋势
     */
    @PreAuthorize("hasAuthority('admin:statistics:home:chart:order:week')")
    @ApiOperation(value = "周订单量趋势")
    @RequestMapping(value = "/chart/order/week", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> chartOrderInWeek() {
        return CommonResult.success(homeService.chartOrderInWeek());
    }

    /**
     * 月订单量趋势
     */
    @PreAuthorize("hasAuthority('admin:statistics:home:chart:order:month')")
    @ApiOperation(value = "月订单量趋势")
    @RequestMapping(value = "/chart/order/month", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> chartOrderInMonth() {
        return CommonResult.success(homeService.chartOrderInMonth());
    }

    /**
     * 年订单量趋势
     */
    @PreAuthorize("hasAuthority('admin:statistics:home:chart:order:year')")
    @ApiOperation(value = "年订单量趋势")
    @RequestMapping(value = "/chart/order/year", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> chartOrderInYear() {
        return CommonResult.success(homeService.chartOrderInYear());
    }
}



