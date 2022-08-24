package com.zbkj.admin.config;

import com.zbkj.admin.filter.JwtAuthenticationTokenFilter;
import com.zbkj.admin.manager.AuthenticationEntryPointImpl;
import com.zbkj.admin.manager.CustomAccessDeniedHandler;
import com.zbkj.admin.manager.CustomAuthenticationProvider;
import com.zbkj.common.constants.Constants;
import com.zbkj.service.service.impl.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.CorsFilter;

/**
 * Security配置
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
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 跨域过滤器
     */
    @Autowired
    private CorsFilter corsFilter;

    /**
     * token认证过滤器
     */
    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    /**
     * 认证失败处理类
     */
    @Bean
    public AuthenticationEntryPointImpl unauthorizedHandler() {
        return new AuthenticationEntryPointImpl();
    }

    /**
     * 鉴权失败处理类
     */
    @Bean
    public CustomAccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    /**
     * 这里将Spring Security自带的authenticationManager声明成Bean，声明它的作用是用它帮我们进行认证操作，
     * 调用这个Bean的authenticate方法会由Spring Security自动帮我们做认证。
     */
//    @Bean
//    public AuthenticationManager authenticationManager() throws Exception {
//        return new CusAuthenticationManager(customAuthenticationProvider);
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new CustomAuthenticationProvider(new UserDetailServiceImpl()));
    }

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // CRSF禁用，因为不使用session
        http.cors().and().csrf().disable()
            // 认证失败处理类
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler())
                .accessDeniedHandler(accessDeniedHandler()).and()
            // 基于token，所以不需要session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            // 过滤请求
            .authorizeRequests()
            // 跨域预检请求
//            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 对于登录login 验证码captchaImage 和其他放行的目录 允许匿名访问"/citylife/front/**"
                .antMatchers("/api/admin/login", "/api/admin/validate/code/get").permitAll()
                .antMatchers("/api/admin/getLoginPic").permitAll()
                // 放行资源路径
                .antMatchers("/"+ Constants.UPLOAD_TYPE_IMAGE +"/**").anonymous()
                // 放行图片、文件上传
                .antMatchers("/api/admin/upload/image").permitAll()
                .antMatchers("/api/admin/upload/file").permitAll()
                // 代码生成器
                .antMatchers("/api/codegen/code").permitAll()
//            .antMatchers("/wx/user/*/login","/citylife/nocheck/**").anonymous()
            .antMatchers(
                    HttpMethod.GET,
                    "/*.html",
                    "/**/*.html",
                    "/**/*.css",
                    "/**/*.js"
            ).permitAll()
            .antMatchers("/profile/**").anonymous()
            .antMatchers("/common/download**").anonymous()
            .antMatchers("/common/download/resource**").anonymous()
            .antMatchers("/doc.html").permitAll()
            .antMatchers("/swagger-resources/**").permitAll()
            .antMatchers("/webjars/**").permitAll()
            .antMatchers("/v2/**").permitAll()
            .antMatchers("/swagger-ui.html/**").permitAll()
            .antMatchers("/*/api-docs").anonymous()
            .antMatchers("/druid/**").anonymous()
            .antMatchers("/captcha/get", "/captcha/check").anonymous()
            .antMatchers("/api/admin/payment/callback/**").anonymous()
            .antMatchers("/api/public/**").anonymous()
            // 除上面外的所有请求全部需要鉴权认证
            .anyRequest().authenticated()
            .and()
            .headers().frameOptions().disable();// 防止iframe 造成跨域
//        http.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler);
        // 添加JWT filter
        // 开启登录认证流程过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        // 添加CORS filter
        http.addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class);
        http.addFilterBefore(corsFilter, LogoutFilter.class);
    }

}
