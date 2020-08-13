package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.request.StoreCategoryRequest;
import com.zbkj.crmeb.store.request.StoreCategorySearchRequest;
import com.zbkj.crmeb.store.response.StoreCategoryTreeList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.store.service.StoreCategoryService;
import com.zbkj.crmeb.store.model.StoreCategory;

import java.util.List;


/**
 * 商品分类表 前端控制器
 */
//@Slf4j
//@RestController
//@RequestMapping("api/admin/store/category")
//@Api(tags = "商品 -- 分类") //配合swagger使用
// 注意：商品分类使用无限极分类完成
public class StoreCategoryController {

    @Autowired
    private StoreCategoryService storeCategoryService;

    /**
     * 分页显示商品分类表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreCategory>>  getList(
            @Validated StoreCategorySearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreCategory> storeCategoryCommonPage =
                CommonPage.restPage(storeCategoryService.getList(request, pageParamRequest));
        return CommonResult.success(storeCategoryCommonPage);
    }

//    @ApiOperation(value = "树形结构体")
//    @RequestMapping(value = "/list/tree", method = RequestMethod.GET)
//    public CommonResult<StoreCategoryTreeList> getTreeData(
//            @Validated Integer pid,
//            @Validated Integer isShow){
//        List<StoreCategoryTreeList> list = storeCategoryService.getTreeList(pid, isShow);
//        return null;
//    }

    /**
     * 新增商品分类表
     * @param storeCategoryRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated StoreCategoryRequest storeCategoryRequest){
        StoreCategory storeCategory = new StoreCategory();
        BeanUtils.copyProperties(storeCategoryRequest, storeCategory);

        if(storeCategoryService.save(storeCategory)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除商品分类表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeCategoryService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改商品分类表
     * @param id integer id
     * @param storeCategoryRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated StoreCategoryRequest storeCategoryRequest){
        StoreCategory storeCategory = new StoreCategory();
        BeanUtils.copyProperties(storeCategoryRequest, storeCategory);
        storeCategory.setId(id);

        if(storeCategoryService.updateById(storeCategory)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询商品分类表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreCategory> info(@RequestParam(value = "id") Integer id){
        StoreCategory storeCategory = storeCategoryService.getById(id);
        return CommonResult.success(storeCategory);
   }
}



