package com.zbkj.service.service;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.response.pagelayout.PageLayoutBottomNavigationResponse;
import com.zbkj.common.vo.MyRecord;

import java.util.HashMap;

/**
 * 页面布局接口
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public interface PageLayoutService {

    /**
     * 页面首页
     * @return 首页信息
     */
    HashMap<String, Object> index();

    /**
     * 首页保存
     * @param jsonObject 数据
     * @return Boolean
     */
    Boolean save(JSONObject jsonObject);

    /**
     * 页面首页banner保存
     * @param jsonObject 数据
     * @return Boolean
     */
    Boolean indexBannerSave(JSONObject jsonObject);

    /**
     * 页面首页menu保存
     * @param jsonObject 数据
     * @return Boolean
     */
    Boolean indexMenuSave(JSONObject jsonObject);

    /**
     * 页面首页新闻保存
     * @param jsonObject 数据
     * @return Boolean
     */
    Boolean indexNewsSave(JSONObject jsonObject);

    /**
     * 页面用户中心banner保存
     * @param jsonObject 数据
     * @return Boolean
     */
    Boolean userBannerSave(JSONObject jsonObject);

    /**
     * 页面用户中心导航保存
     * @param jsonObject 数据
     * @return Boolean
     */
    Boolean userMenuSave(JSONObject jsonObject);

    /**
     * 页面用户中心商品table保存
     * @param jsonObject 数据
     * @return Boolean
     */
    Boolean indexTableSave(JSONObject jsonObject);

    /**
     * 获取页面底部导航信息
     */
    PageLayoutBottomNavigationResponse getBottomNavigation();

    /**
     * 页面底部导航信息保存
     * @return 保存结果
     */
    Boolean bottomNavigationSave(JSONObject jsonObject);
}
