package com.zbkj.crmeb.front.controller;


import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.category.vo.CategoryTreeVo;
import com.zbkj.crmeb.front.request.ProductRequest;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.front.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户 -- 用户中心
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
@RestController("ProductController")
@RequestMapping("api/front")
@Api(tags = "商品")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 热门商品推荐
     */
    @ApiOperation(value = "热门商品推荐")
    @RequestMapping(value = "/product/hot", method = RequestMethod.GET)
    public CommonResult<CommonPage<IndexProductResponse>> getHotProductList(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(productService.getHotProductList(pageParamRequest));
    }

    /**
     * 优选商品推荐
     */
    @ApiOperation(value = "优选商品推荐")
    @RequestMapping(value = "/product/good", method = RequestMethod.GET)
    public CommonResult<CommonPage<IndexProductResponse>> getGoodProductList(){
        return CommonResult.success(productService.getGoodProductList());
    }

    /**
     * 获取分类
     */
    @ApiOperation(value = "获取分类")
    @RequestMapping(value = "/category", method = RequestMethod.GET)
    public CommonResult<List<CategoryTreeVo>> getCategory(){
        return CommonResult.success(productService.getCategory());
    }

    /**
     * 商品列表
     */
    @ApiOperation(value = "商品列表")
    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public CommonResult<CommonPage<IndexProductResponse>> getList(@Validated ProductRequest request, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(productService.getList(request, pageParamRequest));
    }

    /**
     * 商品详情
     */
    @ApiOperation(value = "商品详情")
    @RequestMapping(value = "/product/detail/{id}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "normal-正常，video-视频")
    public CommonResult<ProductDetailResponse> getDetail(@PathVariable Integer id, @RequestParam(value = "type", defaultValue = "normal") String type){
        return CommonResult.success(productService.getDetail(id, type));
    }

    /**
     * 商品评论列表
     */
    @ApiOperation(value = "商品评论列表")
    @RequestMapping(value = "/reply/list/{id}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "评价等级|0=全部,1=好评,2=中评,3=差评", allowableValues = "range[0,1,2,3]")
    public CommonResult<CommonPage<ProductReplyResponse>> getReplyList(
            @PathVariable Integer id,
            @RequestParam(value = "type") Integer type,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(productService.getReplyList(id, type, pageParamRequest)));
    }

    /**
     * 商品评论数量
     */
    @ApiOperation(value = "商品评论数量")
    @RequestMapping(value = "/reply/config/{id}", method = RequestMethod.GET)
    public CommonResult<StoreProductReplayCountResponse> getReplyCount(@PathVariable Integer id){
        return CommonResult.success(productService.getReplyCount(id));
    }

    /**
     * 商品详情评论
     */
    @ApiOperation(value = "商品详情评论")
    @RequestMapping(value = "/reply/product/{id}", method = RequestMethod.GET)
    public CommonResult<ProductDetailReplyResponse> getProductReply(@PathVariable Integer id){
        return CommonResult.success(productService.getProductReply(id));
    }
}



