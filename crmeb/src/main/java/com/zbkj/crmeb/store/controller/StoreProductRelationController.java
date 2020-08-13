package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductRelation;
import com.zbkj.crmeb.store.request.StoreProductRelationRequest;
import com.zbkj.crmeb.store.request.StoreProductRelationSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 商品点赞和收藏表 前端控制器
 */
//@Slf4j
//@RestController
//@RequestMapping("api/admin/store/product/relation")
//@Api(tags = "商品 -- 点赞 -- 收藏") //配合swagger使用

public class StoreProductRelationController {

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    /**
     * 分页显示商品点赞和收藏表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProduct>>  getList(
            @Validated StoreProductRelationSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreProduct> storeProductRelationCommonPage
                = CommonPage.restPage(storeProductRelationService.getList(request, pageParamRequest));
        return CommonResult.success(storeProductRelationCommonPage);
    }

    /**
     * 新增商品点赞和收藏表
     * @param storeProductRelationRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated StoreProductRelationRequest storeProductRelationRequest){
        StoreProductRelation storeProductRelation = new StoreProductRelation();
        BeanUtils.copyProperties(storeProductRelationRequest, storeProductRelation);

        if(storeProductRelationService.save(storeProductRelation)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除商品点赞和收藏表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeProductRelationService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改商品点赞和收藏表
     * @param id integer id
     * @param storeProductRelationRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated StoreProductRelationRequest storeProductRelationRequest){
        StoreProductRelation storeProductRelation = new StoreProductRelation();
        BeanUtils.copyProperties(storeProductRelationRequest, storeProductRelation);
//        storeProductRelation.setId(id);

        if(storeProductRelationService.updateById(storeProductRelation)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询商品点赞和收藏表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreProductRelation> info(@RequestParam(value = "id") Integer id){
        StoreProductRelation storeProductRelation = storeProductRelationService.getById(id);
        return CommonResult.success(storeProductRelation);
   }
}



