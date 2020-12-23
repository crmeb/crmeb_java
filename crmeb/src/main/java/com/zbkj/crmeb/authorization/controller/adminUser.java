package com.zbkj.crmeb.authorization.controller;

import com.common.CheckAdminToken;
import com.common.CommonResult;
import com.constants.Constants;
import com.exception.CrmebException;
import com.exception.ExceptionCodeEnum;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.system.request.SystemAdminLoginRequest;
import com.zbkj.crmeb.system.request.SystemAdminRequest;
import com.zbkj.crmeb.system.response.SystemAdminResponse;
import com.zbkj.crmeb.system.response.SystemGroupDataAdminLoginBannerResponse;
import com.zbkj.crmeb.system.service.SystemAdminService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.validatecode.model.ValidateCode;
import com.zbkj.crmeb.validatecode.service.ValidateCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin 平台用户
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin")
@Api(tags = "Admin 平台用户")
public class adminUser {

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private CheckAdminToken checkAdminToken;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Autowired
    private ValidateCodeService validateCodeService;

    @ApiOperation(value="AdminUserLogin")
    @PostMapping(value = "/login", produces = "application/json")
    public CommonResult<SystemAdminResponse> SystemAdminLogin(@RequestBody @Validated SystemAdminLoginRequest systemAdminLoginRequest, HttpServletRequest request) throws Exception {
        // 判断验证码
        ValidateCode validateCode = new ValidateCode(systemAdminLoginRequest.getKey(),systemAdminLoginRequest.getCode());
        boolean codeCheckResult = validateCodeService.check(validateCode);
        if(!codeCheckResult) throw new CrmebException("验证码不正确");
        // 执行登录
        SystemAdminRequest systemAdminRequest = new SystemAdminRequest();
        systemAdminRequest.setAccount(systemAdminLoginRequest.getAccount());
        systemAdminRequest.setPwd(systemAdminLoginRequest.getPwd());
        SystemAdminResponse systemAdminResponse = systemAdminService.login(systemAdminRequest, CrmebUtil.getClientIp(request));
        if(null == systemAdminResponse){
            return CommonResult.failed(ExceptionCodeEnum.FAILED, "login failed");
        }

        if(StringUtils.isNotBlank(systemAdminLoginRequest.getWxCode())){
            systemAdminService.bind(systemAdminLoginRequest.getWxCode(), systemAdminResponse.getId());
        }
        return CommonResult.success(systemAdminResponse, "login success");
    }

    @ApiOperation(value="AdminUserLogout")
    @GetMapping(value = "/logout")
    public CommonResult<SystemAdminResponse> SystemAdminLogout(HttpServletRequest request) throws Exception {
        String token = checkAdminToken.getTokenFormRequest(request);
//        TokenModel tokenModel = tokenManager.getToken(token);
//        if(StringUtils.isEmpty(tokenModel.getUserNo())){
//            throw new CrmebException("");
//        }
        systemAdminService.logout(token);
        return CommonResult.success("logout success");
    }

    @ApiOperation(value="GetAdminUserByToken")
    @GetMapping(value = "/getAdminInfoByToken")
    public CommonResult<SystemAdminResponse> getAdminInfo(HttpServletRequest request) throws Exception{
        String token = checkAdminToken.getTokenFormRequest(request);
        SystemAdminResponse systemAdminResponse = systemAdminService.getInfoByToken(token);

        return CommonResult.success(systemAdminResponse);
    }

    /**
     * 获取登录页图片
     * @return Map<String, Object>
     */
    @ApiOperation(value = "获取登录页图片")
    @RequestMapping(value = "/getLoginPic", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> getLoginPic(){
        Map<String, Object> map = new HashMap<>();
        //背景图
        map.put("backgroundImage", systemConfigService.getValueByKey(Constants.CONFIG_KEY_ADMIN_LOGIN_BACKGROUND_IMAGE));
        //logo
        map.put("logo", systemConfigService.getValueByKey(Constants.CONFIG_KEY_ADMIN_LOGIN_LOGO));
        //轮播图
        List<SystemGroupDataAdminLoginBannerResponse> bannerList = systemGroupDataService.getListByGid(Constants.GROUP_DATA_ID_ADMIN_LOGIN_BANNER_IMAGE_LIST, SystemGroupDataAdminLoginBannerResponse.class);
        map.put("banner", bannerList);
        return CommonResult.success(map);
    }

    /**
     * 微信登录公共号授权登录
     * @author Mr.Zhang
     * @since 2020-05-25
     */
    @ApiOperation(value = "微信登录公共号授权登录")
    @RequestMapping(value = "/authorize/login", method = RequestMethod.GET)
    public CommonResult<SystemAdminResponse> login(@RequestParam(value = "code") String code, HttpServletRequest request) throws Exception {
        return CommonResult.success(systemAdminService.weChatAuthorizeLogin(code, CrmebUtil.getClientIp(request)));
    }

    /**
     * 解绑微信
     * @author Mr.Zhang
     * @since 2020-05-25
     */
    @ApiOperation(value = "解绑微信")
    @RequestMapping(value = "/unbind", method = RequestMethod.GET)
    public CommonResult<Boolean> bind(){
        return CommonResult.success(systemAdminService.unBind());
    }
}
