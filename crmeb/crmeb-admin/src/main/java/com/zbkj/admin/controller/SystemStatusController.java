package com.zbkj.admin.controller;


import com.zbkj.admin.service.SystemStatusService;
import com.zbkj.common.response.RoleInfoResponse;
import com.zbkj.common.response.SystemStatusResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.SystemAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统状态
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
@RequestMapping("api/admin/system/status")
@Api(tags = "系统状态")
public class SystemStatusController {

    @Autowired
    private SystemStatusService systemStatusService;

    /**
     * 查询java运行环境提供的系统相关数据
     */
    //@PreAuthorize("hasAuthority('admin:system:status:info')")
    @ApiOperation(value = "查询java运行环境提供的系统相关数据")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemStatusResponse> info(){
        return CommonResult.success(systemStatusService.getInfo());
    }

}
