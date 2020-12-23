package com.zbkj.crmeb.front.controller;


import com.common.CheckAdminToken;
import com.common.CommonResult;
import com.utils.CrmebUtil;
import com.utils.ValidateFormUtil;
import com.zbkj.crmeb.front.request.LoginMobileRequest;
import com.zbkj.crmeb.front.request.LoginRequest;
import com.zbkj.crmeb.front.request.RegisterRequest;
import com.zbkj.crmeb.front.response.LoginResponse;
import com.zbkj.crmeb.sms.service.SmsService;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户登陆 前端控制器
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController("FrontLoginController")
@RequestMapping("api/front")
@Api(tags = "用户 -- 登录注册")
public class LoginController {
    @Autowired
    private UserService userService;

    @Autowired
    CheckAdminToken checkAdminToken;

    @Autowired
    private SmsService smsService;


    /**
     * 手机号登录接口
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "手机号登录接口")
    @RequestMapping(value = "/login/mobile", method = RequestMethod.POST)
    public CommonResult<LoginResponse> phoneLogin(@RequestBody @Validated LoginMobileRequest loginRequest, HttpServletRequest request) throws Exception {
        String clientIp = CrmebUtil.getClientIp(request);
        RegisterRequest registerRequest = new RegisterRequest();
        BeanUtils.copyProperties(loginRequest, registerRequest);
        return CommonResult.success(userService.register(registerRequest, clientIp));
    }

    /**
     * 账号密码登录
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "账号密码登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResult<LoginResponse> login(@RequestBody @Validated LoginRequest loginRequest) throws Exception {
        return CommonResult.success(userService.login(loginRequest));
    }


    /**
     * 退出登录
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "退出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public CommonResult<String> loginOut(HttpServletRequest request){
        userService.loginOut(checkAdminToken.getTokenFormRequest(request));
        return CommonResult.success();
    }

    /**
     * 发送短信
     * @param phone 手机号码
     * @return 发送是否成功
     */
    @ApiOperation(value = " 发送短信")
    @RequestMapping(value = "/sendCode", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="phone", value="手机号码", required = true)
    })
    public CommonResult<Object> sendCode(@RequestParam String phone){
        ValidateFormUtil.isPhone(phone,"手机号码错误");
        if(smsService.pushCodeToList(phone,1, null)){
            return CommonResult.success("发送成功");
        }else{
            return CommonResult.failed("发送失败");
        }
    }
}



