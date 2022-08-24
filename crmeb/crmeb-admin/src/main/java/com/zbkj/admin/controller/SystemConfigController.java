package com.zbkj.admin.controller;

import com.zbkj.common.response.CommonResult;
import com.zbkj.common.model.system.SystemConfig;
import com.zbkj.common.request.SystemConfigAdminRequest;
import com.zbkj.common.request.SystemFormCheckRequest;
import com.zbkj.service.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


/**
 * 配置表 前端控制器
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
@RequestMapping("api/admin/system/config")
@Api(tags = "设置 -- Config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 查询配置表信息
     * @param formId Integer
     */
    @PreAuthorize("hasAuthority('admin:system:config:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> info(@RequestParam(value = "formId") Integer formId) {
        return CommonResult.success(systemConfigService.info(formId));
    }


    /**
     * 整体保存表单数据
     * @param systemFormCheckRequest SystemFormCheckRequest 新增参数
     */
    @PreAuthorize("hasAuthority('admin:system:config:save:form')")
    @ApiOperation(value = "整体保存表单数据")
    @RequestMapping(value = "/save/form", method = RequestMethod.POST)
    public CommonResult<String> saveFrom(@RequestBody @Validated SystemFormCheckRequest systemFormCheckRequest) {
        if (systemConfigService.saveForm(systemFormCheckRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 检测表单name是否存在
     * @param name name
     */
    @PreAuthorize("hasAuthority('admin:system:config:check')")
    @ApiOperation(value = "检测表单name是否存在")
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public CommonResult<Boolean> check(@RequestParam String name) {
        return CommonResult.success(systemConfigService.checkName(name));
    }

    /**
     * 配置表中仅仅存储对应的配置
     * @param key 配置表中的配置字段
     * @param value 对应的值
     */
    @PreAuthorize("hasAuthority('admin:system:config:saveuniq')")
    @ApiOperation(value = "表单配置中仅仅存储")
    @RequestMapping(value = "/saveuniq", method = RequestMethod.POST)
    public CommonResult<Boolean> justSaveUniq(@RequestParam String key, @RequestParam String value) {
        return CommonResult.success(systemConfigService.updateOrSaveValueByName(key, value));
    }

    /**
     * 根据key获取表单配置数据
     * @param key 配置表的的字段
     */
    @PreAuthorize("hasAuthority('admin:system:config:getuniq')")
    @ApiOperation(value = "表单配置根据key获取")
    @RequestMapping(value = "/getuniq", method = RequestMethod.GET)
    public CommonResult<Object> justGetUniq(@RequestParam String key) {
        return CommonResult.success(systemConfigService.getValueByKey(key),"success");
    }

    /**
     * 根据key获取配置
     */
    @PreAuthorize("hasAuthority('admin:system:config:get')")
    @ApiOperation(value = "根据key获取配置")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public CommonResult<List<SystemConfig>> getByKey(@RequestParam String key) {
        return CommonResult.success(systemConfigService.getListByKey(key));
    }

    /**
     * 更新配置信息
     */
    @PreAuthorize("hasAuthority('admin:system:config:update')")
    @ApiOperation(value = "更新配置信息")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<List<SystemConfig>> getByKey(@RequestBody @Validated List<SystemConfigAdminRequest> requestList) {
        if (systemConfigService.updateByList(requestList)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



