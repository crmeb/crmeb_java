package com.zbkj.crmeb.front.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.front.request.UserCouponReceiveRequest;
import com.zbkj.crmeb.marketing.response.StoreCouponUserResponse;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 优惠卷控制器
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
@RequestMapping("api/front/coupon")
@Api(tags = "营销 -- 优惠券")
public class UserCouponController {

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    /**
     * 我的优惠券
     */
    @ApiOperation(value = "我的优惠券")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="type", value="类型，usable-可用，unusable-不可用", required = true),
            @ApiImplicitParam(name="page", value="页码", required = true),
            @ApiImplicitParam(name="limit", value="每页数量", required = true)
    })
    public CommonResult<CommonPage<StoreCouponUserResponse>> getList(@RequestParam(value = "type") String type,
                                                                     @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(storeCouponUserService.getMyCouponList(type, pageParamRequest));
    }

    /**
     * 领券
     * @param request UserCouponReceiveRequest 新增参数
     */
    @ApiOperation(value = "领券")
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    public CommonResult<String> receive(@RequestBody @Validated UserCouponReceiveRequest request){
        if(storeCouponUserService.receiveCoupon(request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

}



