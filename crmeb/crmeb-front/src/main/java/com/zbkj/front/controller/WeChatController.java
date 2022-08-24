package com.zbkj.front.controller;

import com.zbkj.common.model.wechat.TemplateMessage;
import com.zbkj.common.request.RegisterAppWxRequest;
import com.zbkj.common.request.RegisterThirdUserRequest;
import com.zbkj.common.request.WxBindingPhoneRequest;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.response.LoginResponse;
import com.zbkj.common.response.WeChatJsSdkConfigResponse;
import com.zbkj.front.service.UserCenterService;
import com.zbkj.service.service.SystemNotificationService;
import com.zbkj.service.service.WechatNewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 微信缓存表 前端控制器
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
@RestController("WeChatFrontController")
@RequestMapping("api/front/wechat")
@Api(tags = "微信 -- 开放平台")
public class WeChatController {

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private WechatNewService wechatNewService;

    @Autowired
    private SystemNotificationService systemNotificationService;

    /**
     * 通过微信code登录
     */
    @ApiOperation(value = "微信登录公共号授权登录")
    @RequestMapping(value = "/authorize/login", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spread_spid", value = "推荐人id", dataType = "Integer"),
            @ApiImplicitParam(name = "code", value = "code码", dataType = "String", required = true)
    })
    public CommonResult<LoginResponse> login(@RequestParam(value = "spread_spid", defaultValue = "0", required = false) Integer spreadUid,
                                             @RequestParam(value = "code") String code){
        return CommonResult.success(userCenterService.weChatAuthorizeLogin(code, spreadUid));
    }

    /**
     * 微信登录小程序授权登录
     */
    @ApiOperation(value = "微信登录小程序授权登录")
    @RequestMapping(value = "/authorize/program/login", method = RequestMethod.POST)
    public CommonResult<LoginResponse> programLogin(@RequestParam String code, @RequestBody @Validated RegisterThirdUserRequest request){
        return CommonResult.success(userCenterService.weChatAuthorizeProgramLogin(code, request));
    }

    /**
     * 微信注册绑定手机号
     */
    @ApiOperation(value = "微信注册绑定手机号")
    @RequestMapping(value = "/register/binding/phone", method = RequestMethod.POST)
    public CommonResult<LoginResponse> registerBindingPhone(@RequestBody @Validated WxBindingPhoneRequest request){
        return CommonResult.success(userCenterService.registerBindingPhone(request));
    }

    /**
     * 获取微信公众号js配置
     */
    @ApiOperation(value = "获取微信公众号js配置")
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    @ApiImplicitParam(name = "url", value = "页面地址url")
    public CommonResult<WeChatJsSdkConfigResponse> configJs(@RequestParam(value = "url") String url){
        return CommonResult.success(wechatNewService.getJsSdkConfig(url));
    }

    /**
     * 小程序获取授权logo
     */
    @ApiOperation(value = "小程序获取授权logo")
    @RequestMapping(value = "/getLogo", method = RequestMethod.GET)
    public CommonResult<Map<String, String>> getLogo(){
        Map<String, String> map = new HashMap<>();
        map.put("logoUrl", userCenterService.getLogo());
        return CommonResult.success(map);
    }

    /**
     * 订阅消息模板列表
     */
    @ApiOperation(value = "订阅消息模板列表")
    @RequestMapping(value = "/program/my/temp/list", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "支付之前：beforePay|支付成功：afterPay|申请退款：refundApply|充值之前：beforeRecharge|创建砍价：createBargain|参与拼团：pink|取消拼团：cancelPink")
    public CommonResult<List<TemplateMessage>> programMyTempList(@RequestParam(name = "type") String type){
        return CommonResult.success(systemNotificationService.getMiniTempList(type));
    }
}



