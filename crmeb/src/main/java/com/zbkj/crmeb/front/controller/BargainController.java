package com.zbkj.crmeb.front.controller;

import com.common.CommonResult;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.bargain.response.StoreBargainResponse;
import com.zbkj.crmeb.bargain.response.StoreBargainUserHelpResponse;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserHelpService;
import com.zbkj.crmeb.front.request.BargainFrontRequest;
import com.zbkj.crmeb.front.response.BargainCountResponse;
import com.zbkj.crmeb.front.response.BargainDetailResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * H5 砍价
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/front/bargain")
@Api(tags = "砍价商品")
public class BargainController {

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private StoreBargainUserHelpService storeBargainUserHelpService;

    /**
     * 砍价商品列表
     * @return 砍价商品列表
     */
    @ApiOperation(value = "砍价商品列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<PageInfo<StoreBargainResponse>> list(@ModelAttribute PageParamRequest pageParamRequest){
        PageInfo<StoreBargainResponse> h5List = storeBargainService.getH5List(pageParamRequest);
        return CommonResult.success(h5List);
    }

    /**
     * 获取查看、分享、参与人数
     * @return
     */
    @ApiOperation(value = "获取查看、分享、参与人数")
    @RequestMapping(value = "/share/{id}", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> share(@PathVariable(value = "id") Integer id) {
        Map<String, Object> map = storeBargainService.getH5Share(id);
        return CommonResult.success(map);
    }

    /**
     * 获取帮忙好友砍价数据
     * @return
     */
    @ApiOperation(value = "获取帮忙好友砍价数据")
    @RequestMapping(value = "/help/count", method = RequestMethod.GET)
    public CommonResult<BargainCountResponse> count(@ModelAttribute @Validated BargainFrontRequest bargainFrontRequest) {
        BargainCountResponse bargainCountResponse = storeBargainUserHelpService.getH5CountByBargainId(bargainFrontRequest);
        return CommonResult.success(bargainCountResponse);
    }

    /**
     * 帮忙好友砍价信息列表
     * @return 砍价商品列表
     */
    @ApiOperation(value = "帮忙好友砍价信息列表")
    @RequestMapping(value = "/help/list", method = RequestMethod.GET)
    public CommonResult<PageInfo<StoreBargainUserHelpResponse>> helpList(@ModelAttribute @Validated BargainFrontRequest bargainFrontRequest, @ModelAttribute PageParamRequest pageParamRequest){
        PageInfo<StoreBargainUserHelpResponse> list = storeBargainUserHelpService.getHelpList(bargainFrontRequest, pageParamRequest);
        return CommonResult.success(list);
    }

    /**
     * 砍价商品详情
     * @return
     */
    @ApiOperation(value = "砍价商品详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<BargainDetailResponse> detail(@PathVariable(value = "id") Integer id) {
        BargainDetailResponse h5Detail = storeBargainService.getH5Detail(id);
        return CommonResult.success(h5Detail);
    }

    /**
     * 开始砍价
     * @return
     */
    @ApiOperation(value = "开始砍价")
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public CommonResult<Object> start(@RequestBody @Validated BargainFrontRequest bargainFrontRequest) {
        Boolean start = storeBargainService.start(bargainFrontRequest);
        if (start) {
            return CommonResult.success("参与成功");
        }
        return CommonResult.failed("参与失败，请重新参与");
    }

    /**
     * 砍价
     * @return
     */
    @ApiOperation(value = "砍价")
    @RequestMapping(value = "/help", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> help(@RequestBody @Validated BargainFrontRequest bargainFrontRequest) {
        return CommonResult.success(storeBargainUserHelpService.help(bargainFrontRequest));
    }

    /**
     * 砍价发起用户信息
     * @return
     */
    @ApiOperation(value = "砍价发起用户信息")
    @RequestMapping(value = "/start/user", method = RequestMethod.GET)
    public CommonResult<Map<String, String>> startUser(@ModelAttribute @Validated BargainFrontRequest bargainFrontRequest) {
        return CommonResult.success(storeBargainUserHelpService.startUser(bargainFrontRequest));
    }
}
