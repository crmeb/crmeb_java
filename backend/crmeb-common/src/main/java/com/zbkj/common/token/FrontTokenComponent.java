package com.zbkj.common.token;

import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.model.user.User;
import com.zbkj.common.utils.JwtTokenUtil;
import com.zbkj.common.utils.RequestUtil;
import com.zbkj.common.vo.LoginUserVo;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
public class FrontTokenComponent {

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUserVo getLoginUser(HttpServletRequest request) {
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
     * @param user 用户信息
     * @return 令牌
     */
    public String createToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getUid());
        return JwtTokenUtil.createToken(String.valueOf(user.getUid()), Constants.JWT_TOKEN_TYPE_FRONT, claims, Constants.JWT_FRONT_EXPIRE_MINUTES);
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
    public String getToken(HttpServletRequest request) {
        return JwtTokenUtil.resolveBearerToken(request.getHeader(Constants.HEADER_AUTHORIZATION_KEY));
    }

    /**
     * 退出登录
     * @param request HttpServletRequest
     */
    public void logout(HttpServletRequest request) {
    }

    /**
     * 获取当前登录用户id
     */
    public Integer getUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = getToken(request);
        if (StrUtil.isEmpty(token)) {
            return null;
//            throw new CrmebException("登录信息已过期，请重新登录！");
        }
        try {
            Claims claims = JwtTokenUtil.parseToken(token);
            if (!JwtTokenUtil.isTokenType(claims, Constants.JWT_TOKEN_TYPE_FRONT)) {
                return null;
            }
            return claims.get("uid", Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    //路由在此处，则返回true，无论用户是否登录都可以访问
    public boolean checkRouter(String uri) {
        if (uri != null && uri.startsWith("api/front/theme_info")) {
            return true;
        }
        if (uri != null && uri.equals("api/front/theme/product")) {
            return true;
        }
        if (uri != null && uri.equals("api/front/theme/coupon")) {
            return true;
        }
        if (uri != null && uri.equals("api/front/theme/seckill")) {
            return true;
        }
        if (uri != null && uri.equals("api/front/theme/combination")) {
            return true;
        }
        if (uri != null && uri.equals("api/front/theme/bargain")) {
            return true;
        }
        if (uri != null && uri.equals("api/front/theme/article")) {
            return true;
        }
        String[] routerList = {
                "api/front/product/detail",
                "api/front/coupons",
                "api/front/index",
                "api/front/bargain/list",
                "api/front/combination/list",
                "api/front/seckill/list",
                "api/front/index/product",
                "api/front/combination/index",
                "api/front/bargain/index",
                "api/front/seckill/index",
                "api/front/index/color/config",
                "api/front/product/list",
                "api/front/product/sku/detail",
                "api/front/index/get/version",
                "api/front/image/domain",
                "api/front/product/leaderboard",
                "api/front/menu/user",
                "api/front/combination/header",
                "api/front/combination/detail",
                "api/front/bargain/header",
                "api/front/bargain/detail",
                "api/front/seckill/header",
                "api/front/seckill/detail"
        };

        return ArrayUtils.contains(routerList, uri);
    }

    public Boolean check(String token, HttpServletRequest request){

        try {
            Claims claims = JwtTokenUtil.parseToken(token);
            if (JwtTokenUtil.isTokenType(claims, Constants.JWT_TOKEN_TYPE_FRONT)) {
                return true;
            }
            return checkRouter(RequestUtil.getUri(request));
        }catch (Exception e){
            return checkRouter(RequestUtil.getUri(request));
        }
    }

    public void clearToken() {
    }

    public void clearUserToken(Integer uid) {
    }
}
