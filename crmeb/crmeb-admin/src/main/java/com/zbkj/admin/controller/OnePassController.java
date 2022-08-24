package com.zbkj.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.request.*;
import com.zbkj.common.response.CommonResult;
import com.zbkj.service.service.OnePassService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 一号通控制器
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
@RequestMapping("api/admin/pass")
@Api(tags = "一号通服务")
public class OnePassController {

    @Autowired
    private OnePassService onePassService;

    /**
     * 获取用户验证码
     * @param phone 手机号码
     */
    @PreAuthorize("hasAuthority('admin:pass:send:code')")
    @ApiOperation(value = "获取用户验证码")
    @RequestMapping(value = "/sendUserCode", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="phone", value="手机号"),
            @ApiImplicitParam(name="types", value="验证码类型1:修改，0:注册")
    })
    public CommonResult<Object> sendUserCode(@RequestParam(name = "phone") String phone,@RequestParam(name = "types", required = false) Integer types) {
        return CommonResult.success(onePassService.sendUserCode(phone, types));
    }

    /**
     * 账号注册
     * @param registerRequest 注册参数
     */
    @PreAuthorize("hasAuthority('admin:pass:register')")
    @ApiOperation(value = "账号注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public CommonResult<String> register(@Validated @RequestBody OnePassRegisterRequest registerRequest) {
        return CommonResult.success(onePassService.register(registerRequest));
    }

    /**
     * 一号通用户登录
     * @return
     */
    @PreAuthorize("hasAuthority('admin:pass:login')")
    @ApiOperation(value = "一号通用户登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> account(@Validated @RequestBody OnePassLoginRequest request) {
        return CommonResult.success(onePassService.login(request));
    }

    /**
     * 判断是否已经登录
     * @return
     */
    @PreAuthorize("hasAuthority('admin:pass:is:login')")
    @ApiOperation(value = "是否已经登录")
    @RequestMapping(value = "/isLogin", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> isLogin() {
        return CommonResult.success(onePassService.isLogin());
    }

    /**
     * 一号通用户信息
     */
    @PreAuthorize("hasAuthority('admin:pass:info')")
    @ApiOperation(value = "一号通用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<JSONObject> getInfo() {
        return CommonResult.success(onePassService.info());
    }

    /**
     * 注销当前登录
     * @return result
     */
    @PreAuthorize("hasAuthority('admin:pass:logout')")
    @ApiOperation(value = "注销")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public CommonResult<JSONObject> logOut() {
        if (onePassService.logOut()) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改密码
     * 手机号+验证码方式修改
     */
    @PreAuthorize("hasAuthority('admin:pass:update:password')")
    @ApiOperation(value = "修改密码")
    @RequestMapping(value = "/update/password", method = RequestMethod.POST)
    public CommonResult<JSONObject> updatePassword(@Validated @RequestBody OnePassUpdateRequest request) {
        if (onePassService.updatePassword(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改手机号——验证账号密码
     */
    @PreAuthorize("hasAuthority('admin:pass:update:phone:validator')")
    @ApiOperation(value = "修改手机号——验证账号密码")
    @RequestMapping(value = "/update/phone/validator", method = RequestMethod.POST)
    public CommonResult<JSONObject> updatePhone(@Validated @RequestBody OnePassLoginRequest request) {
        if (onePassService.beforeUpdatePhoneValidator(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改手机号
     */
    @PreAuthorize("hasAuthority('admin:pass:update:phone')")
    @ApiOperation(value = "修改手机号")
    @RequestMapping(value = "/update/phone", method = RequestMethod.POST)
    public CommonResult<JSONObject> updatePhone(@Validated @RequestBody OnePassUpdateRequest request) {
        if (onePassService.updatePhone(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 套餐列表
     * @param type 套餐类型：sms,短信；expr_query,物流查询；expr_dump,电子面单；copy,产品复制
     */
    @PreAuthorize("hasAuthority('admin:pass:meal:list')")
    @ApiOperation(value = "套餐列表")
    @RequestMapping(value = "/meal/list", method = RequestMethod.GET)
    @ApiImplicitParam(name="type", value="套餐类型：sms,短信；expr_query,物流查询；expr_dump,电子面单；copy,产品复制")
    public CommonResult<JSONObject> mealList(@Validated @RequestParam String type) {
        return CommonResult.success(onePassService.mealList(type));
    }

    /**
     * 套餐购买
     */
    @PreAuthorize("hasAuthority('admin:pass:meal:code')")
    @ApiOperation(value = "套餐购买")
    @RequestMapping(value = "/meal/code", method = RequestMethod.POST)
    public CommonResult<JSONObject> mealCode(@RequestBody @Validated MealCodeRequest request) {
        return CommonResult.success(onePassService.mealCode(request));
    }

    /**
     * 服务开通
     */
    @PreAuthorize("hasAuthority('admin:pass:service:open')")
    @ApiOperation(value = "服务开通")
    @RequestMapping(value = "/service/open", method = RequestMethod.POST)
    public CommonResult<JSONObject> serviceOpen(@RequestBody @Validated ServiceOpenRequest request) {
        if (onePassService.serviceOpen(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 用量记录
     */
    @PreAuthorize("hasAuthority('admin:pass:user:record')")
    @ApiOperation(value = "用量记录")
    @RequestMapping(value = "/user/record", method = RequestMethod.GET)
    public CommonResult<JSONObject> record(@Validated OnePassUserRecordRequest request) {
        return CommonResult.success(onePassService.userRecord(request));
    }
}
