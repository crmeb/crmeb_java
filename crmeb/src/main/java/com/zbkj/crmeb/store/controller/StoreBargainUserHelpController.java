package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.request.StoreBargainUserHelpRequest;
import com.zbkj.crmeb.store.request.StoreBargainUserHelpSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.store.service.StoreBargainUserHelpService;
import com.zbkj.crmeb.store.model.StoreBargainUserHelp;


/**
 * 砍价用户帮助表 前端控制器
 */
//@Slf4j
//@RestController
//@RequestMapping("api/admin/store/bargain/user/help")
//@Api(tags = "商品 -- 砍价 --用户帮助") //配合swagger使用

public class StoreBargainUserHelpController {

    @Autowired
    private StoreBargainUserHelpService storeBargainUserHelpService;

    /**
     * 分页显示砍价用户帮助表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreBargainUserHelp>>  getList(
            @Validated StoreBargainUserHelpSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreBargainUserHelp> storeBargainUserHelpCommonPage = CommonPage.restPage(storeBargainUserHelpService.getList(request, pageParamRequest));
        return CommonResult.success(storeBargainUserHelpCommonPage);
    }

    /**
     * 新增砍价用户帮助表
     * @param storeBargainUserHelpRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated StoreBargainUserHelpRequest storeBargainUserHelpRequest){
        StoreBargainUserHelp storeBargainUserHelp = new StoreBargainUserHelp();
        BeanUtils.copyProperties(storeBargainUserHelpRequest, storeBargainUserHelp);

        if(storeBargainUserHelpService.save(storeBargainUserHelp)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除砍价用户帮助表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeBargainUserHelpService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改砍价用户帮助表
     * @param id integer id
     * @param storeBargainUserHelpRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated StoreBargainUserHelpRequest storeBargainUserHelpRequest){
        StoreBargainUserHelp storeBargainUserHelp = new StoreBargainUserHelp();
        BeanUtils.copyProperties(storeBargainUserHelpRequest, storeBargainUserHelp);
        storeBargainUserHelp.setId(id);

        if(storeBargainUserHelpService.updateById(storeBargainUserHelp)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询砍价用户帮助表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreBargainUserHelp> info(@RequestParam(value = "id") Integer id){
        StoreBargainUserHelp storeBargainUserHelp = storeBargainUserHelpService.getById(id);
        return CommonResult.success(storeBargainUserHelp);
   }
}



