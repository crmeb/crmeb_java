package com.zbkj.admin.manager;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 自定义认证管理器
 * @Author 指缝de阳光
 * @Date 2021/11/17 15:23
 * @Version 1.0
 */
@Component
public class CusAuthenticationManager implements AuthenticationManager {

//    private final CustomAuthenticationProvider customAuthenticationProvider = new CustomAuthenticationProvider();
    private final CustomAuthenticationProvider customAuthenticationProvider;

    public CusAuthenticationManager(CustomAuthenticationProvider customAuthenticationProvider) {
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authentication result = customAuthenticationProvider.authenticate(authentication);
        if (Objects.nonNull(result)) {
            return result;
        }
        throw new ProviderNotFoundException("Authentication failed!");
    }
}
