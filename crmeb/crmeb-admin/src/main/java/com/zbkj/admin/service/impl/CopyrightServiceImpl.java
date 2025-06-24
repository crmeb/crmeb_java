package com.zbkj.admin.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.admin.copyright.CopyrightInfoResponse;
import com.zbkj.admin.copyright.CopyrightUpdateInfoRequest;
import com.zbkj.admin.service.CopyrightService;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.utils.RestTemplateUtil;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 版权服务实现类
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
@Service
public class CopyrightServiceImpl implements CopyrightService {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CrmebConfig crmebConfig;
    @Autowired
    private RestTemplateUtil restTemplateUtil;


    private static final String CRMEB_COPYRIGHT_URL = "https://authorize.crmeb.net/api/auth_cert_query?domain_name={}&label={}&version={}";
    private static final String CRMEB_COPYRIGHT_URL_DATA = "data";
    private static final String CRMEB_COPYRIGHT_URL_STATUS = "status";
    private static final String CRMEB_COPYRIGHT_URL_COPYRIGHT = "copyright";
    private static final String CRMEB_COPYRIGHT_URL_AUTHCODE = "auth_code";


    /**
     * 获取版权信息
     */
    @Override
    public CopyrightInfoResponse getInfo() {
        CopyrightInfoResponse response = new CopyrightInfoResponse();
        String domainName = systemConfigService.getValueByKey(Constants.CONFIG_KEY_API_URL);
        if (StrUtil.isBlank(domainName)) {
            response.setStatus(-2);
            return response;
        }
        String label = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_LABEL);
        String version = crmebConfig.getVersion();
        if (StrUtil.isBlank(version)) {
            throw new CrmebException("请先在yml中配置版本号");
        }
        response.setDomainUrl(domainName);
        response.setLabel(Integer.parseInt(label));
        response.setVersion(version);

        JSONObject jsonObject = restTemplateUtil.post(StrUtil.format(CRMEB_COPYRIGHT_URL, domainName, label, version));
        if (ObjectUtil.isNull(jsonObject.getInteger("status")) || !jsonObject.getInteger("status").equals(200)) {
            throw new CrmebException("CRMEB版权接口调用失败," + jsonObject);
        }
        System.out.println("==================================== " + jsonObject.toString());
        JSONObject dataJson = jsonObject.getJSONObject(CRMEB_COPYRIGHT_URL_DATA);

        response.setStatus(dataJson.getInteger(CRMEB_COPYRIGHT_URL_STATUS));
        response.setCopyright(dataJson.getString(CRMEB_COPYRIGHT_URL_COPYRIGHT));
        if (!dataJson.getInteger(CRMEB_COPYRIGHT_URL_STATUS).equals(1)) {
            return response;
        }
        response.setAuthCode(dataJson.getString(CRMEB_COPYRIGHT_URL_AUTHCODE));
        response.setCompanyName(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_INFO));
        response.setCompanyImage(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_IMAGE));
        return response;
    }

}
