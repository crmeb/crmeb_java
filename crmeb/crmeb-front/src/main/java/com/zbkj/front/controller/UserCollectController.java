package com.zbkj.front.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.UserCollectAllRequest;
import com.zbkj.common.request.UserCollectRequest;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.UserRelationResponse;
import com.zbkj.service.service.StoreProductRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 商品点赞和收藏表 前端控制器
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
@RestController
@RequestMapping("api/front/collect")
@Api(tags = "用户 -- 点赞/收藏")
public class UserCollectController {

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    /**
     * 我的收藏列表
     */
    @ApiOperation(value = "我的收藏列表")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserRelationResponse>> getList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(storeProductRelationService.getUserList(pageParamRequest)));
    }

    /**
     * 添加收藏产品
     * @param request StoreProductRelationRequest 新增参数
     */
    @ApiOperation(value = "添加收藏产品")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated UserCollectRequest request) {
        if (storeProductRelationService.add(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 添加收藏产品
     * @param request UserCollectAllRequest 新增参数
     */
    @ApiOperation(value = "批量收藏")
    @RequestMapping(value = "/all", method = RequestMethod.POST)
    public CommonResult<String> all(@RequestBody @Validated UserCollectAllRequest request) {
        if (storeProductRelationService.all(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 取消收藏产品
     */
    @ApiOperation(value = "取消收藏产品")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult<String> delete(@RequestBody String requestJson) {
        if (storeProductRelationService.delete(requestJson)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 取消收藏产品(通过商品)
     */
    @ApiOperation(value = "取消收藏产品(通过商品)")
    @RequestMapping(value = "/cancel/{proId}", method = RequestMethod.POST)
    public CommonResult<String> cancel(@PathVariable Integer proId) {
        if (storeProductRelationService.deleteByProIdAndUid(proId)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }
}



