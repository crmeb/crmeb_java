package com.zbkj.admin.config;

import com.zbkj.admin.filter.ResponseFilter;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.interceptor.SwaggerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.MappedInterceptor;

/**
 * token验证拦截器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 这里使用一个Bean为的是可以在拦截器中自由注入，也可以在拦截器中使用SpringUtil.getBean 获取
    // 但是觉得这样更优雅

    @Autowired
    CrmebConfig crmebConfig;

    @Bean
    public ResponseFilter responseFilter(){ return new ResponseFilter(); }

    @Value("${swagger.basic.username}")
    private String username;
    @Value("${swagger.basic.password}")
    private String password;
    @Value("${swagger.basic.check}")
    private Boolean check;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加token拦截器
        //addPathPatterns添加需要拦截的命名空间；
        //excludePathPatterns添加排除拦截命名空间


//        //后台token拦截
//        registry.addInterceptor(adminTokenInterceptor()).
//                addPathPatterns("/api/admin/**").
//                excludePathPatterns("/api/admin/validate/**").
//                excludePathPatterns("/api/admin/login").
//                excludePathPatterns("/api/admin/logout").
//                excludePathPatterns("/api/admin/getLoginPic").
//                excludePathPatterns("/api/admin/wechat/config").
//                excludePathPatterns("/api/admin/authorize/login").
//                excludePathPatterns("/api/admin/payment/callback/**").
////                excludePathPatterns("/api/admin/system/role/menu").
//                excludePathPatterns("/api/admin/system/role/info").
//                excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");
//
//        //后台权限规则
//        registry.addInterceptor(adminAuthInterceptor()).
//                addPathPatterns("/api/admin/**").
//                excludePathPatterns("/api/admin/validate/**").
//                excludePathPatterns("/api/admin/login").
//                excludePathPatterns("/api/admin/logout").
//                excludePathPatterns("/api/admin/getLoginPic").
//                excludePathPatterns("/api/admin/payment/callback/**").
//                excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        /** 本地文件上传路径 */
        registry.addResourceHandler(Constants.UPLOAD_TYPE_IMAGE + "/**")
                .addResourceLocations("file:" + crmebConfig.getImagePath() + "/" + Constants.UPLOAD_TYPE_IMAGE + "/");
    }

    @Bean
    public FilterRegistrationBean filterRegister() {
        //注册过滤器
        FilterRegistrationBean registration = new FilterRegistrationBean(responseFilter());
        // 仅仅api前缀的请求才会拦截
        registration.addUrlPatterns("/api/admin/*");
        registration.addUrlPatterns("/api/front/*");
        return registration;
    }

    /* 必须在此处配置拦截器,要不然拦不到swagger的静态资源 */
    @Bean
    @ConditionalOnProperty(name = "swagger.basic.enable", havingValue = "true")
    public MappedInterceptor getMappedInterceptor() {
        return new MappedInterceptor(new String[]{"/doc.html", "/webjars/**"}, new SwaggerInterceptor(username, password, check));
    }
}
