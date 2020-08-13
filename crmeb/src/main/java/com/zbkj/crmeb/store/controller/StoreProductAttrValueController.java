package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreProductAttrValue;
import com.zbkj.crmeb.store.request.StoreProductAttrValueRequest;
import com.zbkj.crmeb.store.request.StoreProductAttrValueSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductAttrValueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


/**
 * 商品属性值表 前端控制器
 */
//@Slf4j
//@RestController
//@RequestMapping("api/admin/store/product/attr/value")
//@Api(tags = "商品 -- 属性值") //配合swagger使用

public class StoreProductAttrValueController {

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    /**
     * 分页显示商品属性值表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductAttrValue>>  getList(@Validated StoreProductAttrValueSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreProductAttrValue> storeProductAttrValueCommonPage = CommonPage.restPage(storeProductAttrValueService.getList(request, pageParamRequest));
        return CommonResult.success(storeProductAttrValueCommonPage);
    }

    /**
     * 新增商品属性值表
     * @param storeProductAttrValueRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated StoreProductAttrValueRequest storeProductAttrValueRequest){
        StoreProductAttrValue storeProductAttrValue = new StoreProductAttrValue();
        BeanUtils.copyProperties(storeProductAttrValueRequest, storeProductAttrValue);

        if(storeProductAttrValueService.save(storeProductAttrValue)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除商品属性值表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeProductAttrValueService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改商品属性值表
     * @param id integer id
     * @param storeProductAttrValueRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated StoreProductAttrValueRequest storeProductAttrValueRequest){
        StoreProductAttrValue storeProductAttrValue = new StoreProductAttrValue();
        BeanUtils.copyProperties(storeProductAttrValueRequest, storeProductAttrValue);
//        storeProductAttrValue.setId(id);

        if(storeProductAttrValueService.updateById(storeProductAttrValue)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询商品属性值表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreProductAttrValue> info(@RequestParam(value = "id") Integer id){
        StoreProductAttrValue storeProductAttrValue = storeProductAttrValueService.getById(id);
        return CommonResult.success(storeProductAttrValue);
   }
}



