package com.zbkj.front.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.UserSignInfoResponse;
import com.zbkj.common.vo.SystemGroupDataSignConfigVo;
import com.zbkj.common.vo.UserSignMonthVo;
import com.zbkj.common.vo.UserSignVo;
import com.zbkj.service.service.UserSignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;


/**
 * 签到记录表 前端控制器
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
@RequestMapping("api/front/user/sign")
@Api(tags = "用户 -- 签到")
public class UserSignController {

    @Autowired
    private UserSignService userSignService;

    /**
     * 签到列表
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserSignVo>>  getList(@Validated PageParamRequest pageParamRequest) {
        CommonPage<UserSignVo> userSignCommonPage = CommonPage.restPage(userSignService.getList(pageParamRequest));
        return CommonResult.success(userSignCommonPage);
    }

    /**
     * 签到列表，年月纬度
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/month", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserSignMonthVo>>  getListGroupMonth(@Validated PageParamRequest pageParamRequest) {
        CommonPage<UserSignMonthVo> userSignCommonPage = CommonPage.restPage(userSignService.getListGroupMonth(pageParamRequest));
        return CommonResult.success(userSignCommonPage);
    }

    /**
     * 配置
     */
    @ApiOperation(value = "配置")
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public CommonResult<List<SystemGroupDataSignConfigVo>> config() {
        return CommonResult.success(userSignService.getSignConfig());
    }

    /**
     * 签到
     */
    @ApiOperation(value = "签到")
    @RequestMapping(value = "/integral", method = RequestMethod.GET)
    public CommonResult<SystemGroupDataSignConfigVo> info() {
        return CommonResult.success(userSignService.sign());
    }

    /**
     * 今日记录详情
     */
    @ApiOperation(value = "今日记录详情")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public CommonResult<HashMap<String, Object>> get() {
        return CommonResult.success(userSignService.get());
    }

    /**
     * 签到用户信息
     */
    @ApiOperation(value = "签到用户信息")
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public CommonResult<UserSignInfoResponse> getUserInfo() {
        return CommonResult.success(userSignService.getUserSignInfo());
    }
}



