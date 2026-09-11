package com.zbkj.admin.controller;

import com.zbkj.common.model.record.ShoppingProductDayRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.ProductRankingRequest;
import com.zbkj.common.response.ProductRankingResponse;
import com.zbkj.common.response.ShoppingProductDataResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 统计 -- 主页 前端控制器
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
@RequestMapping("api/admin/statistics/product")
@Api(tags = "商品统计")
public class ProductStatisticsController {

    @Autowired
    private ProductStatisticsService statisticsService;

    @PreAuthorize("hasAuthority('admin:statistics:product:data')")
    @ApiOperation(value = "商品统计数据")
    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public CommonResult<ShoppingProductDataResponse> getDataByDate(@RequestParam(value = "dateLimit") String dateLimit) {
        return CommonResult.success(statisticsService.getDataByDate(dateLimit));
    }

    @PreAuthorize("hasAuthority('admin:statistics:product:ranking')")
    @ApiOperation(value = "商品排行榜")
    @RequestMapping(value = "/ranking", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductRankingResponse>> getRanking(@Validated ProductRankingRequest request) {
        return CommonResult.success(statisticsService.getRanking(request));
    }

    @PreAuthorize("hasAuthority('admin:statistics:product:trend')")
    @ApiOperation(value = "商品趋势数据")
    @RequestMapping(value = "/trend", method = RequestMethod.GET)
    public CommonResult<List<ShoppingProductDayRecord>> getTrendDataByDate(@RequestParam(value = "dateLimit") String dateLimit) {
        return CommonResult.success(statisticsService.getTrendDataByDate(dateLimit));
    }
}
