package com.zbkj.admin.filter;

import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.utils.JwtTokenUtil;
import com.zbkj.common.vo.LoginUserVo;
import com.zbkj.service.service.impl.UserDetailServiceImpl;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * token验证处理
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
@Component
public class TokenComponent {

    @Resource
    private UserDetailServiceImpl userDetailService;

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUserVo getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StrUtil.isNotEmpty(token)) {
            try {
                Claims claims = JwtTokenUtil.parseToken(token);
                if (!JwtTokenUtil.isTokenType(claims, Constants.JWT_TOKEN_TYPE_ADMIN)) {
                    return null;
                }
                String account = claims.get("account", String.class);
                if (StrUtil.isBlank(account)) {
                    return null;
                }
                LoginUserVo loginUser = (LoginUserVo) userDetailService.loadUserByUsername(account);
                loginUser.setToken(token);
                loginUser.setLoginTime(claims.getIssuedAt().getTime());
                loginUser.setExpireTime(claims.getExpiration().getTime());
                return loginUser;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUserVo loginUser) {
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token) {
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(LoginUserVo loginUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", loginUser.getUser().getId());
        claims.put("account", loginUser.getUser().getAccount());
        String token = JwtTokenUtil.createToken(String.valueOf(loginUser.getUser().getId()), Constants.JWT_TOKEN_TYPE_ADMIN, claims, Constants.JWT_ADMIN_EXPIRE_MINUTES);
        loginUser.setToken(token);
        return token;
    }

    /**
     * JWT通过自身过期时间校验，不再按请求刷新服务端缓存。
     *
     * @param loginUser LoginUserVo
     */
    public void verifyToken(LoginUserVo loginUser) {
    }

    /**
     * JWT无状态，不再刷新服务端缓存。
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUserVo loginUser) {
    }

    /**
     * 获取请求token
     *
     * @param request HttpServletRequest
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        return JwtTokenUtil.resolveBearerToken(request.getHeader(Constants.HEADER_AUTHORIZATION_KEY));
    }

}
