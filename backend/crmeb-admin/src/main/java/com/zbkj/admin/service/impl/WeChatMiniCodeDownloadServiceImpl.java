package com.zbkj.admin.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.zbkj.admin.service.WeChatMiniCodeDownloadService;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.UploadConstants;
import com.zbkj.common.constants.WeChatConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.service.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 小程序源码下载 服务实现类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class WeChatMiniCodeDownloadServiceImpl implements WeChatMiniCodeDownloadService {

    @Autowired
    private CrmebConfig crmebConfig;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 小程序源码下载
     *
     * @return 源码压缩包路径
     */
    @Override
    public String WeChatMiniCodeDownload() {
        // 获取 需要运行微信小程序的必备配置
        String crmebWeixinAppid = systemConfigService.getValueByKey(WeChatConstants.WECHAT_MINI_APPID);
        if (StrUtil.isBlank(crmebWeixinAppid)) {
            throw new CrmebException("应用设置中 微信小程序数据配置 或者 支付回调地址以及网站地址 配置不全");
        }
        String crmebName = systemConfigService.getValueByKey(WeChatConstants.WECHAT_MINI_NAME);
        if (StrUtil.isBlank(crmebName)) {
            throw new CrmebException("应用设置中 微信小程序数据配置 或者 支付回调地址以及网站地址 配置不全");
        }
        String apiPath = systemConfigService.getValueByKey(Constants.CONFIG_KEY_FRONT_API_URL);
        if (StrUtil.isBlank(apiPath)) {
            throw new CrmebException("应用设置中 微信小程序数据配置 或者 支付回调地址以及网站地址 配置不全");
        }
        String adminApiPath = systemConfigService.getValueByKey(Constants.CONFIG_KEY_API_URL);
        if (StrUtil.isBlank(adminApiPath)) {
            throw new CrmebException("应用设置中 微信小程序数据配置 或者 支付回调地址以及网站地址 配置不全");
        }
        String appPath = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_SITE_URL);
        if (StrUtil.isBlank(appPath)) {
            throw new CrmebException("应用设置中 微信小程序数据配置 或者 支付回调地址以及网站地址 配置不全");
        }
        // 压缩后给出下载地址
        String miniCodeName = Constants.WECHAT_SOURCE_CODE_FILE_NAME;
        // 解压新的源码用来修改配置后下载
        String baseCodePath = crmebConfig.getImagePath() + UploadConstants.UPLOAD_FILE_KEYWORD;
        String sourceCodePath = baseCodePath + "/mp-weixin.zip";
        String WxMpCodePath = baseCodePath + "/mp-weixin/";
        if (!FileUtil.exist(sourceCodePath)) {
            throw new CrmebException("源码包不存在 联系开发人员");
        }
        // 删除原有文件重新解压原始小程序代码
        FileUtil.del(WxMpCodePath); // 删除上次解压的数据
        FileUtil.del(baseCodePath + miniCodeName); // 删除上次压缩的目录
        ZipUtil.unzip(sourceCodePath, baseCodePath);


        // 修改源码中的配置文件
        String vendorJs = WxMpCodePath + "common/vendor.js";
        String projectConfigJson = WxMpCodePath + "project.config.json";

        // 待替代的关键字
        String vendorJsReplaceApi = "https://api";
//        String vendorJsReplaceAdminApi = "https://adminapi";
        String vendorJsReplaceApp = "https://app";
        String projectConfigJsonCrmebName = "crmebName";
        String projectConfigJsonAppId = "crmebWeixinAppid";
        // 读取文件并替换
        FileReader vendorJsR = new FileReader(vendorJs);
        FileReader projectConfigJsonR = new FileReader(projectConfigJson);
        String vendorResult = vendorJsR.readString().replace(vendorJsReplaceApi, apiPath)
                .replace(vendorJsReplaceApp, appPath);
//                .replace(vendorJsReplaceAdminApi, adminApiPath);
        String projectConfigJsonResult = projectConfigJsonR.readString().replace(projectConfigJsonCrmebName, crmebName)
                .replace(projectConfigJsonAppId, crmebWeixinAppid);
        FileWriter vendorJsW = new FileWriter(vendorJs);
        FileWriter projectConfigJsonW = new FileWriter(projectConfigJson);
        vendorJsW.write(vendorResult);
        projectConfigJsonW.write(projectConfigJsonResult);

        // 压缩后给出下载地址
        ZipUtil.zip(WxMpCodePath, baseCodePath + miniCodeName);
        return UploadConstants.UPLOAD_FILE_KEYWORD + miniCodeName;
    }
}
