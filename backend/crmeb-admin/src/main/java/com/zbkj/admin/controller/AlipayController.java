package com.zbkj.admin.controller;

import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.AliPayService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("api/public/alipay")
@Api(tags = "支付宝支付管理")
public class AlipayController {

    @Autowired
    private AliPayService aliPayService;

    /**
     * 统一收单交易退款查询
     * 对应官方例子   alipay.trade.page.pay.jsp
     */
    @RequestMapping(value = "/refundQueryAlipay", method = RequestMethod.GET)
    public CommonResult<Object> refundQueryAlipay(@RequestParam String orderNo) {
        if (aliPayService.queryRefund(orderNo)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}
