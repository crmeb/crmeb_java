package com.zbkj.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.WeChatConstants;
import com.zbkj.common.utils.RestTemplateUtil;
import com.zbkj.common.utils.WxUtil;
import com.zbkj.common.vo.BaseResultResponseVo;
import com.zbkj.common.vo.RegisterCheckResponseVo;
import com.zbkj.service.service.WechatNewService;
import com.zbkj.service.service.WechatVideoShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 申请接入
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
public class WechatVideoShopServiceImpl implements WechatVideoShopService {

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private WechatNewService wechatNewService;

    /**
     * 接入申请
     *
     * @return 接入结果
     */
    @Override
    public BaseResultResponseVo shopRegisterApply() {
        // get accessToken
        String miniAccessToken = wechatNewService.getMiniAccessToken();
        // 请求微信接口
        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_REGISTER_APPLY, miniAccessToken);
        Map<String, Object> map = new HashMap<>();
        String mapData = restTemplateUtil.postMapData(url, map);
        JSONObject jsonObject = JSONObject.parseObject(mapData);
        WxUtil.checkResult(jsonObject);
        return JSONObject.parseObject(jsonObject.toJSONString(),BaseResultResponseVo.class);
    }

    /**
     * 获取接入状态
     *
     * @return 接入状态结果
     */
    @Override
    public RegisterCheckResponseVo shopRegisterCheck() {
        // get accessToken
        String miniAccessToken = wechatNewService.getMiniAccessToken();
        // 请求微信接口
        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_REGISTER_CHECK, miniAccessToken);
        Map<String, Object> map = new HashMap<>();
        String mapData = restTemplateUtil.postMapData(url, map);
        JSONObject jsonObject = JSONObject.parseObject(mapData);
        WxUtil.checkResult(jsonObject);
        return JSONObject.parseObject(jsonObject.toJSONString(),RegisterCheckResponseVo.class);
    }
}
