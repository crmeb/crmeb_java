package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.SystemConfig;
import com.zbkj.common.request.SaveConfigRequest;
import com.zbkj.common.request.SystemFormCheckRequest;
import com.zbkj.common.request.SystemFormItemCheckRequest;
import com.zbkj.common.response.AdminSiteLogoResponse;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.vo.ExpressSheetVo;
import com.zbkj.service.dao.SystemConfigDao;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.SystemConfigService;
import com.zbkj.service.service.SystemFormTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * SystemConfigServiceImpl 接口实现
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
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigDao, SystemConfig> implements SystemConfigService {

    @Resource
    private SystemConfigDao dao;

    @Autowired
    private SystemFormTempService systemFormTempService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CrmebConfig crmebConfig;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 根据menu name 获取 value
     *
     * @param name menu name
     * @return String
     */
    @Override
    public String getValueByKey(String name) {
        return get(name);
    }

    /**
     * 根据 name 获取 value 找不到抛异常
     *
     * @param name menu name
     * @return String
     */
    @Override
    public String getValueByKeyException(String name) {
        String value = get(name);
        if (StrUtil.isBlank(value)) {
            throw new CrmebException("没有找到或配置：" + name + "数据");
        }
        return value;
    }

    /**
     * 整体保存表单数据
     *
     * @param systemFormCheckRequest SystemFormCheckRequest 数据保存
     * @return boolean
     */
    @Override
    public Boolean saveForm(SystemFormCheckRequest systemFormCheckRequest) {
        //检测form表单，并且返回需要添加的数据
        systemFormTempService.checkForm(systemFormCheckRequest);

        List<SystemConfig> systemConfigList = new ArrayList<>();
        //批量添加
        for (SystemFormItemCheckRequest systemFormItemCheckRequest : systemFormCheckRequest.getFields()) {
            SystemConfig systemConfig = new SystemConfig();
            systemConfig.setName(systemFormItemCheckRequest.getName());
            String value = systemAttachmentService.clearPrefix(systemFormItemCheckRequest.getValue());
            if (StrUtil.isBlank(value)) {
                //去掉图片域名之后没有数据则说明当前数据就是图片域名
                value = systemFormItemCheckRequest.getValue();
            }
            systemConfig.setValue(value);
            systemConfig.setFormId(systemFormCheckRequest.getId());
            systemConfig.setTitle(systemFormItemCheckRequest.getTitle());
            systemConfigList.add(systemConfig);
        }

        LambdaQueryWrapper<SystemConfig> oldLqw = Wrappers.lambdaQuery();
        oldLqw.eq(SystemConfig::getFormId, systemFormCheckRequest.getId());
        //删除已经隐藏的数据
        List<SystemConfig> systemConfigOldList = dao.selectList(oldLqw);

        Boolean execute = transactionTemplate.execute(e -> {
            //删除之前的数据
            if (CollUtil.isNotEmpty(systemConfigOldList)) {
                dao.delete(oldLqw);
            }
            boolean batch = saveBatch(systemConfigList, 100);
            if (!batch) {
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        });
        if (execute && crmebConfig.isAsyncConfig() && CollUtil.isNotEmpty(systemConfigOldList)) {
            asyncDelete(systemConfigOldList);
        }
        return execute;
    }

    private void asyncDelete(List<SystemConfig> systemConfigList) {
        systemConfigList.forEach(config -> {
            if (redisUtil.hHasKey(Constants.CONFIG_LIST, config.getName())) {
                redisUtil.hmDelete(Constants.CONFIG_LIST, config.getName());
            }
        });
    }

    /**
     * 保存或更新配置数据
     *
     * @param name  菜单名称
     * @param value 菜单值
     * @return boolean
     */
    @Override
    public Boolean updateOrSaveValueByName(String name, String value) {
        if (StrUtil.isNotBlank(value)) {
            value = systemAttachmentService.clearPrefix(value);
        }
        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(SystemConfig::getName, name);
        List<SystemConfig> systemConfigs = dao.selectList(lambdaQueryWrapper);
        if (systemConfigs.size() > 1) {
            throw new CrmebException("配置名称存在多个请检查配置 eb_system_config 重复数据：" + name + "条数：" + systemConfigs.size());
        }
        boolean result;
        SystemConfig systemConfig;
        if (CollUtil.isEmpty(systemConfigs)) {
            systemConfig = new SystemConfig().setName(name).setValue(value);
            result = save(systemConfig);
        } else {
            systemConfig = systemConfigs.get(0);
            systemConfig.setValue(value);
            systemConfig.setUpdateTime(DateUtil.date());
            result = updateById(systemConfig);
        }
        if (result && crmebConfig.isAsyncConfig()) {
            async(systemConfig);
        }
        return result;
    }


    /**
     * 根据formId查询数据
     *
     * @param formId Integer id
     * @return HashMap<String, String>
     */
    @Override
    public HashMap<String, String> info(Integer formId) {
        LambdaQueryWrapper<SystemConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemConfig::getFormId, formId);
        List<SystemConfig> systemConfigList = dao.selectList(lqw);
        if (CollUtil.isEmpty(systemConfigList)) {
            return CollUtil.newHashMap();
        }
        HashMap<String, String> map = new HashMap<>();
        for (SystemConfig systemConfig : systemConfigList) {
            map.put(systemConfig.getName(), systemConfig.getValue());
        }
        map.put("id", formId.toString());
        return map;
    }

    /**
     * 获取面单默认配置信息
     *
     * @return ExpressSheetVo
     */
    @Override
    public ExpressSheetVo getDeliveryInfo() {
        String exportId = get(SysConfigConstants.ELECTRONIC_FACE_SHEET_EXPORT_ID);
        String exportTempId = get(SysConfigConstants.ELECTRONIC_FACE_SHEET_EXPORT_TEMP_ID);
        String exportCom = get(SysConfigConstants.ELECTRONIC_FACE_SHEET_EXPORT_COM);
        String exportToName = get(SysConfigConstants.ELECTRONIC_FACE_SHEET_TO_NAME);
        String exportToTel = get(SysConfigConstants.ELECTRONIC_FACE_SHEET_TO_TEL);
        String exportToAddress = get(SysConfigConstants.ELECTRONIC_FACE_SHEET_ADDRESS);
        String exportSiid = get(SysConfigConstants.ELECTRONIC_FACE_SHEET_PRINTER_NUM);
        String exportOpen = get(SysConfigConstants.ELECTRONIC_FACE_SHEET_OPEN);
        return new ExpressSheetVo(Integer.valueOf(exportId), exportCom, exportTempId, exportToName, exportToTel, exportToAddress, exportSiid, Integer.valueOf(exportOpen));
    }

    /**
     * 获取文件存储类型
     */
    @Override
    public SystemConfig getFileUploadType() {
        return getConfigByNameException(SysConfigConstants.CONFIG_UPLOAD_TYPE);
    }

    /**
     * 获取管理端logo
     *
     * @return AdminSiteLogoResponse
     */
    @Override
    public AdminSiteLogoResponse getSiteLogo() {
        String siteLogoSquare = get(SysConfigConstants.CONFIG_KEY_ADMIN_LOGO_SQUARE);
        String siteLogoLeftTop = get(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LOGO_LEFT_TOP);
        AdminSiteLogoResponse response = new AdminSiteLogoResponse();
        response.setSiteLogoSquare(siteLogoSquare);
        response.setSiteLogoLeftTop(siteLogoLeftTop);
        return response;
    }

    /**
     * 获取腾讯地图key
     */
    @Override
    public SystemConfig getTxMapKey() {
        return getConfigByNameException(SysConfigConstants.CONFIG_SITE_TENG_XUN_MAP_KEY);
    }

    /**
     * 获取移动端首页列表样式
     */
    @Override
    public SystemConfig getHomePageSaleListStyle() {
        return getConfigByNameException(Constants.CONFIG_IS_PRODUCT_LIST_STYLE);
    }

    /**
     * 保存移动端首页列表样式
     */
    @Override
    public Boolean saveHomePageSaleListStyle(SaveConfigRequest request) {
        return updateOrSaveValueByName(Constants.CONFIG_IS_PRODUCT_LIST_STYLE, request.getValue());
    }

    /**
     * 清除config缓存
     */
    @Override
    public Boolean clearCache() {
        redisUtil.delete(Constants.CONFIG_LIST);
        return  Boolean.TRUE;
    }

    /**
     * 获取授权地址
     */
    @Override
    public SystemConfig getAuthHost() {
        return getConfigByNameException(SysConfigConstants.CONFIG_COPYRIGHT_AUTH_HOST);
    }

    /**
     * 获取主题色
     */
    @Override
    public SystemConfig getChangeColor() {
        return getConfigByNameException(SysConfigConstants.CONFIG_CHANGE_COLOR);
    }

    /**
     * 保存主题色
     */
    @Override
    public Boolean saveChangeColor(SaveConfigRequest request) {
        return updateOrSaveValueByName(SysConfigConstants.CONFIG_CHANGE_COLOR, request.getValue());
    }

    private SystemConfig getConfigByName(String name) {
        String value = get(name);
        if (StrUtil.isBlank(value)) {
            value = "";
        }
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setName(name);
        systemConfig.setValue(value);
        return systemConfig;
    }

    private SystemConfig getConfigByNameException(String name) {
        String value = getValueByKeyException(name);
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setName(name);
        systemConfig.setValue(value);
        return systemConfig;
    }

    private void asyncBlank(String key) {
        redisUtil.hset(Constants.CONFIG_LIST, key, "");
    }

    private void async(SystemConfig systemConfig) {
        redisUtil.hset(Constants.CONFIG_LIST, systemConfig.getName(), systemConfig.getValue());
    }


    /**
     * 获取config数据
     *
     * @param name key
     * @return String
     */
    private String get(String name) {
        if (!crmebConfig.isAsyncConfig()) {
            SystemConfig systemConfig = getByName(name);
            if (ObjectUtil.isNull(systemConfig) || StrUtil.isBlank(systemConfig.getValue())) {
                return "";
            }
            return systemConfig.getValue();
        }
        Long size = redisUtil.getHashSize(Constants.CONFIG_LIST);
        if (size <= 0) {
            SystemConfig systemConfig = getByName(name);
            if (ObjectUtil.isNull(systemConfig) || StrUtil.isBlank(systemConfig.getValue())) {
                asyncBlank(name);
                return "";
            }
            async(systemConfig);
            return systemConfig.getValue();
        }
        Object data = redisUtil.hget(Constants.CONFIG_LIST, name);
        if (ObjectUtil.isNotNull(data)) {
            return data.toString();
        }
        SystemConfig systemConfig = getByName(name);
        if (ObjectUtil.isNull(systemConfig) || StrUtil.isBlank(systemConfig.getValue())) {
            asyncBlank(name);
            return "";
        }
        async(systemConfig);
        return systemConfig.getValue();
    }

    private SystemConfig getByName(String name) {
        LambdaQueryWrapper<SystemConfig> lqw = Wrappers.lambdaQuery();
        lqw.select(SystemConfig::getId, SystemConfig::getName, SystemConfig::getValue);
        lqw.eq(SystemConfig::getStatus, false);
        lqw.eq(SystemConfig::getName, name);
        lqw.orderByDesc(SystemConfig::getId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 初始化时加载config_list缓存
     */
    @PostConstruct
    public void loadingConfigCache() {
        if (!crmebConfig.isAsyncConfig()) {
            return;
        }
        if (redisUtil.exists(Constants.CONFIG_LIST)) {
            Long hashSize = redisUtil.getHashSize(Constants.CONFIG_LIST);
            if (hashSize > 0) {
                return;
            }
        }
        LambdaQueryWrapper<SystemConfig> lqw = Wrappers.lambdaQuery();
        lqw.select(SystemConfig::getName, SystemConfig::getValue);
        lqw.eq(SystemConfig::getStatus, false);
        List<SystemConfig> systemConfigList = dao.selectList(lqw);
        systemConfigList.forEach(config -> redisUtil.hset(Constants.CONFIG_LIST, config.getName(), config.getValue()));

    }


    /**
     * 获取移动端域名
     * @return 移动端域名
     */
    @Override
    public String getFrontDomain() {
        return getValueByKey(SysConfigConstants.CONFIG_KEY_SITE_URL);
    }

    /**
     * 获取素材域名
     *
     * @return 素材域名
     */
    @Override
    public String getMediaDomain() {
        return systemAttachmentService.getCdnUrl();
    }
}

