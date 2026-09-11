package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.WeChatConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.utils.RestTemplateUtil;
import com.zbkj.common.utils.WxUtil;
import com.zbkj.common.vo.DeliveryCompanyVo;
import com.zbkj.common.vo.DeliveryInfoVo;
import com.zbkj.common.vo.DeliverySendVo;
import com.zbkj.common.vo.ShopOrderCommonVo;
import com.zbkj.service.service.WechatNewService;
import com.zbkj.service.service.WechatVideoDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
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
public class WechatVideoDeliveryServiceImpl implements WechatVideoDeliveryService {

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private WechatNewService wechatNewService;

    /**
     * 获取快递公司列表
     * @return List<DeliveryCompanyVo>
     */
    @Override
    public List<DeliveryCompanyVo> shopDeliveryGetCompanyList() {
        // 获取accessToken
        String miniAccessToken = wechatNewService.getMiniAccessToken();
        // 请求微信接口
        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_DELIVERY_GET_COMPANY_LIST_URL, miniAccessToken);
        String stringData = restTemplateUtil.postStringData(url, "{}");
        JSONObject jsonObject = JSONObject.parseObject(stringData);
        WxUtil.checkResult(jsonObject);
        List<DeliveryCompanyVo> voList = JSONArray.parseArray(jsonObject.getJSONArray("company_list").toJSONString(), DeliveryCompanyVo.class);
        return voList;
    }

    /**
     * 订单发货
     * @return Boolean
     */
    @Override
    public Boolean shopDeliverySend(DeliverySendVo deliverySendVo) {
        Map<String, Object> sendMap = assembleSendMap(deliverySendVo);
        // 获取accessToken
        String miniAccessToken = wechatNewService.getMiniAccessToken();
        // 请求微信接口
        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_DELIVERY_SEND_URL, miniAccessToken);
        String stringData = restTemplateUtil.postStringData(url, JSONObject.toJSONString(sendMap));
        JSONObject jsonObject = JSONObject.parseObject(stringData);
        WxUtil.checkResult(jsonObject);
        return Boolean.TRUE;
    }

    private Map<String, Object> assembleSendMap(DeliverySendVo deliverySendVo) {
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("out_order_id", deliverySendVo.getOutOrderId());
        map.put("openid", deliverySendVo.getOpenid());
        map.put("finish_all_delivery", deliverySendVo.getFinishSllDelivery());
        List<DeliveryInfoVo> deliveryList = deliverySendVo.getDeliveryList();
        List<Map<String, Object>> infoMapList = deliveryList.stream().map(e -> {
            Map<String, Object> infoMap = CollUtil.newHashMap();
            infoMap.put("delivery_id", e.getDeliveryId());
            infoMap.put("waybill_id", e.getWaybillId());
            return infoMap;
        }).collect(Collectors.toList());
        map.put("delivery_list", infoMapList);
        return map;
    }

    /**
     * 订单确认收货
     * 把订单状态从30（待收货）流转到100（完成）
     * @return Boolean
     */
    @Override
    public Boolean shopDeliveryRecieve(ShopOrderCommonVo shopOrderCommonVo) {
        if (ObjectUtil.isNull(shopOrderCommonVo.getOrderId()) && StrUtil.isBlank(shopOrderCommonVo.getOutOrderId())) {
            throw new CrmebException("订单ID不能为空");
        }
        // 获取accessToken
        String miniAccessToken = wechatNewService.getMiniAccessToken();
        // 请求微信接口
        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_DELIVERY_RECIEVE_URL, miniAccessToken);
        Map<String, Object> map = new HashMap<>();
        if (ObjectUtil.isNotNull(shopOrderCommonVo.getOrderId())) {
            map.put("order_id", shopOrderCommonVo.getOrderId());
        }
        if (StrUtil.isNotBlank(shopOrderCommonVo.getOutOrderId())) {
            map.put("out_order_id", shopOrderCommonVo.getOutOrderId());
        }
        map.put("openid", shopOrderCommonVo.getOpenid());
        String mapData = restTemplateUtil.postMapData(url, map);
        JSONObject jsonObject = JSONObject.parseObject(mapData);
        WxUtil.checkResult(jsonObject);
        return Boolean.TRUE;
    }
}
