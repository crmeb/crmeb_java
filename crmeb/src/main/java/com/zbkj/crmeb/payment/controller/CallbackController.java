package com.zbkj.crmeb.payment.controller;

import com.zbkj.crmeb.payment.service.CallbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 支付回调
 */
@Slf4j
@RestController
@RequestMapping("api/admin/payment/callback")
@Api(tags = "支付回调")

public class CallbackController {

    @Autowired
    private CallbackService callbackService;

    /**
     * 微信支付回调
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @ApiOperation(value = "微信支付回调")
    @RequestMapping(value = "/wechat", method = RequestMethod.POST)
    public void weChat(@RequestBody String  request) {
        //微信支付回调
        try{
            if(callbackService.weChat(request)){
                System.out.println("<xml>\n" +
                        "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                        "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                        "</xml>");
            }
        }catch (Exception e){
            System.out.println("<xml>\n" +
                    "  <return_code><![CDATA[FAIL]]></return_code>\n" +
                    "  <return_msg><![CDATA["+e.getMessage()+"]]></return_msg>\n" +
                    "</xml>");
        }

    }

    /**
     * 支付宝支付回调
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @ApiOperation(value = "支付宝支付回调 ")
    @RequestMapping(value = "/alipay", method = RequestMethod.POST)
    public void aliPay(@RequestBody String request){
        //支付宝支付回调
        callbackService.aliPay(request);
    }
}



