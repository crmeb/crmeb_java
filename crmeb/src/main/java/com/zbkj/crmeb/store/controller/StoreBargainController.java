package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.request.StoreBargainRequest;
import com.zbkj.crmeb.store.request.StoreBargainSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.store.service.StoreBargainService;
import com.zbkj.crmeb.store.model.StoreBargain;


/**
 * 砍价表 前端控制器
 */
//@Slf4j
//@RestController
//@RequestMapping("api/admin/store/bargain")
//@Api(tags = "商品 -- 砍价") //配合swagger使用

public class StoreBargainController {

    @Autowired
    private StoreBargainService storeBargainService;

    /**
     * 分页显示砍价表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreBargain>>  getList(@Validated StoreBargainSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreBargain> storeBargainCommonPage = CommonPage.restPage(storeBargainService.getList(request, pageParamRequest));
        return CommonResult.success(storeBargainCommonPage);
    }

    /**
     * 新增砍价表
     * @param storeBargainRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated StoreBargainRequest storeBargainRequest){
        StoreBargain storeBargain = new StoreBargain();
        BeanUtils.copyProperties(storeBargainRequest, storeBargain);

        if(storeBargainService.save(storeBargain)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除砍价表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeBargainService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改砍价表
     * @param id integer id
     * @param storeBargainRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated StoreBargainRequest storeBargainRequest){
        StoreBargain storeBargain = new StoreBargain();
        BeanUtils.copyProperties(storeBargainRequest, storeBargain);
        storeBargain.setId(id);

        if(storeBargainService.updateById(storeBargain)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询砍价表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreBargain> info(@RequestParam(value = "id") Integer id){
        StoreBargain storeBargain = storeBargainService.getById(id);
        return CommonResult.success(storeBargain);
   }
}



