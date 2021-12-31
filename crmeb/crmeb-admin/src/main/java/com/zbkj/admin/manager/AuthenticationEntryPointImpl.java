package com.zbkj.admin.manager;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.response.CommonResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 认证失败处理类 返回未授权
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) {
//        String msg = StrUtil.format("请求访问：{}，认证失败，无法访问系统资源", httpServletRequest.getRequestURI());
        httpServletResponse.setStatus(200);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("utf-8");
        try {
            httpServletResponse.getWriter().print(JSONObject.toJSONString(CommonResult.unauthorized()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
