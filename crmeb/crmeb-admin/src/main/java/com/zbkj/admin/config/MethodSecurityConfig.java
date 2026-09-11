package com.zbkj.admin.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * 非只读模式下启用系统原有的接口权限校验。
 */
@Configuration
@ConditionalOnProperty(
        prefix = "crmeb",
        name = "non-super-admin-read-only",
        havingValue = "false",
        matchIfMissing = true)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig {
}
