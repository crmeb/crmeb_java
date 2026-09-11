package com.zbkj.common.utils;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT token工具.
 */
public class JwtTokenUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);
    private static final String SECRET_ENV_NAME = "CRMEB_JWT_SECRET";
    private static volatile String configuredSecret;
    private static volatile String generatedSecret;

    private JwtTokenUtil() {
    }

    public static String createToken(String subject, String tokenType, Map<String, Object> claims, long expireMinutes) {
        long currentTime = System.currentTimeMillis();
        Map<String, Object> tokenClaims = new HashMap<>();
        if (claims != null) {
            tokenClaims.putAll(claims);
        }
        tokenClaims.put("type", tokenType);
        return Jwts.builder()
                .setClaims(tokenClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + expireMinutes * 60 * 1000))
                .signWith(SignatureAlgorithm.HS256, getSecret())
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    public static String resolveBearerToken(String authorization) {
        if (StrUtil.isBlank(authorization)) {
            return null;
        }
        String token = authorization.trim();
        if (!token.regionMatches(true, 0, Constants.HEADER_AUTHORIZATION_BEARER, 0, Constants.HEADER_AUTHORIZATION_BEARER.length())) {
            return null;
        }
        return token.substring(Constants.HEADER_AUTHORIZATION_BEARER.length()).trim();
    }

    public static boolean isTokenType(Claims claims, String tokenType) {
        return claims != null && tokenType.equals(claims.get("type", String.class));
    }

    public static void configureSecret(String secret) {
        if (StrUtil.isNotBlank(secret)) {
            configuredSecret = secret.trim();
        }
    }

    private static String getSecret() {
        String secret = configuredSecret;
        if (StrUtil.isBlank(secret)) {
            secret = System.getenv(SECRET_ENV_NAME);
        }
        if (StrUtil.isBlank(secret)) {
            secret = getGeneratedSecret();
        }
        return secret;
    }

    /**
     * 未配置密钥时，为当前进程生成随机密钥（重启后已签发的token失效）
     */
    private static synchronized String getGeneratedSecret() {
        if (StrUtil.isBlank(generatedSecret)) {
            byte[] bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
            generatedSecret = HexUtil.encodeHexStr(bytes);
            LOGGER.warn("未配置 crmeb.jwt-secret 或环境变量 {}，已生成临时JWT密钥，服务重启后需重新登录", SECRET_ENV_NAME);
        }
        return generatedSecret;
    }
}
