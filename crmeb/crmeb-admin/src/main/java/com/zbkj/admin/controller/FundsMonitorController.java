package com.zbkj.admin.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.BrokerageRecordRequest;
import com.zbkj.common.request.FundsMonitorRequest;
import com.zbkj.common.response.MonitorResponse;
import com.zbkj.common.model.user.UserBrokerageRecord;
import com.zbkj.service.service.UserBillService;
import com.zbkj.service.service.UserFundsMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用户提现表 前端控制器
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/finance/founds/monitor")
@Api(tags = "财务 -- 资金监控")
public class FundsMonitorController {

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private UserFundsMonitorService userFundsMonitorService;

    /**
     * 分页显示资金监控
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:finance:monitor:list')")
    @ApiOperation(value = "资金监控")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<MonitorResponse>>  getList(@Validated FundsMonitorRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<MonitorResponse> userExtractCommonPage = CommonPage.restPage(userBillService.fundMonitoring(request, pageParamRequest));
        return CommonResult.success(userExtractCommonPage);
    }

    /**
     * 佣金记录
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:finance:monitor:brokerage:record')")
    @ApiOperation(value = "佣金记录")
    @RequestMapping(value = "/brokerage/record", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserBrokerageRecord>> brokerageRecord(@Validated BrokerageRecordRequest request, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(userFundsMonitorService.getBrokerageRecord(request, pageParamRequest)));
    }
}



