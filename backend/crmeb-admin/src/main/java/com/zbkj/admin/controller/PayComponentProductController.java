package com.zbkj.admin.controller;

import com.zbkj.common.model.wechat.video.PayComponentDraftProduct;
import com.zbkj.common.model.wechat.video.PayComponentProduct;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.ComponentProductSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.PayComponentProductAddRequest;
import com.zbkj.common.response.PayComponentProductResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.PayComponentDraftProductService;
import com.zbkj.service.service.PayComponentProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 *  组件商品表 前端控制器
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/pay/component/product")
@Api(tags = "自定义交易组件—组件商品表") //配合swagger使用
public class PayComponentProductController {

    @Autowired
    private PayComponentProductService payComponentProductService;
    @Autowired
    private PayComponentDraftProductService payComponentDraftProductService;

    /**
     * 添加商品
     */
    @PreAuthorize("hasAuthority('admin:pay:component:product:add')")
    @ApiOperation(value = "添加商品")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<Object> add(@RequestBody @Validated PayComponentProductAddRequest addRequest) {
        if (payComponentDraftProductService.add(addRequest)) {
            return CommonResult.success("添加商品成功");
        }
        return CommonResult.failed("添加商品失败");
    }

    /**
     * 删除商品
     */
    @PreAuthorize("hasAuthority('admin:pay:component:product:delete')")
    @ApiOperation(value = "删除商品")
    @RequestMapping(value = "/delete/{proId}", method = RequestMethod.POST)
    public CommonResult<Object> delete(@PathVariable Integer proId) {
        if (payComponentProductService.delete(proId)) {
            return CommonResult.success("删除商品成功");
        }
        return CommonResult.failed("删除商品失败");
    }

    /**
     * 更新商品
     */
    @PreAuthorize("hasAuthority('admin:pay:component:product:update')")
    @ApiOperation(value = "更新商品")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Object> update(@RequestBody @Validated PayComponentProductAddRequest addRequest) {
        if (payComponentProductService.update(addRequest)) {
            return CommonResult.success("更新商品成功");
        }
        return CommonResult.failed("更新商品失败");
    }

    /**
     * 上架商品
     */
    @PreAuthorize("hasAuthority('admin:pay:component:product:listing')")
    @ApiOperation(value = "上架商品")
    @RequestMapping(value = "/listing/{proId}", method = RequestMethod.POST)
    public CommonResult<Object> listing(@PathVariable Integer proId) {
        if (payComponentProductService.listing(proId)) {
            return CommonResult.success("上架商品成功");
        }
        return CommonResult.failed("上架商品失败");
    }

    /**
     * 下架商品
     */
    @PreAuthorize("hasAuthority('admin:pay:component:product:delisting')")
    @ApiOperation(value = "下架商品")
    @RequestMapping(value = "/delisting/{proId}", method = RequestMethod.POST)
    public CommonResult<Object> delisting(@PathVariable Integer proId) {
        if (payComponentProductService.delisting(proId)) {
            return CommonResult.success("下架商品成功");
        }
        return CommonResult.failed("下架商品失败");
    }

    /**
     * 商品列表（分页）
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:pay:component:product:list')")
    @ApiOperation(value = "商品列表（分页）") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PayComponentProduct>> getList(@Validated ComponentProductSearchRequest request,
                                                                 @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(payComponentProductService.getAdminList(request, pageParamRequest)));
    }

    /**
     * 草稿商品列表（分页）
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:pay:component:product:draft:list')")
    @ApiOperation(value = "草稿商品列表（分页）") //配合swagger使用
    @RequestMapping(value = "/draft/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PayComponentDraftProduct>> getDraftList(@Validated ComponentProductSearchRequest request,
                                                                           @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(payComponentDraftProductService.getAdminList(request, pageParamRequest)));
    }

    /**
     * 草稿商品详情
     */
    @PreAuthorize("hasAuthority('admin:pay:component:product:draft:info')")
    @ApiOperation(value = "草稿商品详情") //配合swagger使用
    @RequestMapping(value = "/draft/get/{id}", method = RequestMethod.GET)
    public CommonResult<PayComponentDraftProduct> getDraftInfo(@PathVariable Integer id) {
        return CommonResult.success(payComponentDraftProductService.getInfo(id));
    }

    /**
     * 正式商品详情
     */
    @PreAuthorize("hasAuthority('admin:pay:component:product:info')")
    @ApiOperation(value = "正式商品详情") //配合swagger使用
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public CommonResult<PayComponentProductResponse> getInfo(@PathVariable Integer id) {
        return CommonResult.success(payComponentProductService.getInfo(id));
    }
}



