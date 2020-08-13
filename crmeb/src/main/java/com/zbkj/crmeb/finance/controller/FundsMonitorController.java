package com.zbkj.crmeb.finance.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.finance.model.UserFundsMonitor;
import com.zbkj.crmeb.finance.request.FundsMonitorSearchRequest;
import com.zbkj.crmeb.finance.request.FundsMonitorUserSearchRequest;
import com.zbkj.crmeb.finance.service.UserFundsMonitorService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 用户提现表 前端控制器
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
     * @author Mr.Zhang
     * @since 2020-05-11
     */
    @ApiOperation(value = "资金监控")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserBill>>  getList(@Validated FundsMonitorSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<UserBill> userExtractCommonPage = CommonPage.restPage(userBillService.getList(request, pageParamRequest));
        return CommonResult.success(userExtractCommonPage);
    }

    /**
     * 分页显示资金监控
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-11
     */
    @ApiOperation(value = "佣金记录")
    @RequestMapping(value = "/list/user", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserFundsMonitor>>  user(@Validated FundsMonitorUserSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<UserFundsMonitor> userFundsMonitorCommonPage = CommonPage.restPage(userFundsMonitorService.getFundsMonitor(request, pageParamRequest));
        return CommonResult.success(userFundsMonitorCommonPage);
    }
}



