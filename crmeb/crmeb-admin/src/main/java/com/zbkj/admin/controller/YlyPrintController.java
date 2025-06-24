package com.zbkj.admin.controller;


import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.YlyPrintService;
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
 * 易联云打印订单
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/yly")
@Api(tags = "易联云 打印订单小票") //配合swagger使用
public class YlyPrintController {


    @Autowired
    private YlyPrintService ylyPrintService;

    @PreAuthorize("hasAuthority('admin:yly:print')")
    @ApiOperation(value = "打印小票")
    @RequestMapping(value = "/print/{ordid}", method = RequestMethod.GET)
    public CommonResult<String> updateStatus(@PathVariable  String ordid) {
        ylyPrintService.YlyPrint(ordid,false);
        return CommonResult.success();
    }
}
