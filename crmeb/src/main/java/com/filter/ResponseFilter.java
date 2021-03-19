package com.filter;


import com.alibaba.fastjson.JSON;
import com.utils.RequestUtil;
import com.zbkj.crmeb.front.service.impl.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


/**
 * +----------------------------------------------------------------------
 * | crmeb [ crmeb赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | copyright (c) 2016~2020 https://www.crmeb.com all rights reserved.
 * +----------------------------------------------------------------------
 * | licensed crmeb并不是自由软件，未经许可不能去掉crmeb相关版权
 * +----------------------------------------------------------------------
 * | author: crmeb team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 * 返回值输出过滤器
 */
@Slf4j
@Component
public class ResponseFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {


        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setContentType("application/json;charset=UTF-8");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String url = request.getRequestURI().substring(request.getContextPath().length());
        if (url.startsWith("/") && url.length() > 1) {
            url = url.substring(1);
        }

        log.info("请求地址:>>>>"+url);
        ResponseWrapper wrapperResponse = new ResponseWrapper(response);//转换成代理类
        // 这里只拦截返回，直接让请求过去，如果在请求前有处理，可以在这里处理
        filterChain.doFilter(request, wrapperResponse);
        byte[] content = wrapperResponse.getContent();//获取返回值
        //判断是否有值
        if (content.length > 0) {
            String str = new String(content, StandardCharsets.UTF_8);

            try {
                HttpServletRequest req = (HttpServletRequest) request;
                str = new ResponseRouter().filter(str, RequestUtil.getUri(req));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //把返回值输出到客户端
            ServletOutputStream outputStream = response.getOutputStream();
            if (str.length() > 0) {
                outputStream.write(str.getBytes());
                outputStream.flush();
                outputStream.close();
                //最后添加这一句，输出到客户端
                response.flushBuffer();
            }
        }
    }
}
