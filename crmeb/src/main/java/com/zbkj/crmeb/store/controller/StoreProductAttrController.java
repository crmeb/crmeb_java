package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreProductAttr;
import com.zbkj.crmeb.store.request.StoreProductAttrRequest;
import com.zbkj.crmeb.store.request.StoreProductAttrSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductAttrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


/**
 * 商品属性表 前端控制器
 */
//@Slf4j
//@RestController
//@RequestMapping("api/admin/store/product/attr")
//@Api(tags = "商品 -- 属性") //配合swagger使用

public class StoreProductAttrController {

    @Autowired
    private StoreProductAttrService storeProductAttrService;

    /**
     * 分页显示商品属性表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductAttr>>  getList(@Validated StoreProductAttrSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreProductAttr> storeProductAttrCommonPage = CommonPage.restPage(storeProductAttrService.getList(request, pageParamRequest));
        return CommonResult.success(storeProductAttrCommonPage);
    }

    /**
     * 新增商品属性表
     * @param storeProductAttrRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated StoreProductAttrRequest storeProductAttrRequest){
        StoreProductAttr storeProductAttr = new StoreProductAttr();
        BeanUtils.copyProperties(storeProductAttrRequest, storeProductAttr);

        if(storeProductAttrService.save(storeProductAttr)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除商品属性表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeProductAttrService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改商品属性表
     * @param id integer id
     * @param storeProductAttrRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated StoreProductAttrRequest storeProductAttrRequest){
        StoreProductAttr storeProductAttr = new StoreProductAttr();
        BeanUtils.copyProperties(storeProductAttrRequest, storeProductAttr);
//        storeProductAttr.setId(id);

        if(storeProductAttrService.updateById(storeProductAttr)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询商品属性表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreProductAttr> info(@RequestParam(value = "id") Integer id){
        StoreProductAttr storeProductAttr = storeProductAttrService.getById(id);
        return CommonResult.success(storeProductAttr);
   }
}



