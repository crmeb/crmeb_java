package com.zbkj.admin.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.service.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 项目启动任务
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Component
public class StartupRunner implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String INSTALL_STATISTICS_URL = "{}://shop.crmeb.net/index.php/admin/server.upgrade_api/updatewebinfo";

    /** 安装统计请求超时时间（毫秒） */
    private static final int INSTALL_STATISTICS_TIMEOUT = 5000;

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CrmebConfig crmebConfig;

    @Override
    public void run(String... args) {
        // 异步执行，不阻塞启动
        CompletableFuture.runAsync(this::installStatistics);
    }

    /**
     * 安装统计，上报成功后不再重复上报
     */
    public void installStatistics() {
        try {
            if ("1".equals(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_INSTALL_STATISTICS))) {
                return;
            }
            String version = crmebConfig.getVersion();
            if (StrUtil.isBlank(version)) {
                version = "CRMEB-JAVA-KY-EDIT";
            }
            String apiUrl = systemConfigService.getValueByKey(Constants.CONFIG_KEY_API_URL);
            if (StrUtil.isBlank(apiUrl) || !(StrUtil.startWithIgnoreCase(apiUrl, "http"))) {
                return;
            }
            // 与站点协议保持一致，站点未启用 https 时使用 http 访问
            String scheme = StrUtil.startWithIgnoreCase(apiUrl, "https") ? "https" : "http";
            Map<String, String> map = new HashMap<>();
            map.put("host", apiUrl);
            map.put("version", version);
            map.put("https", "https");
            String result = HttpUtil.post(StrUtil.format(INSTALL_STATISTICS_URL, scheme), JSONObject.toJSONString(map), INSTALL_STATISTICS_TIMEOUT);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject != null && Integer.valueOf(200).equals(jsonObject.getInteger("status"))) {
                systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_INSTALL_STATISTICS, "1");
            }
        } catch (Exception e) {
            // 统计失败不影响系统使用
            logger.warn("安装统计上报失败：{}", e.getMessage());
        }
    }
}
