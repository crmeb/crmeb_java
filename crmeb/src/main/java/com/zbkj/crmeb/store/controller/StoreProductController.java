package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.request.StoreCopyProductRequest;
import com.zbkj.crmeb.store.request.StoreProductRequest;
import com.zbkj.crmeb.store.request.StoreProductSearchRequest;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.store.response.StoreProductTabsHeader;
import com.zbkj.crmeb.store.service.StoreCartService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.store.service.StoreProductService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * 商品表 前端控制器
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
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductResponse>> getList(
            @Validated StoreProductSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(storeProductService.getList(request, pageParamRequest)));
    }

    /**
     * 新增商品表
     * @param storeProductRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated StoreProductRequest storeProductRequest){
        if(storeProductService.save(storeProductRequest)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除商品表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestBody @PathVariable Integer id, @RequestParam(value = "type", required = false, defaultValue = "recycle")String type){
        if(storeProductService.deleteProduct(id, type)){
//        if(storeProductService.removeById(id)){
            storeCartService.productStatusNotEnable(id);
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 恢复已删除商品表
     * @param id Integer
     * @author Stivepeim
     * @since 2020-08-28
     */
    @ApiOperation(value = "恢复商品")
    @RequestMapping(value = "/restore/{id}", method = RequestMethod.GET)
    public CommonResult<String> restore(@RequestBody @PathVariable Integer id){
        if(storeProductService.reStoreProduct(id)){
//            storeCartService.productStatusNotEnable(id);
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改商品表
     * @param storeProductRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated StoreProductRequest storeProductRequest){
        if(storeProductService.update(storeProductRequest)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询商品表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<StoreProductResponse> info(@PathVariable Integer id){
        StoreProductResponse storeProductResponse = storeProductService.getByProductId(id);
        return CommonResult.success(storeProductResponse);
   }

    /**
     * 商品tabs表头数据
     * @return
     */
   @ApiOperation(value = "商品表头数量")
   @RequestMapping(value = "/tabs/headers", method = RequestMethod.GET)
   public CommonResult<List<StoreProductTabsHeader>> getTabsHeader(){
        return CommonResult.success(storeProductService.getTabsHeader());
   }


    /**
     * 上架
     */
    @ApiOperation(value = "上架")
    @RequestMapping(value = "/putOnShell/{id}", method = RequestMethod.GET)
    public CommonResult<String> putOn(@PathVariable Integer id){
        if(storeProductService.putOnShelf(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 下架
     */
    @ApiOperation(value = "下架")
    @RequestMapping(value = "/offShell/{id}", method = RequestMethod.GET)
    public CommonResult<String> offShell(@PathVariable Integer id){
        if(storeProductService.offShelf(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 虚拟销量
     * @param id integer id
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @ApiOperation(value = "虚拟销量")
    @RequestMapping(value = "/ficti/{id}/{num}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "int",  required = true),
            @ApiImplicitParam(name = "num", value = "数值", dataType = "int", required = true),
    })
    public CommonResult<String> sale(@PathVariable Integer id, @PathVariable Integer num){
        StoreProduct storeProduct = storeProductService.getById(id);
        storeProduct.setFicti(num);
        if(storeProductService.updateById(storeProduct)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

//    /**
//     * 修改商品表
//     * @param id integer id
//     * @param num integer 排序值
//     * @author Mr.Zhang
//     * @since 2020-05-06
//     */
//    @ApiOperation(value = "排序数字")
//    @RequestMapping(value = "/sort", method = RequestMethod.GET)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "id", dataType = "Integer", required = true),
//            @ApiImplicitParam(name = "num", value = "数值", dataType = "Integer", required = true),
//    })
//    public CommonResult<String> sort(@RequestParam(value = "id") Integer id, @RequestParam(value = "num") Integer num){
//        StoreProduct storeProduct = storeProductService.getById(id);
//        storeProduct.setSort(num);
//        if(storeProductService.updateById(storeProduct)){
//            return CommonResult.success();
//        }else{
//            return CommonResult.failed();
//        }
//    }
    /**
     * 库存变动
     * @param request StoreProductStockRequest 参数
     * @author Mr.Zhang
     * @since 2020-05-19
     */
    @ApiOperation(value = "库存变动")
    @RequestMapping(value = "/stock", method = RequestMethod.GET)
    public CommonResult<String> stock(@Validated StoreProductStockRequest request){
        if(storeProductService.stockAddRedis(request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

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
    @ApiOperation(value = "获取复制商品配置")
    @RequestMapping(value = "/copy/config", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> copyConfig() {
        return CommonResult.success(storeProductService.copyConfig());
    }

    @ApiOperation(value = "复制平台商品")
    @RequestMapping(value = "/copy/product", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> copyProduct(@RequestBody @Valid StoreCopyProductRequest request) {
        return CommonResult.success(storeProductService.copyProduct(request.getUrl()));
    }
}



