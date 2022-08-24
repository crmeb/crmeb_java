package com.zbkj.admin.controller;

import com.zbkj.common.response.CommonResult;
import com.zbkj.common.response.WeChatJsSdkConfigResponse;
import com.zbkj.service.service.WechatNewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信 -- 开放平台 admin
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
@RestController("WeChatAdminController")
@RequestMapping("api/admin/wechat")
@Api(tags = "微信 -- 开放平台 admin")
public class WeChatAdminController {

    @Autowired
    private WechatNewService wechatNewService;

    /**
     * 获取微信公众号js配置
     */
    @PreAuthorize("hasAuthority('admin:wechat:config')")
    @ApiOperation(value = "获取微信公众号js配置")
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    @ApiImplicitParam(name = "url", value = "页面地址url")
    public CommonResult<WeChatJsSdkConfigResponse> configJs(@RequestParam(value = "url") String url) {
        return CommonResult.success(wechatNewService.getJsSdkConfig(url));
    }
}
