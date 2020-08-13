package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreProductCate;
import com.zbkj.crmeb.store.request.StoreProductCateRequest;
import com.zbkj.crmeb.store.request.StoreProductCateSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductCateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


/**
 * 商品分类辅助表 前端控制器
 */
//@Slf4j
//@RestController
//@RequestMapping("api/admin/store/product/cate")
//@Api(tags = "商品 -- 分类辅助") //配合swagger使用

public class StoreProductCateController {

    @Autowired
    private StoreProductCateService storeProductCateService;

    /**
     * 分页显示商品分类辅助表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductCate>>  getList(@Validated StoreProductCateSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreProductCate> storeProductCateCommonPage = CommonPage.restPage(storeProductCateService.getList(request, pageParamRequest));
        return CommonResult.success(storeProductCateCommonPage);
    }

    /**
     * 新增商品分类辅助表
     * @param storeProductCateRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated StoreProductCateRequest storeProductCateRequest){
        StoreProductCate storeProductCate = new StoreProductCate();
        BeanUtils.copyProperties(storeProductCateRequest, storeProductCate);

        if(storeProductCateService.save(storeProductCate)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除商品分类辅助表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeProductCateService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改商品分类辅助表
     * @param id integer id
     * @param storeProductCateRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated StoreProductCateRequest storeProductCateRequest){
        StoreProductCate storeProductCate = new StoreProductCate();
        BeanUtils.copyProperties(storeProductCateRequest, storeProductCate);
        storeProductCate.setId(id);

        if(storeProductCateService.updateById(storeProductCate)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询商品分类辅助表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreProductCate> info(@RequestParam(value = "id") Integer id){
        StoreProductCate storeProductCate = storeProductCateService.getById(id);
        return CommonResult.success(storeProductCate);
   }
}



