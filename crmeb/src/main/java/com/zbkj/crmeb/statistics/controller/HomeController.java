package com.zbkj.crmeb.statistics.controller;

import com.common.CommonResult;
import com.zbkj.crmeb.statistics.response.*;
import com.zbkj.crmeb.statistics.service.HomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * 统计 -- 主页 前端控制器
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
@RequestMapping("api/admin/statistics/home")
@Api(tags = "统计 -- 主页")
public class HomeController {

    @Autowired
    private HomeService homeService;

    /**
     * 销售额
     * @author Mr.Zhang
     * @since 2020-05-16
     */
    @ApiOperation(value = "销售额")
    @RequestMapping(value = "/sales", method = RequestMethod.GET)
    public CommonResult<HomeRateResponse> sales(){
        return CommonResult.success(homeService.sales());
    }

    /**
     * 订单量
     * @author Mr.Zhang
     * @since 2020-05-16
     */
    @ApiOperation(value = "订单量")
    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public CommonResult<HomeRateResponse> order(){
        return CommonResult.success(homeService.order());
    }


    /**
     * 新增用户
     * @author Mr.Zhang
     * @since 2020-05-16
     */
    @ApiOperation(value = "新增用户")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public CommonResult<HomeRateResponse> user(){
        return CommonResult.success(homeService.user());
    }

    /**
     * 用户访问量
     * @author Mr.Zhang
     * @since 2020-05-16
     */
    @ApiOperation(value = "用户访问量")
    @RequestMapping(value = "/views", method = RequestMethod.GET)
    public CommonResult<HomeRateResponse> views(){
        return CommonResult.success(homeService.views());
    }

    /**
     * 用户曲线图
     * @author Mr.Zhang
     * @since 2020-05-16
     */
    @ApiOperation(value = "用户曲线图")
    @RequestMapping(value = "/chart/user", method = RequestMethod.GET)
    public CommonResult<Map<Object, Object>> chartUser(){
        return CommonResult.success(homeService.chartUser());
    }

    /**
     * 用户购买统计
     * @author Mr.Zhang
     * @since 2020-05-16
     */
    @ApiOperation(value = "用户购买统计")
    @RequestMapping(value = "/chart/user/buy", method = RequestMethod.GET)
    public CommonResult<Map<String, Integer>> chartUserBuy(){
        return CommonResult.success(homeService.chartUserBuy());
    }

    /**
     * 30天订单量趋势
     * @author Mr.Zhang
     * @since 2020-05-16
     */
    @ApiOperation(value = "30天订单量趋势")
    @RequestMapping(value = "/chart/order", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> chartOrder(){
        return CommonResult.success(homeService.chartOrder());
    }

    /**
     * 周订单量趋势
     * @author Mr.Zhang
     * @since 2020-05-16
     */
    @ApiOperation(value = "周订单量趋势")
    @RequestMapping(value = "/chart/order/week", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> chartOrderInWeek(){
        return CommonResult.success(homeService.chartOrderInWeek());
    }

    /**
     * 月订单量趋势
     * @author Mr.Zhang
     * @since 2020-05-16
     */
    @ApiOperation(value = "月订单量趋势")
    @RequestMapping(value = "/chart/order/month", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> chartOrderInMonth(){
        return CommonResult.success(homeService.chartOrderInMonth());
    }

    /**
     * 年订单量趋势
     * @author Mr.Zhang
     * @since 2020-05-16
     */
    @ApiOperation(value = "年订单量趋势")
    @RequestMapping(value = "/chart/order/year", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> chartOrderInYear(){
        return CommonResult.success(homeService.chartOrderInYear());
    }
}



