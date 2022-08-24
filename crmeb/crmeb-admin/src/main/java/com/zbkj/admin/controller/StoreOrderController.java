package com.zbkj.admin.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.vo.ExpressSheetVo;
import com.zbkj.common.vo.LogisticsResultVo;
import com.zbkj.service.service.StoreOrderService;
import com.zbkj.service.service.StoreOrderVerification;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单表 前端控制器
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
@RequestMapping("api/admin/store/order")
@Api(tags = "订单") //配合swagger使用
public class StoreOrderController {

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderVerification storeOrderVerification;

    /**
     * 分页显示订单表
     *  @param request          搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:order:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreOrderDetailResponse>> getList(@Validated StoreOrderSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(storeOrderService.getAdminList(request, pageParamRequest));
    }

    /**
     * 获取订单各状态数量
     */
    @PreAuthorize("hasAuthority('admin:order:status:num')")
    @ApiOperation(value = "获取订单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<StoreOrderCountItemResponse> getOrderStatusNum(
            @RequestParam(value = "dateLimit", defaultValue = "") String dateLimit,
            @RequestParam(value = "type", defaultValue = "2") @Range(min = 0, max = 2, message = "未知的订单类型") Integer type) {
        return CommonResult.success(storeOrderService.getOrderStatusNum(dateLimit, type));
    }

    /**
     * 获取订单统计数据
     */
    @PreAuthorize("hasAuthority('admin:order:list:data')")
    @ApiOperation(value = "获取订单统计数据")
    @RequestMapping(value = "/list/data", method = RequestMethod.GET)
    public CommonResult<StoreOrderTopItemResponse> getOrderData(@RequestParam(value = "dateLimit", defaultValue = "")String dateLimit) {
        return CommonResult.success(storeOrderService.getOrderData(dateLimit));
    }


    /**
     * 订单删除
     */
    @PreAuthorize("hasAuthority('admin:order:delete')")
    @ApiOperation(value = "订单删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "orderNo") String orderNo) {
        if (storeOrderService.delete(orderNo)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 备注订单
     */
    @PreAuthorize("hasAuthority('admin:order:mark')")
    @ApiOperation(value = "备注")
    @RequestMapping(value = "/mark", method = RequestMethod.POST)
    public CommonResult<String> mark(@RequestParam String orderNo, @RequestParam String mark) {
        if (storeOrderService.mark(orderNo, mark)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 修改订单(改价)
     */
    @PreAuthorize("hasAuthority('admin:order:update:price')")
    @ApiOperation(value = "修改订单(改价)")
    @RequestMapping(value = "/update/price", method = RequestMethod.POST)
    public CommonResult<String> updatePrice(@RequestBody @Validated StoreOrderUpdatePriceRequest request) {
        if (storeOrderService.updatePrice(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 订单详情
     */
    @PreAuthorize("hasAuthority('admin:order:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreOrderInfoResponse> info(@RequestParam(value = "orderNo") String orderNo) {
        return CommonResult.success(storeOrderService.info(orderNo));
    }

    /**
     * 发送货
     */
    @PreAuthorize("hasAuthority('admin:order:send')")
    @ApiOperation(value = "发送货")
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public CommonResult<Boolean> send(@RequestBody @Validated StoreOrderSendRequest request) {
        if (storeOrderService.send(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 退款
     */
    @PreAuthorize("hasAuthority('admin:order:refund')")
    @ApiOperation(value = "退款")
    @RequestMapping(value = "/refund", method = RequestMethod.GET)
    public CommonResult<Boolean> send(@Validated StoreOrderRefundRequest request) {
        return CommonResult.success(storeOrderService.refund(request));
    }

    /**
     * 拒绝退款
     */
    @PreAuthorize("hasAuthority('admin:order:refund:refuse')")
    @ApiOperation(value = "拒绝退款")
    @RequestMapping(value = "/refund/refuse", method = RequestMethod.GET)
    public CommonResult<Object> refundRefuse(@RequestParam String orderNo, @RequestParam String reason) {
        if (storeOrderService.refundRefuse(orderNo, reason)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 快递查询
     */
    @PreAuthorize("hasAuthority('admin:order:logistics:info')")
    @ApiOperation(value = "快递查询")
    @RequestMapping(value = "/getLogisticsInfo", method = RequestMethod.GET)
    public CommonResult<LogisticsResultVo> getLogisticsInfo(@RequestParam(value = "orderNo") String orderNo) {
        return CommonResult.success(storeOrderService.getLogisticsInfo(orderNo));
    }

    /**
     * 核销订单头部数据
     *
     * @author stivepeim
     * @since 2020-08-29
     */
    @PreAuthorize("hasAuthority('admin:order:statistics')")
    @ApiOperation(value = "核销订单头部数据")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public CommonResult<StoreStaffTopDetail> getStatistics() {
        return CommonResult.success(storeOrderVerification.getOrderVerificationData());
    }

    /**
     * 核销订单 月列表数据
     *
     * @author stivepeim
     * @since 2020-08-29
     */
    @PreAuthorize("hasAuthority('admin:order:statistics:data')")
    @ApiOperation(value = "核销订单 月列表数据")
    @RequestMapping(value = "/statisticsData", method = RequestMethod.GET)
    public CommonResult<List<StoreStaffDetail>> getStaffDetail(StoreOrderStaticsticsRequest request) {
        return CommonResult.success(storeOrderVerification.getOrderVerificationDetail(request));
    }


    /**
     * 核销码核销订单
     *
     * @author stivepeim
     * @since 2020-09-01
     */
    @PreAuthorize("hasAuthority('admin:order:write:update')")
    @ApiOperation(value = "核销码核销订单")
    @RequestMapping(value = "/writeUpdate/{vCode}", method = RequestMethod.GET)
    public CommonResult<Object> verificationOrder(@PathVariable String vCode) {
        return CommonResult.success(storeOrderVerification.verificationOrderByCode(vCode));
    }

    /**
     * 核销码查询待核销订单
     *
     * @author stivepeim
     * @since 2020-09-01
     */
    @PreAuthorize("hasAuthority('admin:order:write:confirm')")
    @ApiOperation(value = "核销码查询待核销订单")
    @RequestMapping(value = "/writeConfirm/{vCode}", method = RequestMethod.GET)
    public CommonResult<Object> verificationConfirmOrder(
            @PathVariable String vCode) {
        return CommonResult.success(storeOrderVerification.getVerificationOrderByCode(vCode));
    }

    /**
     * 订单统计详情
     *
     * @author stivepeim
     * @since 2020-09-01
     */
    @PreAuthorize("hasAuthority('admin:order:time')")
    @ApiOperation(value = "订单统计详情")
    @RequestMapping(value = "/time", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateLimit", value = "today,yesterday,lately7,lately30,month,year,/yyyy-MM-dd hh:mm:ss,yyyy-MM-dd hh:mm:ss/",
                    dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "1=price 2=order", required = true)
    })
    public CommonResult<Object> statisticsOrderTime(@RequestParam String dateLimit,
                                                    @RequestParam Integer type) {
        return CommonResult.success(storeOrderService.orderStatisticsByTime(dateLimit, type));
    }

    /**
     * 获取面单默认配置信息
     */
    @PreAuthorize("hasAuthority('admin:order:sheet:info')")
    @ApiOperation(value = "获取面单默认配置信息")
    @RequestMapping(value = "/sheet/info", method = RequestMethod.GET)
    public CommonResult<ExpressSheetVo> getDeliveryInfo() {
        return CommonResult.success(storeOrderService.getDeliveryInfo());
    }

}



