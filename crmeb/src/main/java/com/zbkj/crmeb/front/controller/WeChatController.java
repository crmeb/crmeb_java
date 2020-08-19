package com.zbkj.crmeb.front.controller;

import com.common.CommonResult;
import com.zbkj.crmeb.front.response.LoginResponse;
import com.zbkj.crmeb.front.service.UserCenterService;
import com.zbkj.crmeb.user.request.RegisterThirdUserRequest;
import com.zbkj.crmeb.wechat.model.TemplateMessage;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import com.zbkj.crmeb.wechat.service.WeChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * 微信缓存表 前端控制器
 */
@Slf4j
@RestController("WeChatFrontController")
@RequestMapping("api/front/wechat")
@Api(tags = "微信 -- 开放平台")
public class WeChatController {

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private TemplateMessageService templateMessageService;

    /**
     * 获取授权页面跳转地址
     * @author Mr.Zhang
     * @since 2020-05-25
     */
    @ApiOperation(value = "获取授权页面跳转地址")
    @RequestMapping(value = "/authorize/get", method = RequestMethod.GET)
    public CommonResult<Object> get(){
        return CommonResult.success(weChatService.getAuthorizeUrl());
    }

    /**
     * 通过微信code登录
     * @author Mr.Zhang
     * @since 2020-05-25
     */
    @ApiOperation(value = "微信登录公共号授权登录")
    @RequestMapping(value = "/authorize/login", method = RequestMethod.GET)
    public CommonResult<LoginResponse> login(@RequestParam(value = "spread_spid", defaultValue = "0", required = false) Integer spreadUid,
                                             @RequestParam(value = "code") String code){
        return CommonResult.success(userCenterService.weChatAuthorizeLogin(code, spreadUid));
    }

    /**
     * 通过小程序code登录
     * @author Mr.Zhang
     * @since 2020-05-25
     */

    @ApiOperation(value = "微信登录小程序授权登录")
    @RequestMapping(value = "/authorize/program/login", method = RequestMethod.POST)
    public CommonResult<LoginResponse> programLogin(@RequestParam String code, @RequestBody @Validated RegisterThirdUserRequest request){
        return CommonResult.success(userCenterService.weChatAuthorizeProgramLogin(code, request));
    }


    /**
     * 获取微信公众号js配置
     * @author Mr.Zhang
     * @since 2020-05-25
     */
    @ApiOperation(value = "获取微信公众号js配置")
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    @ApiImplicitParam(name = "url", value = "页面地址url")
    public CommonResult<Object> configJs(@RequestParam(value = "url") String url){
        return CommonResult.success(weChatService.getJsSdkConfig(url));
    }

    /**
     * 小程序获取授权logo
     * @author Mr.Zhang
     * @since 2020-05-25
     */
    @ApiOperation(value = "小程序获取授权logo")
    @RequestMapping(value = "/getLogo", method = RequestMethod.GET)
    public CommonResult<Map<String, String>> getLogo(){
        Map<String, String> map = new HashMap<>();
        map.put("logoUrl", userCenterService.getLogo());
        return CommonResult.success(map);
    }

    /**
     * 查询微信模板信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<TemplateMessage> info(@PathVariable Integer id){
        TemplateMessage templateMessage = templateMessageService.infoException(id);
        return CommonResult.success(templateMessage);
    }
}



