package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.request.StoreServiceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.store.service.StoreServiceService;
import com.zbkj.crmeb.store.model.StoreService;


/**
 * 客服表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/store-service")
@Api(tags = "客服表") //配合swagger使用

public class StoreServiceController {

    @Autowired
    private StoreServiceService storeServiceService;

    /**
     * 分页显示客服表
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreService>>  getList(@Validated PageParamRequest pageParamRequest){
        CommonPage<StoreService> storeServiceCommonPage = CommonPage.restPage(storeServiceService.getList(pageParamRequest));
        return CommonResult.success(storeServiceCommonPage);
    }

    /**
     * 新增客服表
     * @param storeServiceRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated StoreServiceRequest storeServiceRequest){
        StoreService storeService = new StoreService();
        BeanUtils.copyProperties(storeServiceRequest, storeService);

        if(storeServiceService.save(storeService)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除客服表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeServiceService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改客服表
     * @param id integer id
     * @param storeServiceRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated StoreServiceRequest storeServiceRequest){
        StoreService storeService = new StoreService();
        BeanUtils.copyProperties(storeServiceRequest, storeService);
        storeService.setId(id);

        if(storeServiceService.updateById(storeService)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询客服表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreService> info(@RequestParam(value = "id") Integer id){
        StoreService storeService = storeServiceService.getById(id);
        return CommonResult.success(storeService);
   }
}



