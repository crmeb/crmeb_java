package com.zbkj.crmeb.front.controller;

import com.common.CommonResult;
import com.common.PageParamRequest;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.marketing.response.StoreCouponFrontResponse;
import com.zbkj.crmeb.marketing.response.StoreCouponUserOrder;
import com.zbkj.crmeb.marketing.service.StoreCouponService;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * 优惠券表 前端控制器
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController("CouponFrontController")
@RequestMapping("api/front")
@Api(tags = "优惠券")

public class CouponController {

    @Autowired
    private StoreCouponService storeCouponService;

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    @Autowired
    private UserService userService;


    /**
     * 分页显示优惠券表
     * @param type 类型，搜索产品指定优惠券
     * @param productId 产品id，搜索产品指定优惠券
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/coupons", method = RequestMethod.GET)
    public CommonResult<List<StoreCouponFrontResponse>>  getList(
            @RequestParam(value = "type", defaultValue = "0") int type,
            @RequestParam(value = "productId", defaultValue = "0") int productId,
            @Validated PageParamRequest pageParamRequest){

        if(type == 0){
            productId = 0;
        }

        return CommonResult.success(storeCouponService.getListByUser(productId, pageParamRequest, userService.getUserId()));
    }

    /**
     * 根据购物车id获取可用优惠券
     * @param cartId 购物车id
     * @return 优惠券集合
     */
    @ApiOperation(value = "当前购物车可用优惠券")
    @RequestMapping(value = "coupons/order", method = RequestMethod.GET)
    public CommonResult<List<StoreCouponUserOrder>> getCouponsListByCartId(@RequestParam String cartId){
        try {
            CrmebUtil.stringToArrayInt(cartId);
        } catch (NumberFormatException e) {
            return CommonResult.success(new ArrayList<StoreCouponUserOrder>());
        }
        return CommonResult.success(storeCouponUserService.getListByCartIds(CrmebUtil.stringToArrayInt(cartId)));
    }
}



