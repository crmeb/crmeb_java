package com.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.common.CheckFrontToken;
import com.common.CommonResult;
import com.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  移动端管理端 token验证拦截器 使用前注意需要一个@Bean手动注解，否则注入无效
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
public class FrontTokenInterceptor implements HandlerInterceptor {
    @Autowired
    private CheckFrontToken checkFrontToken;

    //程序处理之前需要处理的业务
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        String token = checkFrontToken.getTokenFormRequest(request);

        if(token == null || token.isEmpty()){
            //判断路由，部分路由不管用户是否登录都可以访问
            boolean result = checkFrontToken.checkRouter(RequestUtil.getUri(request));
            if(result){
                return true;
            }

            response.getWriter().write(JSONObject.toJSONString(CommonResult.unauthorized()));
            return false;
        }

        Boolean result = checkFrontToken.check(token, request);
        if(!result){
            response.getWriter().write(JSONObject.toJSONString(CommonResult.unauthorized()));
            return false;
        }
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

}
