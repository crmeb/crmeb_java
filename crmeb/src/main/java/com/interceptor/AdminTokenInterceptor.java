package com.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.common.CheckAdminToken;
import com.common.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *  PC管理端 token验证拦截器 使用前注意需要一个@Bean手动注解，否则注入无效
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
public class AdminTokenInterceptor implements HandlerInterceptor {

    @Autowired
    CheckAdminToken checkAdminToken;

    //程序处理之前需要处理的业务
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        String token = checkAdminToken.getTokenFormRequest(request);

        if(token == null || token.isEmpty()){
            response.getWriter().write(JSONObject.toJSONString(CommonResult.unauthorized()));
            return false;
        }

        Boolean result = checkAdminToken.check(token);
        if(!result){
            response.getWriter().write(JSONObject.toJSONString(CommonResult.unauthorized()));
            return false;
        }
        return true;
    }



    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex) {

    }

}
