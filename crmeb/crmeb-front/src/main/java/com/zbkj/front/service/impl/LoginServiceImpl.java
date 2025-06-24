package com.zbkj.front.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.SmsConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.LoginMobileRequest;
import com.zbkj.common.request.LoginRequest;
import com.zbkj.common.response.LoginConfigResponse;
import com.zbkj.common.response.LoginResponse;
import com.zbkj.common.token.FrontTokenComponent;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.front.service.LoginService;
import com.zbkj.service.service.SystemConfigService;
import com.zbkj.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 移动端登录服务类
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
@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FrontTokenComponent tokenComponent;
    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 账号密码登录
     *
     * @return LoginResponse
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userService.getByPhone(loginRequest.getPhone());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("此账号未注册");
        }
        if (!user.getStatus()) {
            throw new CrmebException("此账号被禁用");
        }

        // 校验密码
        String password = CrmebUtil.encryptPassword(loginRequest.getPassword(), loginRequest.getPhone());
        if (!user.getPwd().equals(password)) {
            throw new CrmebException("密码错误");
        }

        LoginResponse loginResponse = new LoginResponse();
        String token = tokenComponent.createToken(user);
        loginResponse.setToken(token);

        //绑定推广关系
        if (loginRequest.getSpreadPid() > 0) {
            bindSpread(user, loginRequest.getSpreadPid());
        }

        // 记录最后一次登录时间
        user.setLastLoginTime(CrmebDateUtil.nowDateTime());
        user.setUpdateTime(DateUtil.date());
        userService.updateById(user);

        loginResponse.setUid(user.getUid());
        loginResponse.setNikeName(user.getNickname());
        loginResponse.setPhone(user.getPhone());
        return loginResponse;
    }

    /**
     * 手机号验证码登录
     *
     * @param loginRequest 登录请求信息
     * @return LoginResponse
     */
    @Override
    public LoginResponse phoneLogin(LoginMobileRequest loginRequest) {
        //检测验证码
        checkValidateCode(loginRequest.getPhone(), loginRequest.getCaptcha());
        Integer spreadPid = Optional.ofNullable(loginRequest.getSpreadPid()).orElse(0);
        //查询手机号信息
        User user = userService.getByPhone(loginRequest.getPhone());
        if (ObjectUtil.isNull(user)) {// 此用户不存在，走新用户注册流程
            user = userService.registerPhone(loginRequest.getPhone(), spreadPid);
        } else {
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }
            if (user.getSpreadUid().equals(0) && spreadPid > 0) {
                // 绑定推广关系
                bindSpread(user, spreadPid);
            }
            // 记录最后一次登录时间
            user.setLastLoginTime(CrmebDateUtil.nowDateTime());
            user.setUpdateTime(DateUtil.date());
            boolean b = userService.updateById(user);
            if (!b) {
                logger.error("用户登录时，记录最后一次登录时间出错,uid = " + user.getUid());
            }
        }

        //生成token
        LoginResponse loginResponse = new LoginResponse();
        String token = tokenComponent.createToken(user);
        loginResponse.setToken(token);
        loginResponse.setUid(user.getUid());
        loginResponse.setNikeName(user.getNickname());
        loginResponse.setPhone(user.getPhone());
        return loginResponse;
    }

    /**
     * 检测手机验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    private void checkValidateCode(String phone, String code) {
        Object validateCode = redisUtil.get(SmsConstants.SMS_VALIDATE_PHONE + phone);
        if (ObjectUtil.isNull(validateCode)) {
            throw new CrmebException("验证码已过期");
        }
        if (!validateCode.toString().equals(code)) {
            throw new CrmebException("验证码错误");
        }
        //删除验证码
        redisUtil.delete(SmsConstants.SMS_VALIDATE_PHONE + phone);
    }

    /**
     * 绑定分销关系
     *
     * @param user      User 用户user类
     * @param spreadUid Integer 推广人id
     * @return Boolean
     * 1.判断分销功能是否启用
     * 2.判断分销模式
     * 3.根据不同的分销模式校验
     * 4.指定分销，只有分销员才可以分销，需要spreadUid是推广员才可以绑定
     * 5.满额分销，同上
     * 6.人人分销，可以直接绑定
     */
    @Override
    public Boolean bindSpread(User user, Integer spreadUid) {
        Boolean checkBingSpread = userService.checkBingSpread(user, spreadUid, "old");
        if (!checkBingSpread) return false;

        user.setSpreadUid(spreadUid);
        user.setSpreadTime(CrmebDateUtil.nowDateTime());
        user.setUpdateTime(DateUtil.date());

        Boolean execute = transactionTemplate.execute(e -> {
            userService.updateById(user);
            userService.updateSpreadCountByUid(spreadUid, "add");
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("绑定推广人时出错，userUid = {}, spreadUid = {}", user.getUid(), spreadUid));
        }
        return execute;
    }

    /**
     * 推出登录
     *
     * @param request HttpServletRequest
     */
    @Override
    public void loginOut(HttpServletRequest request) {
        tokenComponent.logout(request);
    }

    /**
     * 校验token是否有效
     *
     * @return true 有效， false 无效
     */
    @Override
    public Boolean tokenIsExist() {
        Integer userId = userService.getUserId();
        return userId > 0;
    }

    /**
     * 获取登录配置
     */
    @Override
    public LoginConfigResponse getLoginConfig() {
        String routinePhoneVerification = systemConfigService.getValueByKey(Constants.WECHAT_ROUTINE_PHONE_VERIFICATION);
        String publicLoginType = systemConfigService.getValueByKey(Constants.WECHAT_PUBLIC_LOGIN_TYPE);
        String mobileLoginLogo = systemConfigService.getValueByKey(Constants.CONFIG_KEY_MOBILE_LOGIN_LOGO);
        String siteName = systemConfigService.getValueByKey(Constants.CONFIG_KEY_SITE_NAME);
        LoginConfigResponse response = new LoginConfigResponse();
        response.setPublicLoginType(publicLoginType);
        response.setRoutinePhoneVerification(routinePhoneVerification);
        response.setMobileLoginLogo(mobileLoginLogo);
        response.setSiteName(siteName);
        return response;
    }
}
