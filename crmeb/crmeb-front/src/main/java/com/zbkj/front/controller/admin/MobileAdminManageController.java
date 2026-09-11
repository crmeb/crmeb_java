package com.zbkj.front.controller.admin;

import com.zbkj.common.response.MobileAdminManageStatisticsResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.service.MobileAdminManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 移动端商家工作台
 */
@RestController
@RequestMapping("api/front/manage")
@Api(tags = "移动端商家管理-工作台")
public class MobileAdminManageController {

    @Autowired
    private MobileAdminManageService mobileAdminManageService;

    @ApiOperation(value = "商家工作台统计")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public CommonResult<MobileAdminManageStatisticsResponse> statistics() {
        return CommonResult.success(mobileAdminManageService.getStatistics());
    }
}
