package com.zbkj.admin.manager;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.response.CommonResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 自定义权限不足处理程序
 *
 * @Author 指缝de阳光
 * @Date 2021/11/19 14:45
 * @Version 1.0
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        httpServletResponse.setStatus(200);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("utf-8");
        try {
            httpServletResponse.getWriter().print(JSONObject.toJSONString(CommonResult.forbidden()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
