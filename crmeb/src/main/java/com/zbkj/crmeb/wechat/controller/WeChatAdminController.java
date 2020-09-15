package com.zbkj.crmeb.wechat.controller;

import com.common.CommonResult;
import com.zbkj.crmeb.wechat.service.WeChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname WeChatAdminController
 * @Description TODO
 * @Date 9/4/20 2:22 下午
 * @Created by stivepeim
 */
@Slf4j
@RestController("WeChatAdminController")
@RequestMapping("api/admin/wechat")
@Api(tags = "微信 -- 开放平台 admin")
public class WeChatAdminController {

    @Autowired
    private WeChatService weChatService;

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
}
