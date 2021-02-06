package com.zbkj.crmeb.wechat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.constants.WeChatConstants;
import com.utils.RedisUtil;
import com.zbkj.crmeb.wechat.service.WeChatService;
import com.zbkj.crmeb.wechat.service.WechatPublicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 微信公众号Service实现类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class WechatPublicServiceImpl implements WechatPublicService {

    private static final Logger logger = LoggerFactory.getLogger(WechatPublicServiceImpl.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WeChatService weChatService;


    /**
     * 获取公众号自定义菜单
     * @return Object
     */
    @Override
    public Object getCustomizeMenus() {
        Object list = redisUtil.get(WeChatConstants.REDIS_PUBLIC_MENU_KEY);
        if(list == null || list.equals("")){
            //如果没有， 去读取
            JSONObject tagsList = weChatService.get();
            redisUtil.set(WeChatConstants.REDIS_PUBLIC_MENU_KEY, tagsList);
            list = tagsList;
        }
        return list;
    }

    /**
     * 保存自定义菜单
     * @param data 菜单json
     * @return JSONObject
     */
    @Override
    public JSONObject createMenus(String data) {
        JSONObject jsonObject = weChatService.create(data);
        // 清除历史缓存
        if (redisUtil.exists(WeChatConstants.REDIS_PUBLIC_MENU_KEY)) {
            redisUtil.remove(WeChatConstants.REDIS_PUBLIC_MENU_KEY);
        }
        return jsonObject;
    }

    /**
     * 删除自定义菜单
     * @return
     */
    @Override
    public JSONObject deleteMenus() {
        JSONObject delete = weChatService.delete();
        // 清除历史缓存
        if (redisUtil.exists(WeChatConstants.REDIS_PUBLIC_MENU_KEY)) {
            redisUtil.remove(WeChatConstants.REDIS_PUBLIC_MENU_KEY);
        }
        return delete;
    }
}
