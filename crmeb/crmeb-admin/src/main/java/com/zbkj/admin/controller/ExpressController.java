package com.zbkj.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ExpressUpdateRequest;
import com.zbkj.common.request.ExpressSearchRequest;
import com.zbkj.common.request.ExpressUpdateShowRequest;
import com.zbkj.service.service.ExpressService;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.zbkj.common.model.express.Express;

import java.util.List;


/**
 * 快递公司表 前端控制器
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
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
     */
    @PreAuthorize("hasAuthority('admin:express:list')")
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParam(name="keywords", value="搜索关键字")
    public CommonResult<CommonPage<Express>>  getList(@Validated ExpressSearchRequest request,
                                                      @ModelAttribute PageParamRequest pageParamRequest) {
        CommonPage<Express> expressCommonPage = CommonPage.restPage(expressService.getList(request, pageParamRequest));
        return CommonResult.success(expressCommonPage);
    }

    /**
     * 编辑快递公司
     */
    @PreAuthorize("hasAuthority('admin:express:update')")
    @ApiOperation(value = "编辑")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ExpressUpdateRequest expressRequest) {
        if (expressService.updateExpress(expressRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     *修改显示状态
     */
    @PreAuthorize("hasAuthority('admin:express:update:show')")
    @ApiOperation(value = "修改显示状态")
    @RequestMapping(value = "/update/show", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ExpressUpdateShowRequest expressRequest) {
        if (expressService.updateExpressShow(expressRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 同步物流公司
     */
    @PreAuthorize("hasAuthority('admin:express:sync')")
    @ApiOperation(value = "同步物流公司")
    @RequestMapping(value = "/sync/express", method = RequestMethod.POST)
    public CommonResult<String> syncExpress() {
        if (expressService.syncExpress()) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }


    /**
     * 查询快递公司表信息
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:express:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="快递公司ID", required = true)
    public CommonResult<Express> info(@RequestParam(value = "id") Integer id) {
        return CommonResult.success(expressService.getInfo(id));
   }

    /**
     * 查询全部物流公司
     */
    @PreAuthorize("hasAuthority('admin:express:all')")
    @ApiOperation(value = "查询全部物流公司")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ApiImplicitParam(name="type", value="类型：normal-普通，elec-电子面单")
    public CommonResult<List<Express>> all(@RequestParam(value = "type") String type) {
        return CommonResult.success(expressService.findAll(type));
    }

    /**
     * 查询物流公司面单模板
     */
    @PreAuthorize("hasAuthority('admin:express:template')")
    @ApiOperation(value = "查询物流公司面单模板")
    @RequestMapping(value = "/template", method = RequestMethod.GET)
    @ApiImplicitParam(name="com", value="快递公司编号", required = true)
    public CommonResult<JSONObject> template(@RequestParam(value = "com") String com) {
        return CommonResult.success(expressService.template(com));
    }
}



