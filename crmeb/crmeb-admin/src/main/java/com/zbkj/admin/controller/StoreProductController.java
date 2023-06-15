package com.zbkj.admin.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.StoreCopyProductRequest;
import com.zbkj.common.request.StoreProductAddRequest;
import com.zbkj.common.request.StoreProductRequest;
import com.zbkj.common.request.StoreProductSearchRequest;
import com.zbkj.common.response.StoreProductInfoResponse;
import com.zbkj.common.response.StoreProductResponse;
import com.zbkj.common.response.StoreProductTabsHeader;
import com.zbkj.service.service.StoreCartService;
import com.zbkj.service.service.StoreProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * 商品表 前端控制器
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
@RequestMapping("api/admin/store/product")
@Api(tags = "商品") //配合swagger使用
public class StoreProductController {

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreCartService storeCartService;

    /**
     * 分页显示商品表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:product:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductResponse>> getList(@Validated StoreProductSearchRequest request,
                                                                  @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(storeProductService.getAdminList(request, pageParamRequest)));
    }

    /**
     * 新增商品
     * @param request 新增参数
     */
    @PreAuthorize("hasAuthority('admin:product:save')")
    @ApiOperation(value = "新增商品")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated StoreProductAddRequest request) {
        if (storeProductService.save(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 删除商品表
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:product:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestBody @PathVariable Integer id, @RequestParam(value = "type", required = false, defaultValue = "recycle")String type) {
        if (storeProductService.deleteProduct(id, type)) {
            if ("recycle".equals(type)) {
                storeCartService.productStatusNotEnable(id);
            } else {
                storeCartService.productDelete(id);
            }
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 恢复已删除商品表
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:product:restore')")
    @ApiOperation(value = "恢复商品")
    @RequestMapping(value = "/restore/{id}", method = RequestMethod.GET)
    public CommonResult<String> restore(@RequestBody @PathVariable Integer id) {
        if (storeProductService.reStoreProduct(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 商品修改
     * @param storeProductRequest 商品参数
     */
    @PreAuthorize("hasAuthority('admin:product:update')")
    @ApiOperation(value = "商品修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated StoreProductAddRequest storeProductRequest) {
        if (storeProductService.update(storeProductRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 商品详情
     * @param id 商品id
     */
    @PreAuthorize("hasAuthority('admin:product:info')")
    @ApiOperation(value = "商品详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<StoreProductInfoResponse> info(@PathVariable Integer id) {
        return CommonResult.success(storeProductService.getInfo(id));
   }

    /**
     * 商品tabs表头数据
     */
    @PreAuthorize("hasAuthority('admin:product:tabs:headers')")
   @ApiOperation(value = "商品表头数量")
   @RequestMapping(value = "/tabs/headers", method = RequestMethod.GET)
   public CommonResult<List<StoreProductTabsHeader>> getTabsHeader() {
        return CommonResult.success(storeProductService.getTabsHeader());
   }


    /**
     * 上架
     */
    @PreAuthorize("hasAuthority('admin:product:up')")
    @ApiOperation(value = "上架")
    @RequestMapping(value = "/putOnShell/{id}", method = RequestMethod.GET)
    public CommonResult<String> putOn(@PathVariable Integer id) {
        if (storeProductService.putOnShelf(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 下架
     */
    @PreAuthorize("hasAuthority('admin:product:down')")
    @ApiOperation(value = "下架")
    @RequestMapping(value = "/offShell/{id}", method = RequestMethod.GET)
    public CommonResult<String> offShell(@PathVariable Integer id) {
        if (storeProductService.offShelf(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @PreAuthorize("hasAuthority('admin:product:import:product')")
    @ApiOperation(value = "导入99Api商品")
    @RequestMapping(value = "/importProduct", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "form", value = "导入平台1=淘宝，2=京东，3=苏宁，4=拼多多, 5=天猫", dataType = "int",  required = true),
            @ApiImplicitParam(name = "url", value = "URL", dataType = "String", required = true),
    })
    public CommonResult<StoreProductRequest> importProduct(
            @RequestParam @Valid int form,
            @RequestParam @Valid String url) throws IOException, JSONException {
        StoreProductRequest productRequest = storeProductService.importProductFromUrl(url, form);
        return CommonResult.success(productRequest);
    }

    /**
     * 获取复制商品配置
     */
    @PreAuthorize("hasAuthority('admin:product:copy:config')")
    @ApiOperation(value = "获取复制商品配置")
    @RequestMapping(value = "/copy/config", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> copyConfig() {
        return CommonResult.success(storeProductService.copyConfig());
    }

    @PreAuthorize("hasAuthority('admin:product:copy:product')")
    @ApiOperation(value = "复制平台商品")
    @RequestMapping(value = "/copy/product", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> copyProduct(@RequestBody @Valid StoreCopyProductRequest request) {
        return CommonResult.success(storeProductService.copyProduct(request.getUrl()));
    }
}



