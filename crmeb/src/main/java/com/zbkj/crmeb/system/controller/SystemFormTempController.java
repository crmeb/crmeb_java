package com.zbkj.crmeb.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.zbkj.crmeb.system.request.SystemFormTempRequest;
import com.zbkj.crmeb.system.request.SystemFormTempSearchRequest;
import com.zbkj.crmeb.system.vo.SystemConfigFormVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.system.service.SystemFormTempService;
import com.zbkj.crmeb.system.model.SystemFormTemp;


/**
 * 表单模板 前端控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/system/form/temp")
@Api(tags = "设置 -- 表单模板")
public class SystemFormTempController {

    @Autowired
    private SystemFormTempService systemFormTempService;

    /**
     * 分页显示表单模板
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-15
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemFormTemp>>  getList(@Validated SystemFormTempSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<SystemFormTemp> systemFormTempCommonPage = CommonPage.restPage(systemFormTempService.getList(request, pageParamRequest));
        return CommonResult.success(systemFormTempCommonPage);
    }

    /**
     * 新增表单模板
     * @param systemFormTempRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-15
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated SystemFormTempRequest systemFormTempRequest){
        try{
            JSONObject.parseObject(systemFormTempRequest.getContent(), SystemConfigFormVo.class);
        }catch (Exception e){
            throw new CrmebException("模板表单 【" + systemFormTempRequest.getName() + "】 的内容不是正确的JSON格式！");
        }
        
        SystemFormTemp systemFormTemp = new SystemFormTemp();
        BeanUtils.copyProperties(systemFormTempRequest, systemFormTemp);

        if(systemFormTempService.save(systemFormTemp)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

//    /**
//     * 删除表单模板
//     * @param id Integer
//     * @author Mr.Zhang
//     * @since 2020-05-15
//     */
//    @ApiOperation(value = "删除")
//    @RequestMapping(value = "/delete", method = RequestMethod.GET)
//    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
//        if(systemFormTempService.removeById(id)){
//            return CommonResult.success();
//        }else{
//            return CommonResult.failed();
//        }
//    }

    /**
     * 修改表单模板
     * @param id integer id
     * @param systemFormTempRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-15
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated SystemFormTempRequest systemFormTempRequest){
        try{
            JSONObject.parseObject(systemFormTempRequest.getContent(), SystemConfigFormVo.class);
        }catch (Exception e){
            throw new CrmebException("模板表单 【" + systemFormTempRequest.getName() + "】 的内容不是正确的JSON格式！");
        }

        SystemFormTemp systemFormTemp = new SystemFormTemp();
        BeanUtils.copyProperties(systemFormTempRequest, systemFormTemp);
        systemFormTemp.setId(id);

        if(systemFormTempService.updateById(systemFormTemp)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询表单模板信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-15
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemFormTemp> info(@RequestParam(value = "id") Integer id){
        SystemFormTemp systemFormTemp = systemFormTempService.getById(id);
        return CommonResult.success(systemFormTemp);
   }
}



