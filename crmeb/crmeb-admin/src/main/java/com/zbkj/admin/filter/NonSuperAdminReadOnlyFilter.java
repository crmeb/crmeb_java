package com.zbkj.admin.filter;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.model.system.SystemAdmin;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.vo.LoginUserVo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 生产演示环境只允许非超级管理员执行只读请求。
 */
public class NonSuperAdminReadOnlyFilter extends OncePerRequestFilter {

    private static final Set<String> WRITE_METHODS = new HashSet<>(Arrays.asList("POST", "PUT", "DELETE"));
    private static final String SUPER_ADMIN_ROLE_ID = "1";

    private final boolean enabled;

    public NonSuperAdminReadOnlyFilter(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication == null ? null : authentication.getPrincipal();

        if (shouldBlock(request.getMethod(), principal)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(JSONObject.toJSONString(
                    CommonResult.failed(CommonResultCode.FORBIDDEN, "暂无权限")));
            return;
        }

        filterChain.doFilter(request, response);
    }

    boolean shouldBlock(String requestMethod, Object principal) {
        if (!enabled || !WRITE_METHODS.contains(requestMethod) || !(principal instanceof LoginUserVo)) {
            return false;
        }

        SystemAdmin admin = ((LoginUserVo) principal).getUser();
        return admin != null && !hasSuperAdminRole(admin.getRoles());
    }

    private boolean hasSuperAdminRole(String roles) {
        if (roles == null || roles.trim().isEmpty()) {
            return false;
        }
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .anyMatch(SUPER_ADMIN_ROLE_ID::equals);
    }
}
