package com.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.common.CommonResult;
import com.utils.RequestUtil;
import com.zbkj.crmeb.system.service.SystemRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *  token验证拦截器
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
public class AdminAuthInterceptor implements HandlerInterceptor {
    @Autowired
    private SystemRoleService systemRoleService;

    //程序处理之前需要处理的业务
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        String uri = RequestUtil.getUri(request);
        if(uri == null || uri.isEmpty()){
            response.getWriter().write(JSONObject.toJSONString(CommonResult.forbidden()));
            return false;
        }

        Boolean result = systemRoleService.checkAuth(uri);

        if(!result){
            response.getWriter().write(JSONObject.toJSONString(CommonResult.forbidden()));
            return false;
        }
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

}
