package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.request.StorePinkRequest;
import com.zbkj.crmeb.store.request.StorePinkSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.store.service.StorePinkService;
import com.zbkj.crmeb.store.model.StorePink;


/**
 * 拼团表 前端控制器
 */
//@Slf4j
//@RestController
//@RequestMapping("api/admin/store/pink")
//@Api(tags = "拼团") //配合swagger使用

public class StorePinkController {

    @Autowired
    private StorePinkService storePinkService;

    /**
     * 分页显示拼团表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StorePink>>  getList(
            @Validated StorePinkSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        CommonPage<StorePink> storePinkCommonPage = CommonPage.restPage(storePinkService.getList(request, pageParamRequest));
        return CommonResult.success(storePinkCommonPage);
    }

    /**
     * 新增拼团表
     * @param storePinkRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated StorePinkRequest storePinkRequest){
        StorePink storePink = new StorePink();
        BeanUtils.copyProperties(storePinkRequest, storePink);

        if(storePinkService.save(storePink)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除拼团表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storePinkService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改拼团表
     * @param id integer id
     * @param storePinkRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated StorePinkRequest storePinkRequest){
        StorePink storePink = new StorePink();
        BeanUtils.copyProperties(storePinkRequest, storePink);
        storePink.setId(id);

        if(storePinkService.updateById(storePink)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询拼团表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StorePink> info(@RequestParam(value = "id") Integer id){
        StorePink storePink = storePinkService.getById(id);
        return CommonResult.success(storePink);
   }
}



