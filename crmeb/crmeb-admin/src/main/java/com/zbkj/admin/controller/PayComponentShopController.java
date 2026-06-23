package com.zbkj.admin.controller;

import com.zbkj.common.model.wechat.video.PayComponentShopBrand;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ShopUploadImgRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.BaseResultResponseVo;
import com.zbkj.common.vo.RegisterCheckResponseVo;
import com.zbkj.common.vo.ShopAuditBrandRequestVo;
import com.zbkj.common.vo.WechatVideoUploadImageResponseVo;
import com.zbkj.service.service.PayComponentShopService;
import com.zbkj.service.service.WechatVideoBeforeService;
import com.zbkj.service.service.WechatVideoShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  自定义交易组件—商家及接入前
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
@RestController
@RequestMapping("api/admin/pay/component/shop")
@Api(tags = "自定义交易组件—商家及接入前") //配合swagger使用
public class PayComponentShopController {

    @Autowired
    private PayComponentShopService shopService;

    @Autowired
    private WechatVideoShopService wechatVideoShopService;

    @Autowired
    private WechatVideoBeforeService wechatVideoBeforeService;

    /**
     * 小程序接入申请
     * @return 申请结果
     */
    @PreAuthorize("hasAuthority('admin:pay:component:shop:register')")
    @ApiOperation(value = "小程序接入申请")
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public CommonResult<BaseResultResponseVo> shopRegisterApply() {
        return CommonResult.success(wechatVideoShopService.shopRegisterApply());
    }

    /**
     * 小程序接入状态检查
     * @return 接入状态
     */
    @PreAuthorize("hasAuthority('admin:pay:component:shop:register:check')")
    @ApiOperation(value = "获取小程序接入状态")
    @RequestMapping(value = "/register/check", method = RequestMethod.GET)
    public CommonResult<RegisterCheckResponseVo> shopRegisterCheck() {
        return CommonResult.success(wechatVideoShopService.shopRegisterCheck());
    }

    // 获取类目详情在其他业务中实现
    @PreAuthorize("hasAuthority('admin:pay:component:shop:img:upload')")
    @ApiOperation(value = "上传图片，只用于品牌和类目申请")
    @RequestMapping(value = "/img/upload", method = RequestMethod.POST)
    public CommonResult<WechatVideoUploadImageResponseVo> shopImgUpload(@RequestBody ShopUploadImgRequest request) {
        return CommonResult.success(wechatVideoBeforeService.shopImgUpload(request));
    }

    @PreAuthorize("hasAuthority('admin:pay:component:shop:brand:audit')")
    @ApiOperation(value = "上传品牌信息")
    @RequestMapping(value = "/brand/audit", method = RequestMethod.POST)
    public CommonResult<Object> shopAuditBrand(@RequestBody @Validated ShopAuditBrandRequestVo request) {
        return CommonResult.success(shopService.auditBrand(request));
    }

    @PreAuthorize("hasAuthority('admin:pay:component:shop:brand:list')")
    @ApiOperation(value = "品牌列表")
    @RequestMapping(value = "/brand/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PayComponentShopBrand>> shopBrandList(@Validated PageParamRequest pageParamRequest, @RequestParam(value = "status", required = false) Integer status) {
        return CommonResult.success(CommonPage.restPage(shopService.brandList(pageParamRequest, status)));
    }

    @PreAuthorize("hasAuthority('admin:pay:component:shop:brand:usable:list')")
    @ApiOperation(value = "品牌列表(可用)")
    @RequestMapping(value = "/brand/usable/list", method = RequestMethod.GET)
    public CommonResult<List<PayComponentShopBrand>> shopUsableBrandList() {
        return CommonResult.success(shopService.usableBrandList());
    }
}
