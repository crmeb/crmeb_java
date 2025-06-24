package com.zbkj.front.pub;

import com.zbkj.common.constants.CopyrightConstants;
import com.zbkj.common.response.CopyRightResponse;
import com.zbkj.common.response.PayConfigResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公开设置控制器
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
@RequestMapping("api/public/config")
@Api(tags = "公开设置控制器")
public class PubliclyConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @ApiOperation(value = "获取版权")
    @RequestMapping(value = "/get/copyright", method = RequestMethod.GET)
    public CommonResult<CopyRightResponse> getCopyright() {
        CopyRightResponse copyRightResponse = new CopyRightResponse();
        copyRightResponse.setCopyrightIcpNumber(systemConfigService.getValueByKey(CopyrightConstants.COPYRIGHT_ICP_NUMBER));
        copyRightResponse.setCopyrightIcpNumberUrl(systemConfigService.getValueByKey(CopyrightConstants.COPYRIGHT_ICP_NUMBER_URL));
        copyRightResponse.setCopyrightInternetRecord(systemConfigService.getValueByKey(CopyrightConstants.COPYRIGHT_INTERNET_RECORD));
        copyRightResponse.setCopyrightInternetRecordUrl(systemConfigService.getValueByKey(CopyrightConstants.COPYRIGHT_INTERNET_RECORD_URL));
        return CommonResult.success(copyRightResponse);
    }

    @ApiOperation(value = "获取移动端域名")
    @RequestMapping(value = "/get/front/domain", method = RequestMethod.GET)
    public CommonResult<String> getFrontDomain() {
        return CommonResult.success(systemConfigService.getFrontDomain());
    }

    @ApiOperation(value = "获取平台当前的素材地址")
    @RequestMapping(value = "/get/admin/mediadomain", method = RequestMethod.GET)
    public CommonResult<String> getMediaDomain() {
        return CommonResult.success(systemConfigService.getMediaDomain());
    }


}
