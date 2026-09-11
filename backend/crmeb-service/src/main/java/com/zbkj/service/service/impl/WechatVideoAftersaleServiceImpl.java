package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.WeChatConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.utils.RestTemplateUtil;
import com.zbkj.common.utils.WxUtil;
import com.zbkj.common.vo.*;
import com.zbkj.service.service.WechatNewService;
import com.zbkj.service.service.WechatVideoAftersaleService;
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
public class WechatVideoAftersaleServiceImpl implements WechatVideoAftersaleService {

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private WechatNewService wechatNewService;

    /**
     * 创建售后
     * @return Boolean
     */
    @Override
    public Boolean shopAftersaleAdd(ShopAftersaleAddVo shopAftersaleAddVo) {
        Map<String, Object> afterAddMap = assembleAfterSaleAddMap(shopAftersaleAddVo);
        // 获取accessToken
        String miniAccessToken = wechatNewService.getMiniAccessToken();
        // 请求微信接口
        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_AFTERSALE_ADD_URL, miniAccessToken);
        String stringData = restTemplateUtil.postStringData(url, JSONObject.toJSONString(afterAddMap));
        JSONObject jsonObject = JSONObject.parseObject(stringData);
        WxUtil.checkResult(jsonObject);
        return Boolean.TRUE;
    }

    /**
     * 组装售后map
     */
    private Map<String, Object> assembleAfterSaleAddMap(ShopAftersaleAddVo shopAftersaleAddVo) {
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("out_order_id", shopAftersaleAddVo.getOutOrderId());
        map.put("out_aftersale_id", shopAftersaleAddVo.getOutAftersaleId());
        map.put("openid", shopAftersaleAddVo.getOpenid());
        map.put("type", shopAftersaleAddVo.getType());
        map.put("create_time", shopAftersaleAddVo.getCreateTime());
        map.put("status", shopAftersaleAddVo.getStatus());
        map.put("finish_all_aftersale", shopAftersaleAddVo.getFinishAllAftersale());
        map.put("path", shopAftersaleAddVo.getPath());
        List<AftersaleProductInfoVo> productInfos = shopAftersaleAddVo.getProductInfos();
        List<Map<String, Object>> proMap = productInfos.stream().map(e -> {
            Map<String, Object> proInfoMap = CollUtil.newHashMap();
            proInfoMap.put("out_product_id", e.getOutProductId());
            proInfoMap.put("out_sku_id", e.getOutSkuId());
            proInfoMap.put("product_cnt", e.getProductCnt());
            return proInfoMap;
        }).collect(Collectors.toList());
        map.put("product_infos", proMap);
        return map;
    }

    /**
     * 获取售后
     * @return ShopAftersaleVo
     */
    @Override
    public ShopAftersaleVo shopAftersaleGet(ShopOrderCommonVo shopOrderCommonVo) {
        if (ObjectUtil.isNull(shopOrderCommonVo.getOrderId()) && StrUtil.isBlank(shopOrderCommonVo.getOutOrderId())) {
            throw new CrmebException("订单ID不能为空");
        }
        // 获取accessToken
        String miniAccessToken = wechatNewService.getMiniAccessToken();
        // 请求微信接口
        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_AFTERSALE_GET_URL, miniAccessToken);
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
        ShopAftersaleVo shopAftersaleVo = JSONObject.parseObject(jsonObject.getJSONObject("aftersale_infos").toJSONString(), ShopAftersaleVo.class);
        return shopAftersaleVo;
    }

    /**
     * 更新售后
     * @return Boolean
     */
    @Override
    public Boolean shopAftersaleUpdate(ShopAftersaleUpdateVo shopAftersaleUpdateVo) {
        Map<String, Object> afterUpdateMap = assembleAfterSaleUpdateMap(shopAftersaleUpdateVo);
        // 获取accessToken
        String miniAccessToken = wechatNewService.getMiniAccessToken();
        // 请求微信接口
        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_AFTERSALE_UPDATE_URL, miniAccessToken);
        String stringData = restTemplateUtil.postStringData(url, JSONObject.toJSONString(afterUpdateMap));
        JSONObject jsonObject = JSONObject.parseObject(stringData);
        WxUtil.checkResult(jsonObject);
        return Boolean.TRUE;
    }

    /**
     * 组装更新售后mao
     */
    private Map<String, Object> assembleAfterSaleUpdateMap(ShopAftersaleUpdateVo shopAftersaleUpdateVo) {
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("out_order_id", shopAftersaleUpdateVo.getOutOrderId());
        map.put("openid", shopAftersaleUpdateVo.getOpenid());
        map.put("out_aftersale_id", shopAftersaleUpdateVo.getOutAftersaleId());
        map.put("status", shopAftersaleUpdateVo.getStatus());
        map.put("finish_all_aftersale", shopAftersaleUpdateVo.getFinishAllAftersale());
        return map;
    }
}
