package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreProductDescription;
import com.zbkj.crmeb.store.request.StoreProductDescriptionRequest;
import com.zbkj.crmeb.store.request.StoreProductDescriptionSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductDescriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


/**
 *  前端控制器
 */
//@Slf4j
//@RestController
//@RequestMapping("api/admin/store/product/description")
//@Api(tags = "商品 -- 描述") //配合swagger使用

public class StoreProductDescriptionController {

    @Autowired
    private StoreProductDescriptionService storeProductDescriptionService;

    /**
     * 分页显示
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductDescription>>  getList(@Validated StoreProductDescriptionSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreProductDescription> storeProductDescriptionCommonPage = CommonPage.restPage(storeProductDescriptionService.getList(request, pageParamRequest));
        return CommonResult.success(storeProductDescriptionCommonPage);
    }

    /**
     * 新增
     * @param storeProductDescriptionRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated StoreProductDescriptionRequest storeProductDescriptionRequest){
        StoreProductDescription storeProductDescription = new StoreProductDescription();
        BeanUtils.copyProperties(storeProductDescriptionRequest, storeProductDescription);

        if(storeProductDescriptionService.save(storeProductDescription)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeProductDescriptionService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改
     * @param id integer id
     * @param storeProductDescriptionRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated StoreProductDescriptionRequest storeProductDescriptionRequest){
        StoreProductDescription storeProductDescription = new StoreProductDescription();
        BeanUtils.copyProperties(storeProductDescriptionRequest, storeProductDescription);
//        storeProductDescription.setId(id);

        if(storeProductDescriptionService.updateById(storeProductDescription)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-27
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreProductDescription> info(@RequestParam(value = "id") Integer id){
        StoreProductDescription storeProductDescription = storeProductDescriptionService.getById(id);
        return CommonResult.success(storeProductDescription);
   }
}



