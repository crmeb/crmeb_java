package com.zbkj.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.exception.CrmebException;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Locale;

/**
 * 出站 URL 拉取防护：协议/私网/可选 Host 白名单。
 */
public final class UrlFetchGuard {

    public static final int MAX_BYTES = 2 * 1024 * 1024;

    private UrlFetchGuard() {
    }

    /**
     * 校验 URL 是否允许服务端主动拉取。allowedHosts 为空时仅做协议与私网拦截。
     */
    public static void validate(String url, Collection<String> allowedHosts) {
        if (StrUtil.isBlank(url)) {
            throw new CrmebException("url参数错误");
        }
        final URI uri;
        try {
            uri = URI.create(url.trim());
        } catch (Exception e) {
            throw new CrmebException("url参数错误");
        }
        String scheme = uri.getScheme();
        if (scheme == null || (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
            throw new CrmebException("仅支持 http/https 地址");
        }
        String host = uri.getHost();
        if (StrUtil.isBlank(host)) {
            throw new CrmebException("url参数错误");
        }
        String hostLower = host.toLowerCase(Locale.ROOT);
        if (CollUtil.isNotEmpty(allowedHosts)) {
            boolean allowed = false;
            for (String item : allowedHosts) {
                if (StrUtil.isBlank(item)) {
                    continue;
                }
                String allowHost = normalizeHost(item);
                if (hostLower.equals(allowHost) || hostLower.endsWith("." + allowHost)) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                throw new CrmebException("url不在允许的域名范围内");
            }
        }
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress address : addresses) {
                if (isBlockedAddress(address)) {
                    throw new CrmebException("禁止访问内网或保留地址");
                }
            }
        } catch (UnknownHostException e) {
            throw new CrmebException("url参数错误");
        }
    }

    public static void validateResponseSize(byte[] body) {
        if (body != null && body.length > MAX_BYTES) {
            throw new CrmebException("远程资源过大");
        }
    }

    /** 规范化配置中的 URL 或纯 host 为小写 host。 */
    public static String normalizeHost(String hostOrUrl) {
        if (StrUtil.isBlank(hostOrUrl)) {
            return "";
        }
        String value = hostOrUrl.trim();
        try {
            if (value.contains("://")) {
                URI uri = URI.create(value);
                if (StrUtil.isNotBlank(uri.getHost())) {
                    return uri.getHost().toLowerCase(Locale.ROOT);
                }
            }
        } catch (Exception ignored) {
            // fall through
        }
        // 去掉路径
        int slash = value.indexOf('/');
        if (slash >= 0) {
            value = value.substring(0, slash);
        }
        int colon = value.indexOf(':');
        if (colon >= 0 && value.indexOf(']') < 0) {
            value = value.substring(0, colon);
        }
        return value.toLowerCase(Locale.ROOT);
    }

    static boolean isBlockedAddress(InetAddress address) {
        return address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress();
    }

    /**
     * 推广列表 ORDER BY 列名白名单（与前端 childCount/orderCount/numberCount 对齐）；非法则回退 u.create_time。
     */
    public static String sanitizeSpreadSortKey(String sortKey) {
        if (StrUtil.isBlank(sortKey)) {
            return "u.create_time";
        }
        String key = sortKey.trim();
        if ("childCount".equals(key) || "spread_count".equalsIgnoreCase(key)) {
            return "u.spread_count";
        }
        if ("orderCount".equals(key)) {
            return "orderCount";
        }
        if ("numberCount".equals(key)) {
            return "numberCount";
        }
        if ("create_time".equalsIgnoreCase(key) || "spread_time".equalsIgnoreCase(key)) {
            return "u." + key.toLowerCase(Locale.ROOT);
        }
        return "u.create_time";
    }

    public static String sanitizeSortDirection(String isAsc) {
        if (StrUtil.isNotBlank(isAsc) && "asc".equalsIgnoreCase(isAsc.trim())) {
            return "asc";
        }
        return "desc";
    }
}
