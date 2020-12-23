package com.zbkj.crmeb.marketing.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.zbkj.crmeb.marketing.request.StoreCouponUserRequest;
import com.zbkj.crmeb.marketing.request.StoreCouponUserSearchRequest;
import com.zbkj.crmeb.marketing.response.StoreCouponUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.marketing.model.StoreCouponUser;


/**
 * 优惠券发放记录表 前端控制器
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
@RequestMapping("api/admin/marketing/coupon/user")
@Api(tags = "营销 -- 优惠券 -- 领取记录")
public class StoreCouponUserController {

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    /**
     * 分页显示优惠券发放记录表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreCouponUserResponse>>  getList(@Validated StoreCouponUserSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreCouponUserResponse> storeCouponUserCommonPage = CommonPage.restPage(storeCouponUserService.getList(request, pageParamRequest));
        return CommonResult.success(storeCouponUserCommonPage);
    }

    /**
     * 领券
     * @param storeCouponUserRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @ApiOperation(value = "领券")
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    public CommonResult<String> receive(@Validated StoreCouponUserRequest storeCouponUserRequest){
        if(storeCouponUserService.receive(storeCouponUserRequest)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }
}



