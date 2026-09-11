package com.zbkj.admin.controller;

import cn.hutool.core.util.ObjectUtil;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.SystemFormTemp;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemFormTempRequest;
import com.zbkj.common.request.SystemFormTempSearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.result.SystemConfigResultCode;
import com.zbkj.service.service.SystemFormTempService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;


/**
 * 表单模板 前端控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
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
     */
    @PreAuthorize("hasAuthority('admin:system:form:list')")
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemFormTemp>> getList(@Validated SystemFormTempSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        CommonPage<SystemFormTemp> systemFormTempCommonPage = CommonPage.restPage(systemFormTempService.getList(request, pageParamRequest));
        return CommonResult.success(systemFormTempCommonPage);
    }

    /**
     * 新增表单模板
     * @param systemFormTempRequest 新增参数
     */
    @PreAuthorize("hasAuthority('admin:system:form:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated SystemFormTempRequest systemFormTempRequest) {
        if (systemFormTempService.add(systemFormTempRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改表单模板
     * @param id integer id
     * @param systemFormTempRequest 修改参数
     */
    @PreAuthorize("hasAuthority('admin:system:form:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated SystemFormTempRequest systemFormTempRequest) {
        if (systemFormTempService.edit(id, systemFormTempRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 查询表单模板信息
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:system:form:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemFormTemp> info(@RequestParam(value = "id") Integer id) {
        SystemFormTemp systemFormTemp = systemFormTempService.getById(id);
        return CommonResult.success(systemFormTemp);
   }

    @PreAuthorize("hasAuthority('admin:system:form:name:info')")
    @ApiOperation(value = "通过名称查询详情")
    @RequestMapping(value = "/name/info", method = RequestMethod.GET)
    @ApiImplicitParam(name="name", value="表单模板Name", required = true)
    public SystemFormTemp nameInfo(@NotBlank(message = "name不能为空") @RequestParam(value = "name") String name) {
        SystemFormTemp temp = systemFormTempService.getOneByName(name);
        if (ObjectUtil.isNull(temp)) {
            throw new CrmebException(SystemConfigResultCode.FORM_TEMP_NOT_EXIST);
        }
        return temp;
    }

}



