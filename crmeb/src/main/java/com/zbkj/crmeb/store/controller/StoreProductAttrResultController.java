package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreProductAttrResult;
import com.zbkj.crmeb.store.request.StoreProductAttrResultRequest;
import com.zbkj.crmeb.store.request.StoreProductAttrResultSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductAttrResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


/**
 * 商品属性详情表 前端控制器
 */
//@Slf4j
//@RestController
//@RequestMapping("api/admin/store/product/attr/result")
//@Api(tags = "商品 -- 属性 -- 详情") //配合swagger使用

public class StoreProductAttrResultController {

    @Autowired
    private StoreProductAttrResultService storeProductAttrResultService;

    /**
     * 分页显示商品属性详情表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductAttrResult>>  getList(@Validated StoreProductAttrResultSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreProductAttrResult> storeProductAttrResultCommonPage = CommonPage.restPage(storeProductAttrResultService.getList(request, pageParamRequest));
        return CommonResult.success(storeProductAttrResultCommonPage);
    }

    /**
     * 新增商品属性详情表
     * @param storeProductAttrResultRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated StoreProductAttrResultRequest storeProductAttrResultRequest){
        StoreProductAttrResult storeProductAttrResult = new StoreProductAttrResult();
        BeanUtils.copyProperties(storeProductAttrResultRequest, storeProductAttrResult);

        if(storeProductAttrResultService.save(storeProductAttrResult)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除商品属性详情表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeProductAttrResultService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改商品属性详情表
     * @param id integer id
     * @param storeProductAttrResultRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated StoreProductAttrResultRequest storeProductAttrResultRequest){
        StoreProductAttrResult storeProductAttrResult = new StoreProductAttrResult();
        BeanUtils.copyProperties(storeProductAttrResultRequest, storeProductAttrResult);
//        storeProductAttrResult.setId(id);

        if(storeProductAttrResultService.updateById(storeProductAttrResult)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询商品属性详情表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreProductAttrResult> info(@RequestParam(value = "id") Integer id){
        StoreProductAttrResult storeProductAttrResult = storeProductAttrResultService.getById(id);
        return CommonResult.success(storeProductAttrResult);
   }
}



