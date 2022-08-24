package com.zbkj.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Crmeb 基础配置
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
@ConfigurationProperties(prefix = "crmeb")
public class CrmebConfig {
    // 当前代码版本
    private String version;
    // 待部署域名
    private String  domain;
    // #请求微信接口中专服务器
    private String wechatApiUrl;
    // #微信js api系列是否开启调试模式
    private boolean wechatJsApiDebug;
    // #微信js api是否是beta版本
    private boolean wechatJsApiBeta;
    // #是否同步config表数据到redis
    private boolean asyncConfig;
    // #是否同步小程序公共模板库
    private boolean asyncWeChatProgramTempList;
    // 本地图片路径配置
    private String imagePath;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getWechatApiUrl() {
        return wechatApiUrl;
    }

    public void setWechatApiUrl(String wechatApiUrl) {
        this.wechatApiUrl = wechatApiUrl;
    }

    public boolean isWechatJsApiDebug() {
        return wechatJsApiDebug;
    }

    public void setWechatJsApiDebug(boolean wechatJsApiDebug) {
        this.wechatJsApiDebug = wechatJsApiDebug;
    }

    public boolean isWechatJsApiBeta() {
        return wechatJsApiBeta;
    }

    public void setWechatJsApiBeta(boolean wechatJsApiBeta) {
        this.wechatJsApiBeta = wechatJsApiBeta;
    }

    public boolean isAsyncConfig() {
        return asyncConfig;
    }

    public void setAsyncConfig(boolean asyncConfig) {
        this.asyncConfig = asyncConfig;
    }

    public boolean isAsyncWeChatProgramTempList() {
        return asyncWeChatProgramTempList;
    }

    public void setAsyncWeChatProgramTempList(boolean asyncWeChatProgramTempList) {
        this.asyncWeChatProgramTempList = asyncWeChatProgramTempList;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
