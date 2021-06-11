package com.zbkj.crmeb.front.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.zbkj.crmeb.front.request.*;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.front.service.OrderService;
import com.zbkj.crmeb.store.request.StoreProductReplyAddRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * H5端订单操作
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
@RestController("StoreOrderFrontController")
@RequestMapping("api/front/order")
@Api(tags = "订单")
public class StoreOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 预下单
     */
    @ApiOperation(value = "预下单")
    @RequestMapping(value = "/pre/order", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> preOrder(@RequestBody @Validated PreOrderRequest request) {
        MyRecord record = orderService.preOrder(request);
        return CommonResult.success(record);
    }

    /**
     * 加载预下单
     */
    @ApiOperation(value = "加载预下单")
    @RequestMapping(value = "load/pre/{preOrderNo}", method = RequestMethod.GET)
    public CommonResult<PreOrderResponse> preOrder(@PathVariable String preOrderNo) {
        return CommonResult.success(orderService.loadPreOrder(preOrderNo));
    }

    /**
     * 根据参数计算订单价格
     */
    @ApiOperation(value = "计算订单价格")
    @RequestMapping(value = "/computed/price", method = RequestMethod.POST)
    public CommonResult<ComputedOrderPriceResponse> computedPrice(@Validated @RequestBody OrderComputedPriceRequest request){
        return CommonResult.success(orderService.computedOrderPrice(request));
    }

    /**
     * 创建订单
     */
    @ApiOperation(value = "创建订单")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> createOrder(@Validated @RequestBody CreateOrderRequest orderRequest){
        return CommonResult.success(orderService.createOrder(orderRequest));
    }

    /**
     * 订单列表
     * @param type 类型
     * @param pageRequest 分页
     * @return 订单列表
     */
    @ApiOperation(value = "订单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParams ({
        @ApiImplicitParam(name = "type", value = "评价等级|0=未支付,1=待发货,2=待收货,3=待评价,4=已完成,-3=售后/退款", required = true)
    })
    public CommonResult<CommonPage<OrderDetailResponse>> orderList(
            @RequestParam(name = "type") Integer type,
            @ModelAttribute PageParamRequest pageRequest){
        return CommonResult.success(orderService.list(type, pageRequest));
    }

    /**
     * 订单详情
     * @param orderId 订单编号
     * @return 订单详情
     */
    @ApiOperation(value = "订单详情")
    @RequestMapping(value = "/detail/{orderId}", method = RequestMethod.GET)
    public CommonResult<StoreOrderDetailResponse> orderDetail(@PathVariable String orderId){
        return CommonResult.success(orderService.detailOrder(orderId));
    }

    /**
     * 订单头部信息
     * @return 查询集合数量
     */
    @ApiOperation(value = "订单头部数量")
    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public CommonResult<OrderDataResponse> orderData(){
        return CommonResult.success(orderService.orderData());
    }

    /**
     * 删除已完成订单
     * @param id String 订单号
     * @return 删除结果
     */
    @ApiOperation(value = "删除订单")
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    public CommonResult<Boolean> delete(@RequestParam Integer id){
        if( orderService.delete(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 订单评价
     * @param request StoreProductReplyAddRequest 评论参数
     */
    @ApiOperation(value = "评价订单")
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public CommonResult<Boolean> comment(@RequestBody @Validated StoreProductReplyAddRequest request){
        if(orderService.reply(request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 订单收货
     * @param id Integer 订单id
     */
    @ApiOperation(value = "订单收货")
    @RequestMapping(value = "/take", method = RequestMethod.POST)
    public CommonResult<Boolean> take(@RequestParam(value = "id") Integer id){
        if(orderService.take(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 订单取消
     * @param id Integer 订单id
     */
    @ApiOperation(value = "订单取消")
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public CommonResult<Boolean> cancel(@RequestParam(value = "id") Integer id){
        if(orderService.cancel(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 申请订单退款
     * @param orderId 订单编号
     */
    @ApiOperation(value = "订单退款申请")
    @RequestMapping(value = "/apply/refund/{orderId}", method = RequestMethod.GET)
    public CommonResult<ApplyRefundOrderInfoResponse> refundApplyOrder(@PathVariable String orderId){
        return CommonResult.success(orderService.applyRefundOrderInfo(orderId));
    }

    /**
     * 订单退款申请
     * @param request OrderRefundApplyRequest 订单id
     */
    @ApiOperation(value = "订单退款申请")
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public CommonResult<Boolean> refundApply(@RequestBody @Validated OrderRefundApplyRequest request){
        if(orderService.refundApply(request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询订单退款理由
     * @return 退款理由
     */
    @ApiOperation(value = "订单退款理由")
    @RequestMapping(value = "/refund/reason", method = RequestMethod.GET)
    public CommonResult<List<String>> refundReason(){
        return CommonResult.success(orderService.getRefundReason());
    }

    /**
     * 根据订单号查询物流信息
     * @param orderId 订单号
     * @return 物流信息
     */
    @ApiOperation(value = "物流信息查询")
    @RequestMapping(value = "/express/{orderId}", method = RequestMethod.GET)
    public CommonResult<Object> getExpressInfo(@PathVariable String orderId){
        return CommonResult.success(orderService.expressOrder(orderId));
    }

    @ApiOperation(value = "待评价商品信息查询")
    @RequestMapping(value = "/product", method = RequestMethod.POST)
    public CommonResult<OrderProductReplyResponse> getOrderProductForReply(@Validated @RequestBody GetProductReply request){
        return CommonResult.success(orderService.getReplyProduct(request));
    }
}
