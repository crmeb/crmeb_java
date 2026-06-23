package com.zbkj.admin.controller;


import com.zbkj.common.response.TradeDataResponse;
import com.zbkj.common.response.TradingDataResponse;
import com.zbkj.common.response.TrandeTrendDateResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.TradeStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 统计 -- 交易统计
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/statistics/trade")
@Api(tags = "交易统计")
public class TradeStatisticsController {

    @Autowired
    private TradeStatisticsService statisticsService;

    @PreAuthorize("hasAuthority('admin:statistics:trade:data')")
    @ApiOperation(value = "交易统计数据")
    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public CommonResult<TradeDataResponse> getData() {
        return CommonResult.success(statisticsService.getData());
    }

    @PreAuthorize("hasAuthority('admin:statistics:trade:overview')")
    @ApiOperation(value = "交易概览")
    @RequestMapping(value = "/overview", method = RequestMethod.GET)
    public CommonResult<TradingDataResponse> getOverview(@RequestParam(value = "dateLimit", defaultValue = "") String dateLimit) {
        return CommonResult.success(statisticsService.getOverview(dateLimit));
    }

    @PreAuthorize("hasAuthority('admin:statistics:trade:trend')")
    @ApiOperation(value = "交易趋势数据")
    @RequestMapping(value = "/trend", method = RequestMethod.GET)
    public CommonResult<List<TrandeTrendDateResponse>> getTrendDataByDate(@RequestParam(value = "dateLimit") String dateLimit) {
        return CommonResult.success(statisticsService.getTrendDataByDate(dateLimit));
    }
}
