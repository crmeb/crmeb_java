package com.zbkj.admin.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ShippingTemplatesRequest;
import com.zbkj.common.request.ShippingTemplatesSearchRequest;
import com.zbkj.service.service.ShippingTemplatesService;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.zbkj.common.model.express.ShippingTemplates;

/**
 * 物流-模板控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/express/shipping/templates")
@Api(tags = "设置 -- 物流 -- 模板")
public class ShippingTemplatesController {

    @Autowired
    private ShippingTemplatesService shippingTemplatesService;

    /**
     * 分页显示
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:shipping:templates:list')")
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ShippingTemplates>>  getList(@Validated ShippingTemplatesSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<ShippingTemplates> shippingTemplatesCommonPage = CommonPage.restPage(shippingTemplatesService.getList(request, pageParamRequest));
        return CommonResult.success(shippingTemplatesCommonPage);
    }

    /**
     * 新增
     * @param request 新增参数
     */
    @PreAuthorize("hasAuthority('admin:shipping:templates:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated ShippingTemplatesRequest request){
        if (shippingTemplatesService.create(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed("新增运费模板失败");
    }

    /**
     * 删除
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:shipping:templates:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="模板ID", required = true)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(shippingTemplatesService.remove(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改
     * @param id integer id
     * @param request ShippingTemplatesRequest 修改参数
     */
    @PreAuthorize("hasAuthority('admin:shipping:templates:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated ShippingTemplatesRequest request){
        if (shippingTemplatesService.update(id, request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 查询信息
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:shipping:templates:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="模板ID", required = true)
    public CommonResult<ShippingTemplates> info(@RequestParam(value = "id") Integer id){
        return CommonResult.success(shippingTemplatesService.getInfo(id));
   }
}



