package com.zbkj.admin.controller;

import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.UserStatisticsService;
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
 * 统计 -- 用户统计
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/statistics/user")
@Api(tags = "用户统计")
public class UserStatisticsController {

    @Autowired
    private UserStatisticsService statisticsService;

    @PreAuthorize("hasAuthority('admin:statistics:user:overview')")
    @ApiOperation(value = "用户概览")
    @RequestMapping(value = "/overview", method = RequestMethod.GET)
    public CommonResult<UserOverviewResponse> getOverview(@RequestParam(value = "dateLimit", defaultValue = "") String dateLimit) {
        return CommonResult.success(statisticsService.getOverview(dateLimit));
    }

    @PreAuthorize("hasAuthority('admin:statistics:user:channel')")
    @ApiOperation(value = "用户渠道数据")
    @RequestMapping(value = "/channel", method = RequestMethod.GET)
    public CommonResult<List<UserChannelDataResponse>> getChannelData() {
        return CommonResult.success(statisticsService.getChannelData());
    }

    @PreAuthorize("hasAuthority('admin:statistics:user:overview:list')")
    @ApiOperation(value = "用户概览列表")
    @RequestMapping(value = "/overview/list", method = RequestMethod.GET)
    public CommonResult<List<UserOverviewDateResponse>> getOverviewList(@RequestParam(value = "dateLimit", defaultValue = "") String dateLimit) {
        return CommonResult.success(statisticsService.getOverviewList(dateLimit));
    }
}
