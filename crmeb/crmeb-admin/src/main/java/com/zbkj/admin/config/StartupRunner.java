package com.zbkj.admin.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.service.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CrmebConfig crmebConfig;



    @Override
    public void run(String... args) throws Exception {
        // 项目启动后立即执行的代码
        System.out.println("项目启动完成，开始执行初始化任务...");
        // 异步执行，不阻塞启动
        CompletableFuture.runAsync(this::installStatistics);
        System.out.println("初始化任务执行结束...");
    }

    public void installStatistics() {
        try {
            String version = crmebConfig.getVersion();
            if (StrUtil.isBlank(version) ) {
                version = "CRMEB-JAVA-KY-EDIT";
            }
            String apiUrl = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_API_URL);
            if (StrUtil.isBlank(apiUrl) || !(StrUtil.startWithIgnoreCase(apiUrl, "http"))) {
                return;
            }
            Map<String, String> map = new HashMap<>();
            map.put("host", apiUrl);
            map.put("version", version);
            map.put("https", "https");
            String result = HttpUtil.post("https://shop.crmeb.net/index.php/admin/server.upgrade_api/updatewebinfo", JSONObject.toJSONString(map));

        } catch (Exception e) {
            // 异步调用不应影响主流程
            e.printStackTrace();
        }
    }
}
