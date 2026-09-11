package com.zbkj.common.config;

import com.zbkj.common.utils.JwtTokenUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

/**
 * 初始化 JWT 签名密钥。
 */
@Configuration
public class JwtTokenConfig {

    private final Environment environment;

    public JwtTokenConfig(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void initialize() {
        JwtTokenUtil.configureSecret(environment.getProperty("crmeb.jwt-secret"));
    }
}
