package com.zbkj.admin.controller;

import com.zbkj.common.response.CommonResult;
import com.zbkj.service.service.TemplateMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 微信模板 前端控制器
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
@RequestMapping("api/admin/wechat/template")
@Api(tags = "微信 -- 消息模版") //配合swagger使用
public class TemplateMessageController {

    @Autowired
    private TemplateMessageService templateMessageService;

    /**
     * 公众号模板消息同步
     */
    @PreAuthorize("hasAuthority('admin:wechat:whcbqhn:sync')")
    @ApiOperation(value = "公众号模板消息同步")
    @RequestMapping(value = "/whcbqhn/sync", method = RequestMethod.POST)
    public CommonResult<String> whcbqhnSync() {
        if (templateMessageService.whcbqhnSync()) {
            return CommonResult.success("公众号模板消息同步成功");
        }
        return CommonResult.failed("公众号模板消息同步失败");
    }

    /**
     * 小程序订阅消息同步
     */
    @PreAuthorize("hasAuthority('admin:wechat:routine:sync')")
    @ApiOperation(value = "小程序订阅消息同步")
    @RequestMapping(value = "/routine/sync", method = RequestMethod.POST)
    public CommonResult<String> routineSync() {
        if (templateMessageService.routineSync()) {
            return CommonResult.success("小程序订阅消息同步成功");
        }
        return CommonResult.failed("小程序订阅消息同步失败");
    }
}



