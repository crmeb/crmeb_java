package com.zbkj.common.result;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zbkj.common.annotation.CustomResponseAnnotation;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @ClassName ResultAdvice
 * @Description 返回值/响应体统一处理
 * @Author HZW
 * @Date 2023/2/21 16:44
 * @Version 1.0
 */
@RestControllerAdvice(basePackages = {"com.zbkj"})
public class ResultAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private ObjectMapper objectMapper;

    /* 使用自定义响应的标识 */
    private static final String CUSTOM_RESPONSE_RESULT_ANNOTATION = "CUSTOM-RESPONSE-RESULT-ANNOTATION";

    /**
     * 是否支持advice功能
     * true 支持，false 不支持
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(sra).getRequest();
        CustomResponseAnnotation customResponseAnnotation = (CustomResponseAnnotation) request.getAttribute(CUSTOM_RESPONSE_RESULT_ANNOTATION);
        // 判断返回体是否需要处理
        return customResponseAnnotation == null;
    }

    /**
     * 对返回数据进行处理
     */
    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof String) {// 如果Controller直接返回String的话，SpringBoot是直接返回，故我们需要手动转换成json。
            return objectMapper.writeValueAsString(CommonResult.success(body));
        }
        if (body instanceof CommonResult) {// 如果返回的结果是CommonResult对象，直接返回即可。
            return body;
        }
        return CommonResult.success(body);
    }
}
