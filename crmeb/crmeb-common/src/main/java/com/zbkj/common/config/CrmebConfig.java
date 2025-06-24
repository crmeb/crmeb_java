package com.zbkj.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Crmeb 基础配置
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
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
    // 是否演示站点 所有手机号码都会掩码
    private Boolean demoSite;
    // 活动边框缓存周期
    private Integer activityStyleCachedTime;
    // 活动边框参加 指定商品参加上限
    private Integer selectProductLimit;

    // 不过滤任何数据的url配置
    private List<String> ignored;

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


    public Boolean getDemoSite() {
        return demoSite;
    }

    public void setDemoSite(Boolean demoSite) {
        this.demoSite = demoSite;
    }

    public List<String> getIgnored() {
        return ignored;
    }

    public void setIgnored(List<String> ignored) {
        this.ignored = ignored;
    }

    public Integer getActivityStyleCachedTime() {
        return activityStyleCachedTime;
    }

    public void setActivityStyleCachedTime(Integer activityStyleCachedTime) {
        this.activityStyleCachedTime = activityStyleCachedTime;
    }

    public Integer getSelectProductLimit() {
        return selectProductLimit;
    }

    public void setSelectProductLimit(Integer selectProductLimit) {
        this.selectProductLimit = selectProductLimit;
    }

    @Override
    public String toString() {
        return "CrmebConfig{" +
                "version='" + version + '\'' +
                ", domain='" + domain + '\'' +
                ", wechatApiUrl='" + wechatApiUrl + '\'' +
                ", asyncConfig=" + asyncConfig +
                ", imagePath='" + imagePath + '\'' +
                ", activityStyleCachedTime=" + activityStyleCachedTime +
                ", selectProductLimit=" + selectProductLimit +
                ", ignored=" + ignored +
                '}';
    }
}
