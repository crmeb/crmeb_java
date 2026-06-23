package com.zbkj.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.request.SaveConfigRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.CommonSeparateConfigVo;
import com.zbkj.service.service.WechatNewService;
import com.zbkj.service.service.WechatPublicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信缓存表 前端控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/wechat/menu")
@Api(tags = "微信开放平台 -- 菜单管理")
public class WeChatController {

    @Autowired
    private WechatPublicService wechatPublicService;
    @Autowired
    private WechatNewService wechatNewService;

    /**
     * 获取微信菜单
     */
    @PreAuthorize("hasAuthority('admin:wechat:menu:public:get')")
    @ApiOperation(value = "获取自定义菜单")
    @RequestMapping(value = "/public/get", method = RequestMethod.GET)
    public CommonResult<Object> get() {
        return CommonResult.success(wechatPublicService.getCustomizeMenus());
    }

    /**
     * 创建微信菜单
     * @param data 菜单数据，具体json格式参考微信开放平台
     */
    @PreAuthorize("hasAuthority('admin:wechat:menu:public:create')")
    @ApiOperation(value = "保存自定义菜单")
    @RequestMapping(value = "/public/create", method = RequestMethod.POST)
    public CommonResult<JSONObject> create(@RequestBody String data) {
        if (wechatPublicService.createMenus(data)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 删除微信菜单
     */
    @PreAuthorize("hasAuthority('admin:wechat:menu:public:delete')")
    @ApiOperation(value = "删除自定义菜单")
    @RequestMapping(value = "/public/delete", method = RequestMethod.GET)
    public CommonResult<JSONObject> delete() {
        if (wechatPublicService.deleteMenus()) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('admin:wechat:mini:shipping:switch:get')")
    @ApiOperation(value = "获取微信小程序发货开关")
    @RequestMapping(value = "/get/shipping/switch", method = RequestMethod.GET)
    public CommonResult<CommonSeparateConfigVo> getShippingSwitch() {
        return CommonResult.success(wechatNewService.getShippingSwitch());
    }

    @PreAuthorize("hasAuthority('admin:wechat:mini:shipping:switch:update')")
    @ApiOperation(value = "更新微信小程序发货开关")
    @RequestMapping(value = "/update/shipping/switch", method = RequestMethod.POST)
    public CommonResult<Object> updateShippingSwitch(@RequestBody @Validated SaveConfigRequest request) {
        wechatNewService.updateShippingSwitch(request);
        return CommonResult.success();
    }
}



