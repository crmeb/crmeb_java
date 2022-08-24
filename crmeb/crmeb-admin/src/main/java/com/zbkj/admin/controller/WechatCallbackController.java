package com.zbkj.admin.controller;

import com.zbkj.service.service.WechatCallbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * 微信小程序回调
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
@RequestMapping("api/public/wechat/mini/callback")
@Api(tags = "微信开放平台 -- 小程序回调")
public class WechatCallbackController {

    @Autowired
    private WechatCallbackService wechatCallbackService;

    /**
     * 小程序回调-自己模拟
     */
    @ApiOperation(value = "小程序回调-自己模拟")
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String test(@RequestBody String request) {
        return wechatCallbackService.callback(request);
    }

    /**
     * 小程序回调
     * 目前只用于视频号直播
     */
    @ApiOperation(value = "小程序回调")
    @RequestMapping(value = "/index", method = RequestMethod.POST)
    public String webHook(@RequestBody String request) {
       return wechatCallbackService.callback(request);
    }

    /**
     * 微信推送地址验证
     * @param signature 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param echostr 随机字符串
     */
    @RequestMapping(value = { "/index" }, method = RequestMethod.GET)
    private void webHook(
            @RequestParam(value = "signature", required = false) String signature,
            @RequestParam(value = "timestamp", required = false) String timestamp,
            @RequestParam(value = "nonce", required = false) String nonce,
            @RequestParam(value = "echostr") String echostr,
            HttpServletResponse response) throws IOException {
        System.out.println("微信小程序回调测试成功");
        PrintWriter writer = response.getWriter();
        writer.print(echostr);
        writer.flush();
        writer.close();
    }
}



