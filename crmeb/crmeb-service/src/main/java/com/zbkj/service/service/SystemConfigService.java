package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.system.SystemConfig;
import com.zbkj.common.request.SaveConfigRequest;
import com.zbkj.common.request.SystemFormCheckRequest;
import com.zbkj.common.response.AdminSiteLogoResponse;
import com.zbkj.common.vo.ExpressSheetVo;

import java.util.HashMap;

/**
 * SystemConfigService 接口
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
public interface SystemConfigService extends IService<SystemConfig> {

    /**
     * 根据menu name 获取 value
     * @param key menu name
     * @return String
     */
    String getValueByKey(String key);

    /**
     * 保存或更新配置数据
     * @param name 菜单名称
     * @param value 菜单值
     * @return Boolean
     */
    Boolean updateOrSaveValueByName(String name, String value);

    /**
     * 根据 name 获取 value 找不到抛异常
     * @param key menu name
     * @return String
     */
    String getValueByKeyException(String key);

    /**
     * 整体保存表单数据
     * @param systemFormCheckRequest SystemFormCheckRequest 数据保存
     * @return Boolean
     */
    Boolean saveForm(SystemFormCheckRequest systemFormCheckRequest);

    /**
     * 根据formId查询数据
     * @param formId Integer id
     * @return HashMap<String, String>
     */
    HashMap<String, String> info(Integer formId);

    /**
     * 获取面单默认配置信息
     * @return ExpressSheetVo
     */
    ExpressSheetVo getDeliveryInfo();

    /**
     * 获取文件存储类型
     */
    SystemConfig getFileUploadType();

    /**
     * 获取管理端logo
     *
     * @return AdminSiteLogoResponse
     */
    AdminSiteLogoResponse getSiteLogo();

    /**
     * 获取腾讯地图key
     */
    SystemConfig getTxMapKey();

    /**
     * 获取移动端首页列表样式
     */
    SystemConfig getHomePageSaleListStyle();

    /**
     * 保存移动端首页列表样式
     */
    Boolean saveHomePageSaleListStyle(SaveConfigRequest request);

    /**
     * 清除config缓存
     */
    Boolean clearCache();

    /**
     * 获取授权地址
     */
    SystemConfig getAuthHost();

    /**
     * 获取主题色
     */
    SystemConfig getChangeColor();

    /**
     * 保存主题色
     */
    Boolean saveChangeColor(SaveConfigRequest request);


    /**
     * 获取移动端域名
     * @return 移动端域名
     */
    String getFrontDomain();

    /**
     * 获取素材域名
     *
     * @return 素材域名
     */
    String getMediaDomain();
}
