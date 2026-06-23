package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.common.model.system.SystemGroupData;
import com.zbkj.common.request.SystemFormItemCheckRequest;
import com.zbkj.common.response.pagelayout.PageLayoutBottomNavigationResponse;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.SplashAdConfigVo;
import com.zbkj.common.vo.SplashAdDataVo;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 页面布局接口实现类
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Service
public class PageLayoutServiceImpl implements PageLayoutService {

    @Autowired
    private SystemGroupDataService systemGroupDataService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private GroupConfigService groupConfigService;

    /**
     * 页面首页
     * @return 首页信息
     */
    @Override
    public HashMap<String, Object> index() {
        HashMap<String, Object> map = CollUtil.newHashMap();
        // 首页banner
        List<SystemGroupData> bannerList = systemGroupDataService.findListByGid(Constants.GROUP_DATA_ID_INDEX_BANNER);
        map.put("indexBanner", convertData(bannerList));
        // 首页金刚区
        List<SystemGroupData> menuList = systemGroupDataService.findListByGid(Constants.GROUP_DATA_ID_INDEX_MENU);
        map.put("indexMenu", convertData(menuList));
        // 首页新闻
        List<SystemGroupData> newsList = systemGroupDataService.findListByGid(Constants.GROUP_DATA_ID_INDEX_NEWS_BANNER);
        map.put("indexNews", convertData(newsList));

        // 我的页服务
        List<SystemGroupData> userMenuList = systemGroupDataService.findListByGid(SysGroupDataConstants.GROUP_DATA_ID_USER_CENTER_MENU);
        map.put("userMenu", convertData(userMenuList));
        // 我的页banner
        List<SystemGroupData> userBannerList = systemGroupDataService.findListByGid(SysGroupDataConstants.GROUP_DATA_ID_USER_CENTER_BANNER);
        map.put("userBanner", convertData(userBannerList));
        return map;
    }

    /**
     * 首页保存
     * @param jsonObject 数据
     * @return Boolean
     */
    @Override
    public Boolean save(JSONObject jsonObject) {
        List<SystemGroupData> dataList = CollUtil.newArrayList();

        if (StrUtil.isNotBlank(jsonObject.getString("indexBanner"))) {
            List<JSONObject> indexBanner = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("indexBanner"));
            dataList.addAll(convertGroupData(indexBanner, Constants.GROUP_DATA_ID_INDEX_BANNER));
        }
        if (StrUtil.isNotBlank(jsonObject.getString("indexMenu"))) {
            List<JSONObject> indexMenu = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("indexMenu"));
            dataList.addAll(convertGroupData(indexMenu, Constants.GROUP_DATA_ID_INDEX_MENU));
        }
        if (StrUtil.isNotBlank(jsonObject.getString("indexNews"))) {
            List<JSONObject> indexNews = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("indexNews"));
            dataList.addAll(convertGroupData(indexNews, Constants.GROUP_DATA_ID_INDEX_NEWS_BANNER));
        }
        if (StrUtil.isNotBlank(jsonObject.getString("userMenu"))) {
            List<JSONObject> userMenu = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("userMenu"));
            dataList.addAll(convertGroupData(userMenu, Constants.GROUP_DATA_ID_USER_CENTER_MENU));
        }
        if (StrUtil.isNotBlank(jsonObject.getString("userBanner"))) {
            List<JSONObject> userBanner = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("userBanner"));
            dataList.addAll(convertGroupData(userBanner, Constants.GROUP_DATA_ID_USER_CENTER_BANNER));
        }
        Boolean execute = transactionTemplate.execute(e -> {
            // 先删除历史数据
            systemGroupDataService.deleteByGid(Constants.GROUP_DATA_ID_INDEX_BANNER);
            systemGroupDataService.deleteByGid(Constants.GROUP_DATA_ID_INDEX_MENU);
            systemGroupDataService.deleteByGid(Constants.GROUP_DATA_ID_INDEX_NEWS_BANNER);
            systemGroupDataService.deleteByGid(Constants.GROUP_DATA_ID_USER_CENTER_MENU);
            systemGroupDataService.deleteByGid(Constants.GROUP_DATA_ID_USER_CENTER_BANNER);
            // 保存新数据
            systemGroupDataService.saveBatch(dataList, 100);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 页面首页banner保存
     * @param jsonObject 数据
     * @return Boolean
     */
    @Override
    public Boolean indexBannerSave(JSONObject jsonObject) {
        List<JSONObject> indexBanner = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("indexBanner"));
        List<SystemGroupData> dataList = convertGroupData(indexBanner, Constants.GROUP_DATA_ID_INDEX_BANNER);
        Boolean execute = transactionTemplate.execute(e -> {
            // 先删除历史数据
            systemGroupDataService.deleteByGid(Constants.GROUP_DATA_ID_INDEX_BANNER);
            // 保存新数据
            systemGroupDataService.saveBatch(dataList, 100);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 页面首页menu保存
     * @param jsonObject 数据
     * @return Boolean
     */
    @Override
    public Boolean indexMenuSave(JSONObject jsonObject) {
        List<JSONObject> indexMenu = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("indexMenu"));
        List<SystemGroupData> dataList = convertGroupData(indexMenu, Constants.GROUP_DATA_ID_INDEX_MENU);
        Boolean execute = transactionTemplate.execute(e -> {
            // 先删除历史数据
            systemGroupDataService.deleteByGid(Constants.GROUP_DATA_ID_INDEX_MENU);
            // 保存新数据
            systemGroupDataService.saveBatch(dataList, 100);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 页面首页新闻保存
     * @param jsonObject 数据
     * @return Boolean
     */
    @Override
    public Boolean indexNewsSave(JSONObject jsonObject) {
        List<JSONObject> indexNews = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("indexNews"));
        List<SystemGroupData> dataList = convertGroupData(indexNews, Constants.GROUP_DATA_ID_INDEX_NEWS_BANNER);
        if (StrUtil.isNotBlank(jsonObject.getString("indexNews"))) {
        }
        Boolean execute = transactionTemplate.execute(e -> {
            // 先删除历史数据
            systemGroupDataService.deleteByGid(Constants.GROUP_DATA_ID_INDEX_NEWS_BANNER);
            // 保存新数据
            systemGroupDataService.saveBatch(dataList, 100);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 页面用户中心banner保存
     * @param jsonObject 数据
     * @return Boolean
     */
    @Override
    public Boolean userBannerSave(JSONObject jsonObject) {
        List<JSONObject> userBanner = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("userBanner"));
        List<SystemGroupData> dataList = convertGroupData(userBanner, Constants.GROUP_DATA_ID_USER_CENTER_BANNER);
        Boolean execute = transactionTemplate.execute(e -> {
            // 先删除历史数据
            systemGroupDataService.deleteByGid(Constants.GROUP_DATA_ID_USER_CENTER_BANNER);
            // 保存新数据
            systemGroupDataService.saveBatch(dataList, 100);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 页面用户中心导航保存
     * @param jsonObject 数据
     * @return Boolean
     */
    @Override
    public Boolean userMenuSave(JSONObject jsonObject) {
        List<JSONObject> userMenu = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("userMenu"));
        List<SystemGroupData> dataList = convertGroupData(userMenu, Constants.GROUP_DATA_ID_USER_CENTER_MENU);
        Boolean execute = transactionTemplate.execute(e -> {
            // 先删除历史数据
            systemGroupDataService.deleteByGid(Constants.GROUP_DATA_ID_USER_CENTER_MENU);
            // 保存新数据
            systemGroupDataService.saveBatch(dataList, 100);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 页面用户中心商品table保存
     * @param jsonObject 数据
     * @return Boolean
     */
    @Override
    public Boolean indexTableSave(JSONObject jsonObject) {
        List<JSONObject> indexBanner = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("indexTable"));
        List<SystemGroupData> dataList = convertGroupData(indexBanner, Constants.GROUP_DATA_ID_INDEX_EX_BANNER);
        Boolean execute = transactionTemplate.execute(e -> {
            // 先删除历史数据
            systemGroupDataService.deleteByGid(Constants.GROUP_DATA_ID_INDEX_EX_BANNER);
            // 保存新数据
            systemGroupDataService.saveBatch(dataList, 100);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 获取分类页配置
     * @return MyRecord
     */
    @Override
    public MyRecord getCategoryConfig() {
        String categoryConfig = systemConfigService.getValueByKey(Constants.CONFIG_CATEGORY_CONFIG);// 商品分类页配置
        String isShowCategory = systemConfigService.getValueByKey(Constants.CONFIG_IS_SHOW_CATEGORY);// 是否隐藏一级分类
        MyRecord record = new MyRecord();
        record.set("categoryConfig", categoryConfig);
        record.set("isShowCategory", isShowCategory);
        return record;
    }

    @Override
    public Boolean categoryConfigSave(JSONObject jsonObject) {
        String categoryConfig = jsonObject.getString(Constants.CONFIG_CATEGORY_CONFIG);
        String isShowCategory = jsonObject.getString(Constants.CONFIG_IS_SHOW_CATEGORY);
        return transactionTemplate.execute(e -> {
            systemConfigService.updateOrSaveValueByName(Constants.CONFIG_CATEGORY_CONFIG, categoryConfig);
            systemConfigService.updateOrSaveValueByName(Constants.CONFIG_IS_SHOW_CATEGORY, isShowCategory);
            return Boolean.TRUE;
        });
    }

    /**
     * 转换组合数据
     * @param jsonObjectList 数组
     * @param gid gid
     * @return List
     */
    private List<SystemGroupData> convertGroupData(List<JSONObject> jsonObjectList, Integer gid) {
        return jsonObjectList.stream().map(e -> {
            SystemGroupData groupData = new SystemGroupData();
            if (e.containsKey("id") && ObjectUtil.isNotNull(e.getInteger("id"))) {
                groupData.setId(e.getInteger("id"));
            }
            groupData.setGid(gid);
            groupData.setSort(e.getInteger("sort"));
            groupData.setStatus(e.getBoolean("status"));
            // 组装json
            Map<String, Object> jsonMap = CollUtil.newHashMap();
            jsonMap.put("id", e.getInteger("tempid"));
            jsonMap.put("sort", groupData.getSort());
            jsonMap.put("status", groupData.getStatus());
            List<Map<String, Object>> mapList = CollUtil.newArrayList();
            e.remove("id");
            e.remove("gid");
            e.remove("sort");
            e.remove("status");
            e.remove("tempid");
            e.forEach((key, value) -> {
                Map<String, Object> map = CollUtil.newHashMap();
                map.put("name", key);
                map.put("title", key);
                map.put("value", value);
                if (String.valueOf(value).contains(UploadConstants.UPLOAD_FILE_KEYWORD)) {
                    String values = systemAttachmentService.clearPrefix(String.valueOf(value));
                    map.put("value", values);
                }
                mapList.add(map);
            });
            jsonMap.put("fields", mapList);
            groupData.setValue(JSONObject.toJSONString(jsonMap));
            return groupData;
        }).collect(Collectors.toList());
    }

    /**
     * 转换数据
     * @param dataList 数据列表
     * @return List<Map>
     */
    private List<HashMap<String, Object>> convertData(List<SystemGroupData> dataList) {
        return dataList.stream().map(data -> {
            HashMap<String, Object> map = CollUtil.newHashMap();
            map.put("id", data.getId());
            map.put("gid", data.getGid());
            map.put("sort", data.getSort());
            map.put("status", data.getStatus());
            JSONObject jsonObject = JSONObject.parseObject(data.getValue());
            List<SystemFormItemCheckRequest> systemFormItemCheckRequestList = CrmebUtil.jsonToListClass(jsonObject.getString("fields"), SystemFormItemCheckRequest.class);
            systemFormItemCheckRequestList.forEach(e -> {
                map.put(e.getName(), e.getValue());
            });
            map.put("tempid", jsonObject.getInteger("id"));
            return map;
        }).collect(Collectors.toList());


    }

    /**
     * 获取页面底部导航信息
     */
    @Override
    public PageLayoutBottomNavigationResponse getBottomNavigation() {
        PageLayoutBottomNavigationResponse response = new PageLayoutBottomNavigationResponse();
        // 个人中心页服务
        List<SystemGroupData> dataList = systemGroupDataService.findListByGid(Constants.GROUP_DATA_ID_BOTTOM_NAVIGATION);
        response.setBottomNavigationList(convertData(dataList));

        // 是否自定义
        String isCustom = systemConfigService.getValueByKey(Constants.CONFIG_BOTTOM_NAVIGATION_IS_CUSTOM);
        response.setIsCustom(isCustom);
        return response;
    }

    /**
     * 页面底部导航信息保存
     * @return 保存结果
     */
    @Override
    public Boolean bottomNavigationSave(JSONObject jsonObject) {
        String isCustom = jsonObject.getString("isCustom");
        if (StrUtil.isBlank(isCustom)) {
            throw new CrmebException("请选择是否自定义");
        }
        List<JSONObject> bottomNavigationList = CrmebUtil.jsonArrayToJsonObjectList(jsonObject.getJSONArray("bottomNavigationList"));
        if (CollUtil.isEmpty(bottomNavigationList)) {
            throw new CrmebException("请传入底部导航数据");
        }
        List<SystemGroupData> dataList = convertGroupData(bottomNavigationList, Constants.GROUP_DATA_ID_BOTTOM_NAVIGATION);
        return transactionTemplate.execute(e -> {
            // 先删除历史数据
            systemGroupDataService.deleteByGid(Constants.GROUP_DATA_ID_BOTTOM_NAVIGATION);
            // 保存新数据
            systemGroupDataService.saveBatch(dataList, 100);

            systemConfigService.updateOrSaveValueByName(Constants.CONFIG_BOTTOM_NAVIGATION_IS_CUSTOM, isCustom);
            return Boolean.TRUE;
        });
    }

    /**
     * 获取开屏广告配置
     */
    @Override
    public SplashAdConfigVo getSplashAdConfig() {
        List<String> keyList = new ArrayList<>();
        keyList.add(SysConfigConstants.SPLASH_AD_SWITCH);
        keyList.add(SysConfigConstants.SPLASH_AD_SHOW_TIME);
        keyList.add(SysConfigConstants.SPLASH_AD_SHOW_INTERVAL);
        MyRecord myRecord = systemConfigService.getValuesByKeyList(keyList);
        SplashAdConfigVo configVo = new SplashAdConfigVo();
        configVo.setSplashAdSwitch(myRecord.getInt(SysConfigConstants.SPLASH_AD_SWITCH));
        configVo.setSplashAdShowTime(myRecord.getInt(SysConfigConstants.SPLASH_AD_SHOW_TIME));
        configVo.setSplashAdShowInterval(myRecord.getInt(SysConfigConstants.SPLASH_AD_SHOW_INTERVAL));

        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_SPLASH_AD_DATA, Constants.SORT_ASC, null);
        if (CollUtil.isEmpty(configList)) {
            configVo.setAdList(new ArrayList<>());
        } else {
            Iterator<GroupConfig> iterator = configList.iterator();
            List<SplashAdDataVo> voList = new ArrayList<>();
            while (iterator.hasNext()) {
                GroupConfig config = iterator.next();
                SplashAdDataVo vo = new SplashAdDataVo();
                BeanUtils.copyProperties(config, vo);
                voList.add(vo);
            }
            configVo.setAdList(voList);
        }
        return configVo;
    }

    /**
     * 编辑开屏广告配置
     */
    @Override
    public Boolean splashAdConfigSave(SplashAdConfigVo configVo) {
        String adSwitch = configVo.getSplashAdSwitch().equals(1) ? "1" : "0";
        List<GroupConfig> configList = new ArrayList<>();
        if (CollUtil.isNotEmpty(configVo.getAdList())) {
            configList = configVo.getAdList().stream().map(data -> {
                GroupConfig groupConfig = new GroupConfig();
                groupConfig.setTag(GroupConfigConstants.TAG_SPLASH_AD_DATA);
                groupConfig.setName(data.getName());
                groupConfig.setLinkUrl(data.getLinkUrl());
                groupConfig.setImageUrl(data.getImageUrl());
                groupConfig.setSort(data.getSort());
                groupConfig.setStatus(true);
                return groupConfig;
            }).collect(Collectors.toList());
        }
        List<GroupConfig> finalConfigList = configList;
        return transactionTemplate.execute(e -> {
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.SPLASH_AD_SWITCH, adSwitch);
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.SPLASH_AD_SHOW_TIME, configVo.getSplashAdShowTime().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.SPLASH_AD_SHOW_INTERVAL, configVo.getSplashAdShowInterval().toString());

            if (CollUtil.isEmpty(finalConfigList)) {
                groupConfigService.deleteByTag(GroupConfigConstants.TAG_SPLASH_AD_DATA);
            } else {
                groupConfigService.saveList(finalConfigList);
            }
            return Boolean.TRUE;
        });
    }


}
