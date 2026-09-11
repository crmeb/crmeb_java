package com.zbkj.front.controller;

import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.constants.UserLevelConstants;
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
 * 协议控制器
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
@RequestMapping("api/front/agreement")
@Api(tags = "协议控制器")
public class AgreementController {

    @Autowired
    private SystemConfigService systemConfigService;

    @ApiOperation(value = "关于我们协议 详情")
    @RequestMapping(value = "/aboutusinfo", method = RequestMethod.GET)
    public CommonResult<String> aboutusAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.ABOUTUS_AGREEMENT));
    }

    @ApiOperation(value = "平台资质证明 详情")
    @RequestMapping(value = "/intelligentinfo", method = RequestMethod.GET)
    public CommonResult<String> intelligentAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.PLATFROM_INTELLIGENT_AGREEMENT));
    }


    @ApiOperation(value = "用户注册协议 详情")
    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public CommonResult<String> userAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.USER_REGISTER_AGREEMENT));
    }

    @ApiOperation(value = "用户隐私协议 详情")
    @RequestMapping(value = "/userprivacyinfo", method = RequestMethod.GET)
    public CommonResult<String> userPrivacyAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.USER_PRIVACY_AGREEMENT));
    }

    @ApiOperation(value = "用户注销协议 详情")
    @RequestMapping(value = "/useraccountcancelinfo", method = RequestMethod.GET)
    public CommonResult<String> userAccountCancelAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.USER_CANCEL_AGREEMENT));
    }

    @ApiOperation(value = "商户入驻协议 详情")
    @RequestMapping(value = "/merincomminginfo", method = RequestMethod.GET)
    public CommonResult<String> merincommingAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.MERCHANT_SETTLEMENT_AGREEMENT));
    }

    @ApiOperation(value = "用户注销声明 详情")
    @RequestMapping(value = "/useraccountcancelnoticeinfo", method = RequestMethod.GET)
    public CommonResult<String> userAccountCancelNoticeAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.USER_CANCEL_NOTICE_AGREEMENT));
    }

    @ApiOperation(value = "用户等级规则说明")
    @RequestMapping(value = "/user/level/rule", method = RequestMethod.GET)
    public String userLevelRule() {
        return systemConfigService.getValueByKey(UserLevelConstants.SYSTEM_USER_LEVEL_RULE);
    }

    @ApiOperation(value = "优惠券规则 详情")
    @RequestMapping(value = "/coupon/agreement/info", method = RequestMethod.GET)
    public CommonResult<String> couponAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.COUPON_AGREEMENT));
    }

}
