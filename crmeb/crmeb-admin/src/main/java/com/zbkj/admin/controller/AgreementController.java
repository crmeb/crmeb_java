package com.zbkj.admin.controller;

import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

/**
 * 文字协议 控制器
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
@RequestMapping("api/admin/agreement")
@Api(tags = "协议管理")
public class AgreementController {
    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 保存用户注册协议
     */
    @PreAuthorize("hasAuthority('admin:agreement:user:save')")
    @ApiOperation(value = "用户注册协议 保存")
    @RequestMapping(value = "/usersave", method = RequestMethod.POST)
    public CommonResult<Boolean> userAgreementSave(@RequestBody @NotEmpty String agreement) {
        return CommonResult.success(systemConfigService.updateOrSaveValueByName(SysConfigConstants.USER_REGISTER_AGREEMENT, agreement));
    }

    /**
     * 获取用户注册协议
     */
    @PreAuthorize("hasAuthority('admin:agreement:user:info')")
    @ApiOperation(value = "用户注册协议 详情")
    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public CommonResult<String> userAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.USER_REGISTER_AGREEMENT));
    }

    /**
     * 保存商户入住协议
     */
    @PreAuthorize("hasAuthority('admin:agreement:merincomming:save')")
    @ApiOperation(value = "商户入驻协议 保存")
    @RequestMapping(value = "/merincommingsave", method = RequestMethod.POST)
    public CommonResult<Boolean> merincommingAgreementSave(@RequestBody @NotEmpty String agreement) {
        return CommonResult.success(systemConfigService.updateOrSaveValueByName(SysConfigConstants.MERCHANT_SETTLEMENT_AGREEMENT, agreement));
    }

    /**
     * 获取商户入住协议
     */
    @PreAuthorize("hasAuthority('admin:agreement:merincomming:info')")
    @ApiOperation(value = "商户入驻协议 详情")
    @RequestMapping(value = "/merincomminginfo", method = RequestMethod.GET)
    public CommonResult<String> merincommingAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.MERCHANT_SETTLEMENT_AGREEMENT));
    }

    /**
     * 保存用户隐私协议
     */
    @PreAuthorize("hasAuthority('admin:agreement:userprivacy:save')")
    @ApiOperation(value = "用户隐私协议 保存")
    @RequestMapping(value = "/userprivacysave", method = RequestMethod.POST)
    public CommonResult<Boolean> userPrivacyAgreementSave(@RequestBody @NotEmpty String agreement) {
        return CommonResult.success(systemConfigService.updateOrSaveValueByName(SysConfigConstants.USER_PRIVACY_AGREEMENT, agreement));
    }

    /**
     * 获取用户隐私协议
     */
    @PreAuthorize("hasAuthority('admin:agreement:userprivacy:info')")
    @ApiOperation(value = "用户隐私协议 详情")
    @RequestMapping(value = "/userprivacyinfo", method = RequestMethod.GET)
    public CommonResult<String> userPrivacyAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.USER_PRIVACY_AGREEMENT));
    }

    /**
     * 保存用户注销协议
     */
    @PreAuthorize("hasAuthority('admin:agreement:useraccountcancel:save')")
    @ApiOperation(value = "用户注销协议 保存")
    @RequestMapping(value = "/useraccountcancelsave", method = RequestMethod.POST)
    public CommonResult<Boolean> userAccountCancelAgreementSave(@RequestBody @NotEmpty String agreement) {
        return CommonResult.success(systemConfigService.updateOrSaveValueByName(SysConfigConstants.USER_CANCEL_AGREEMENT, agreement));
    }

    /**
     * 获取用户注销协议
     */
    @PreAuthorize("hasAuthority('admin:agreement:useraccountcancel:info')")
    @ApiOperation(value = "用户注销协议 详情")
    @RequestMapping(value = "/useraccountcancelinfo", method = RequestMethod.GET)
    public CommonResult<String> userAccountCancelAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.USER_CANCEL_AGREEMENT));
    }


    /**
     * 保存用户注销重要提示
     */
    @PreAuthorize("hasAuthority('admin:agreement:useraccountcancelnotice:save')")
    @ApiOperation(value = "用户注销声明 保存")
    @RequestMapping(value = "/useraccountcancelnoticesave", method = RequestMethod.POST)
    public CommonResult<Boolean> userAccountCancelNoticeAgreementSave(@RequestBody @NotEmpty String agreement) {
        return CommonResult.success(systemConfigService.updateOrSaveValueByName(SysConfigConstants.USER_CANCEL_NOTICE_AGREEMENT, agreement));
    }

    /**
     * 获取用户注销声明重要提示
     */
    @PreAuthorize("hasAuthority('admin:agreement:useraccountcancelnotice:info')")
    @ApiOperation(value = "用户注销声明 详情")
    @RequestMapping(value = "/useraccountcancelnoticeinfo", method = RequestMethod.GET)
    public CommonResult<String> userAccountCancelNoticeAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.USER_CANCEL_NOTICE_AGREEMENT));
    }

    /**
     * 关于我们协议保存
     */
    @PreAuthorize("hasAuthority('admin:agreement:aboutus:save')")
    @ApiOperation(value = "关于我们协议 保存")
    @RequestMapping(value = "/aboutussave", method = RequestMethod.POST)
    public CommonResult<Boolean> aboutusAgreementSave(@RequestBody @NotEmpty String agreement) {
        return CommonResult.success(systemConfigService.updateOrSaveValueByName(SysConfigConstants.ABOUTUS_AGREEMENT, agreement));
    }

    /**
     * 关于我们协议 详情
     */
    @PreAuthorize("hasAuthority('admin:agreement:aboutus:info')")
    @ApiOperation(value = "关于我们协议 详情")
    @RequestMapping(value = "/aboutusinfo", method = RequestMethod.GET)
    public CommonResult<String> aboutusAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.ABOUTUS_AGREEMENT));
    }

    /**
     * 平台资质证明 保存
     */
    @PreAuthorize("hasAuthority('admin:agreement:intelligent:save')")
    @ApiOperation(value = "平台资质证明 保存")
    @RequestMapping(value = "/intelligentsave", method = RequestMethod.POST)
    public CommonResult<Boolean> intelligentAgreementSave(@RequestBody @NotEmpty String agreement) {
        return CommonResult.success(systemConfigService.updateOrSaveValueByName(SysConfigConstants.PLATFROM_INTELLIGENT_AGREEMENT, agreement));
    }

    /**
     * 平台资质证明 详情
     */
    @PreAuthorize("hasAuthority('admin:agreement:intelligent:info')")
    @ApiOperation(value = "平台资质证明 详情")
    @RequestMapping(value = "/intelligentinfo", method = RequestMethod.GET)
    public CommonResult<String> intelligentAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.PLATFROM_INTELLIGENT_AGREEMENT));
    }
    

    @PreAuthorize("hasAuthority('admin:agreement:coupon:agreement:save')")
    @ApiOperation(value = "优惠券规则 保存")
    @RequestMapping(value = "/coupon/agreement/save", method = RequestMethod.POST)
    public CommonResult<Boolean> couponAgreementSave(@RequestBody @NotEmpty String agreement) {
        return CommonResult.success(systemConfigService.updateOrSaveValueByName(SysConfigConstants.COUPON_AGREEMENT, agreement));
    }

    @PreAuthorize("hasAuthority('admin:agreement:coupon:agreement:info')")
    @ApiOperation(value = "优惠券规则 详情")
    @RequestMapping(value = "/coupon/agreement/info", method = RequestMethod.GET)
    public CommonResult<String> couponAgreementInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.COUPON_AGREEMENT));
    }
    
}
