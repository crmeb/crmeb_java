package com.zbkj.crmeb.express.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.express.request.ExpressRequest;
import com.zbkj.crmeb.express.request.ExpressSearchRequest;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.express.service.ExpressService;
import com.zbkj.crmeb.express.model.Express;


/**
 * 快递公司表 前端控制器
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/express")
@Api(tags = "设置 -- 物流 -- 公司")

public class ExpressController {

    @Autowired
    private ExpressService expressService;

    /**
     * 分页显示快递公司表
     * @param request ExpressSearchRequest 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParam(name="keywords", value="搜索关键字")
    public CommonResult<CommonPage<Express>>  getList(@Validated ExpressSearchRequest request,
                                                      @ModelAttribute PageParamRequest pageParamRequest){
        CommonPage<Express> expressCommonPage = CommonPage.restPage(expressService.getList(request, pageParamRequest));
        return CommonResult.success(expressCommonPage);
    }

    /**
     * 新增快递公司表
     * @param expressRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@ModelAttribute ExpressRequest expressRequest){
        Express express = new Express();
        BeanUtils.copyProperties(expressRequest, express);

        if(expressService.save(express)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除快递公司表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="快递ID")
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(expressService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改快递公司表
     * @param id integer id
     * @param expressRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParam(name="id", value="快递ID")
    public CommonResult<String> update(@RequestParam Integer id, @ModelAttribute ExpressRequest expressRequest){
        Express express = new Express();
        BeanUtils.copyProperties(expressRequest, express);
        express.setId(id);

        if(expressService.updateById(express)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询快递公司表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="快递ID")
    public CommonResult<Express> info(@RequestParam(value = "id") Integer id){
        Express express = expressService.getById(id);
        return CommonResult.success(express);
   }
}



