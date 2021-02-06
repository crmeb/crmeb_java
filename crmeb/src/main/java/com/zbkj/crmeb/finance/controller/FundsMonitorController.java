package com.zbkj.crmeb.finance.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.finance.model.UserFundsMonitor;
import com.zbkj.crmeb.finance.request.FundsMonitorRequest;
import com.zbkj.crmeb.finance.request.FundsMonitorUserSearchRequest;
import com.zbkj.crmeb.finance.service.UserFundsMonitorService;
import com.zbkj.crmeb.user.model.UserBrokerageRecord;
import com.zbkj.crmeb.user.response.BillType;
import com.zbkj.crmeb.user.response.UserBillResponse;
import com.zbkj.crmeb.user.service.UserBillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 用户提现表 前端控制器
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
    public CommonResult<CommonPage<UserBillResponse>>  getList(@Validated FundsMonitorRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<UserBillResponse> userExtractCommonPage = CommonPage.restPage(userBillService.fundMonitoring(request, pageParamRequest));
        return CommonResult.success(userExtractCommonPage);
    }

    /**
     * 资金监控查询参数
     * @return 资金监控查询参数集合
     */
    @ApiOperation(value = "资金监控--明细类型查询数据")
    @RequestMapping(value = "/list/option", method = RequestMethod.GET)
    public CommonResult<Object>  getList(){
        return CommonResult.success(userBillService.getSearchOption());
    }

    /**
     * 佣金记录
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-11
     */
    @ApiOperation(value = "佣金记录")
    @RequestMapping(value = "/list/user", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserFundsMonitor>>  user(@Validated FundsMonitorUserSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<UserFundsMonitor> userFundsMonitorCommonPage =
                CommonPage.restPage(userFundsMonitorService.getFundsMonitor(request, pageParamRequest));
        return CommonResult.success(userFundsMonitorCommonPage);
    }

    /**
     * 佣金详细记录
     * @param pageParamRequest  分页参数
     * @param userId    被查询的用户id
     * @return  佣金查询结果
     */
    @ApiOperation(value = "佣金详细记录")
    @RequestMapping(value = "/list/user/detail/{userId}", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserBrokerageRecord>> userDetail(PageParamRequest pageParamRequest,
                                                                    @RequestParam(value = "dateLimit", required = false) String dateLimit,
                                                                    @PathVariable Integer userId){
        CommonPage<UserBrokerageRecord> commonPage = CommonPage.restPage(userFundsMonitorService.getFundsMonitorDetail(userId, dateLimit, pageParamRequest));
        return CommonResult.success(commonPage);
    }


    /**
     * 获取资金操作类型列表
     * @return 查询结果
     */
    @ApiOperation(value="资金操作类型")
    @RequestMapping(value = "/list/billtype", method = RequestMethod.GET)
    public CommonResult<List<BillType>> getBillTypeList(){
        return CommonResult.success(userBillService.getBillType());
    }
}



