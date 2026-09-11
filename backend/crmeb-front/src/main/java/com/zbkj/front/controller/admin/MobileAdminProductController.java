package com.zbkj.front.controller.admin;

import com.zbkj.common.model.express.ShippingTemplates;
import com.zbkj.common.model.product.StoreProductAttrValue;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.MobileAdminProductAttrValueUpdateRequest;
import com.zbkj.common.response.StoreProductResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.CategoryTreeVo;
import com.zbkj.front.service.MobileAdminProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 移动端商家商品管理
 */
@RestController
@RequestMapping("api/front/manage/product")
@Api(tags = "移动端商家管理-商品")
public class MobileAdminProductController {

    @Autowired
    private MobileAdminProductService mobileAdminProductService;

    @ApiOperation(value = "商品列表")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductResponse>> getList(@RequestParam Map<String, Object> params) {
        return CommonResult.success(mobileAdminProductService.getList(params));
    }

    @ApiOperation(value = "商品上下架")
    @RequestMapping(value = "/set_show", method = RequestMethod.POST)
    public CommonResult<Boolean> setShow(@RequestBody Map<String, Object> params) {
        return CommonResult.success(mobileAdminProductService.setShow(params));
    }

    @ApiOperation(value = "商品分类树")
    @RequestMapping(value = "/cate", method = RequestMethod.GET)
    public CommonResult<List<CategoryTreeVo>> getCate() {
        return CommonResult.success(mobileAdminProductService.getCate());
    }

    @ApiOperation(value = "保存商品分类")
    @RequestMapping(value = "/save_cate", method = RequestMethod.POST)
    public CommonResult<Boolean> saveCate(@RequestBody Map<String, Object> params) {
        return CommonResult.success(mobileAdminProductService.saveCate(params));
    }

    @ApiOperation(value = "商品运费模板")
    @RequestMapping(value = "/shipping_temp", method = RequestMethod.GET)
    public CommonResult<List<ShippingTemplates>> getShippingTemp() {
        return CommonResult.success(mobileAdminProductService.getShippingTemp());
    }

    @ApiOperation(value = "新增商品")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<Boolean> create(@RequestBody Map<String, Object> params) {
        return CommonResult.success(mobileAdminProductService.create(params));
    }

    @ApiOperation(value = "商品标签列表")
    @RequestMapping(value = "/label", method = RequestMethod.GET)
    public CommonResult<List<Object>> getLabel() {
        return CommonResult.success(new ArrayList<Object>());
    }

    @ApiOperation(value = "保存商品标签")
    @RequestMapping(value = "/save_label", method = RequestMethod.POST)
    public CommonResult<Object> saveLabel() {
        return CommonResult.failed("当前项目未配置商品标签功能");
    }

    @ApiOperation(value = "商品规格价格库存")
    @RequestMapping(value = "/attr/{id}", method = RequestMethod.GET)
    public CommonResult<List<StoreProductAttrValue>> getAttr(@PathVariable Integer id) {
        return CommonResult.success(mobileAdminProductService.getAttr(id));
    }

    @ApiOperation(value = "保存商品规格价格库存")
    @RequestMapping(value = "/save_attr/{id}", method = RequestMethod.POST)
    public CommonResult<Boolean> saveAttr(@PathVariable Integer id,
                                          @RequestBody MobileAdminProductAttrValueUpdateRequest request) {
        return CommonResult.success(mobileAdminProductService.updateAttr(id, request));
    }
}
