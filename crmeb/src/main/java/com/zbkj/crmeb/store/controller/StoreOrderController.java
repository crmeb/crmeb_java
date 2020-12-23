package com.zbkj.crmeb.store.controller;

import com.common.CommonResult;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.zbkj.crmeb.express.vo.ExpressSheetVo;
import com.zbkj.crmeb.express.vo.LogisticsResultVo;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.request.*;
import com.zbkj.crmeb.store.response.StoreOrderInfoResponse;
import com.zbkj.crmeb.store.response.StoreOrderResponse;
import com.zbkj.crmeb.store.response.StoreStaffDetail;
import com.zbkj.crmeb.store.response.StoreStaffTopDetail;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.store.service.StoreOrderVerification;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单表 前端控制器
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
@RequestMapping("api/admin/store/order")
@Api(tags = "订单") //配合swagger使用
public class StoreOrderController {

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderVerification storeOrderVerification;

    /**
     * 分页显示订单表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<StoreOrderResponse>  getList(
            @Validated StoreOrderSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(storeOrderService.getAdminList(request, pageParamRequest));
    }


    /**
     * 删除订单表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        StoreOrder storeOrder = storeOrderService.getById(id);
        if(!storeOrder.getIsDel()){
            return CommonResult.failed("您选择的的订单存在用户未删除的订单，无法删除用户未删除的订单！。");
        }
        if(storeOrder.getIsSystemDel()){
            return CommonResult.failed("此订单已经被删除了。");
        }
        storeOrder.setIsSystemDel(true);
        if(storeOrderService.updateById(storeOrder)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 备注订单
     * @param id integer id
     * @param mark String 备注
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "备注")
    @RequestMapping(value = "/mark", method = RequestMethod.POST)
    public CommonResult<String> mark(@RequestParam Integer id, @RequestParam String mark){
        if(storeOrderService.mark(id, mark)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改订单表
     * @param id integer id
     * @param request String 备注
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "修改订单")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated StoreOrderRequest request){
        StoreOrder storeOrder = new StoreOrder();
        BeanUtils.copyProperties(request, storeOrder);
        storeOrder.setId(id);
        if(storeOrderService.updateById(storeOrder)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询订单表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreOrderInfoResponse> info(@RequestParam(value = "id") Integer id){
        return CommonResult.success(storeOrderService.info(id));
    }

    /**
     * 发送货
     * @param request StoreOrderSendRequest 发货参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "发送货")
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public CommonResult<Boolean> send(@RequestBody @Validated StoreOrderSendRequest request){
        storeOrderService.send(request);
        return CommonResult.success();
    }

    /**
     * 退款
     * @param request StoreOrderRefundRequest 退款参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "退款")
    @RequestMapping(value = "/refund", method = RequestMethod.GET)
    public CommonResult<Boolean> send(@Validated StoreOrderRefundRequest request){
        return CommonResult.success(storeOrderService.refund(request));
    }

    /**
     * 退款
     * @param id Integer 订单id
     * @param reason String 原因
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "拒绝退款")
    @RequestMapping(value = "/refund/refuse", method = RequestMethod.GET)
    public CommonResult<Boolean> refundRefuse(@RequestParam Integer id, @RequestParam String reason){
        if(StringUtils.isBlank(reason)){
            CommonResult.validateFailed("请填写退款原因");
        }
        return CommonResult.success(storeOrderService.refundRefuse(id, reason));
    }

    /**
     * 快递查询
     * @param id Integer 订单id
     * @author Mr.Zhang
     * @since 2020-07-06
     */
    @ApiOperation(value = "快递查询")
    @RequestMapping(value = "/getLogisticsInfo", method = RequestMethod.GET)
    public CommonResult<LogisticsResultVo> getLogisticsInfo(@RequestParam(value = "id") Integer id){
        return CommonResult.success(storeOrderService.getLogisticsInfo(id));
    }

    /**
     * 核销订单头部数据
     * @author stivepeim
     * @since 2020-08-29
     */
    @ApiOperation(value = "核销订单头部数据")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public CommonResult<StoreStaffTopDetail> getStatistics(){
        return CommonResult.success(storeOrderVerification.getOrderVerificationData());
    }

    /**
     * 核销订单头部数据
     * @author stivepeim
     * @since 2020-08-29
     */
    @ApiOperation(value = "核销订单 月列表数据")
    @RequestMapping(value = "/statisticsData", method = RequestMethod.GET)
    public CommonResult<List<StoreStaffDetail>> getStaffDetail(StoreOrderStaticsticsRequest request){
        return CommonResult.success(storeOrderVerification.getOrderVerificationDetail(request));
    }


    /**
     * 核销码核销订单
     * @author stivepeim
     * @since 2020-09-01
     */
    @ApiOperation(value = "核销码核销订单")
    @RequestMapping(value = "/writeUpdate/{vCode}", method = RequestMethod.GET)
    public CommonResult<Object> verificationOrder(
            @PathVariable String vCode){
        return CommonResult.success(storeOrderVerification.verificationOrderByCode(vCode));
    }

    /**
     * 核销码核销订单
     * @author stivepeim
     * @since 2020-09-01
     */
    @ApiOperation(value = "核销码查询待核销订单")
    @RequestMapping(value = "/writeConfirm/{vCode}", method = RequestMethod.GET)
    public CommonResult<Object> verificationConfirmOrder(
            @PathVariable String vCode){
        return CommonResult.success(storeOrderVerification.getVerificationOrderByCode(vCode));
    }

    /**
     *  一键改价
     * @author stivepeim
     * @since 2020-09-01
     */
    @ApiOperation(value = "一键改价")
    @RequestMapping(value = "/editPrice", method = RequestMethod.GET)
    public CommonResult<Object> editOrderPrice(
            @RequestParam String orderId, @RequestParam(required = true,defaultValue = "0") BigDecimal price){
        try {
            if(price.compareTo(BigDecimal.ZERO) < 0) throw new CrmebException("请输入合法参数");
        }catch (Exception e){
            throw new CrmebException("价格参数错误");
        }
        return CommonResult.success(storeOrderService.editPrice(orderId,price));
    }

    /**
     *  订单统计详情
     * @author stivepeim
     * @since 2020-09-01
     */
    @ApiOperation(value = "订单统计详情")
    @RequestMapping(value = "/time", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateLimit", value = "today,yesterday,lately7,lately30,month,year,/yyyy-MM-dd hh:mm:ss,yyyy-MM-dd hh:mm:ss/",
                    dataType = "String",  required = true),
            @ApiImplicitParam(name = "type", value="1=price 2=order",  required = true)
    })
    public CommonResult<Object> statisticsOrderTime(@RequestParam String dateLimit,
                                                    @RequestParam Integer type){
        return CommonResult.success(storeOrderService.orderStatisticsByTime(dateLimit,type));
    }

    /**
     * 获取面单默认配置信息
     * @return
     */
    @ApiOperation(value = "获取面单默认配置信息")
    @RequestMapping(value = "/sheet/info", method = RequestMethod.GET)
    public CommonResult<ExpressSheetVo> getDeliveryInfo() {
        return CommonResult.success(storeOrderService.getDeliveryInfo());
    }
}



