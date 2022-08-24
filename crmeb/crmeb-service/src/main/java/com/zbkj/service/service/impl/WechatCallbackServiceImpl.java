package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.wechat.WechatCallback;
import com.zbkj.service.dao.WechatCallbackDao;
import com.zbkj.service.service.WechatCallbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 微信小程序回调Service
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
@Service
public class WechatCallbackServiceImpl extends ServiceImpl<WechatCallbackDao, WechatCallback> implements WechatCallbackService {

    private static final Logger logger = LoggerFactory.getLogger(WechatCallbackServiceImpl.class);

    @Resource
    private WechatCallbackDao dao;

    /**
     * 微信回调
     * @param request request
     * @return String
     */
    @Override
    public String callback(String request) {
        System.out.println("微信小程序回调：" + request);
        JSONObject jsonObject = JSONObject.parseObject(request);
        System.out.println("微信小程序回调jsonObject：" + jsonObject);
        WechatCallback wechatCallback = new WechatCallback();
        wechatCallback.setToUserName(jsonObject.getString("ToUserName"));
        wechatCallback.setFromUserName(jsonObject.getString("FromUserName"));
        wechatCallback.setCreateTime(jsonObject.getLong("CreateTime"));
        wechatCallback.setMsgType(jsonObject.getString("MsgType"));
        wechatCallback.setEvent(jsonObject.getString("Event"));
        wechatCallback.setAddTime(DateUtil.date());
        wechatCallback.setContent(request);
        save(wechatCallback);
        switch (wechatCallback.getEvent()) {
        }
        return "success";
    }
}

