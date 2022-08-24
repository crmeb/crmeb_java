package com.zbkj.admin.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.AdminIntegralSearchRequest;
import com.zbkj.common.response.UserIntegralRecordResponse;
import com.zbkj.service.service.UserIntegralRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户积分管理控制器
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
@RequestMapping("api/admin/user/integral")
@Api(tags = "用户积分管理")
public class UserIntegralController {

    @Autowired
    private UserIntegralRecordService integralRecordService;

    /**
     * 积分分页列表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:user:integral:list')")
    @ApiOperation(value = "积分分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult<CommonPage<UserIntegralRecordResponse>> getList(@RequestBody @Validated AdminIntegralSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        CommonPage<UserIntegralRecordResponse> restPage = CommonPage.restPage(integralRecordService.findAdminList(request, pageParamRequest));
        return CommonResult.success(restPage);
    }


}
