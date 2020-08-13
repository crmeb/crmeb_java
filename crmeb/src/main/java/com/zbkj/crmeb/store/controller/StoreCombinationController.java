package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.request.StoreCombinationRequest;
import com.zbkj.crmeb.store.request.StoreCombinationSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.store.service.StoreCombinationService;
import com.zbkj.crmeb.store.model.StoreCombination;


/**
 * 拼团商品表 前端控制器
 */
//@Slf4j
//@RestController
//@RequestMapping("api/admin/store/combination")
//@Api(tags = "商品 -- 拼团") //配合swagger使用

public class StoreCombinationController {

    @Autowired
    private StoreCombinationService storeCombinationService;

    /**
     * 分页显示拼团商品表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreCombination>>  getList(
            @Validated StoreCombinationSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreCombination> storeCombinationCommonPage =
                CommonPage.restPage(storeCombinationService.getList(request, pageParamRequest));
        return CommonResult.success(storeCombinationCommonPage);
    }

    /**
     * 新增拼团商品表
     * @param storeCombinationRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated StoreCombinationRequest storeCombinationRequest){
        StoreCombination storeCombination = new StoreCombination();
        BeanUtils.copyProperties(storeCombinationRequest, storeCombination);

        if(storeCombinationService.save(storeCombination)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除拼团商品表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeCombinationService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改拼团商品表
     * @param id integer id
     * @param storeCombinationRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated StoreCombinationRequest storeCombinationRequest){
        StoreCombination storeCombination = new StoreCombination();
        BeanUtils.copyProperties(storeCombinationRequest, storeCombination);
        storeCombination.setId(id);

        if(storeCombinationService.updateById(storeCombination)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询拼团商品表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreCombination> info(@RequestParam(value = "id") Integer id){
        StoreCombination storeCombination = storeCombinationService.getById(id);
        return CommonResult.success(storeCombination);
   }
}



