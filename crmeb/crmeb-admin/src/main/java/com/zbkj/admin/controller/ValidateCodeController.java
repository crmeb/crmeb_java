package com.zbkj.admin.controller;

import com.zbkj.common.response.CommonResult;
import com.zbkj.admin.service.ValidateCodeService;
import com.zbkj.admin.vo.ValidateCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 验证码服务
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
@RequestMapping("api/admin/validate/code")
@Api(tags = "验证码服务")
public class ValidateCodeController {

    @Autowired
    private ValidateCodeService validateCodeService;

    /**
     * 获取图片验证码
     * @return CommonResult
     */
//    @PreAuthorize("hasAuthority('admin:validate:code:get')")
    @ApiOperation(value="获取验证码")
    @GetMapping(value = "/get")
    public CommonResult<ValidateCode> get() {
        ValidateCode validateCode = validateCodeService.get();
        return CommonResult.success(validateCode);
    }
}



