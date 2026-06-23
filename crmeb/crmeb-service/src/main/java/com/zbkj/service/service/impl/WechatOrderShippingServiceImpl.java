package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.RedisConstatns;
import com.zbkj.common.constants.UserConstants;
import com.zbkj.common.constants.WeChatConstants;
import com.zbkj.common.dto.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.finance.UserRecharge;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.order.StoreOrderInfo;
import com.zbkj.common.model.user.UserToken;
import com.zbkj.common.model.wechat.WechatPayInfo;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.RestTemplateUtil;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 微信订单发货管理服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2023/11/27
 */
@Service
public class WechatOrderShippingServiceImpl implements WechatOrderShippingService {

    private static final Logger logger = LoggerFactory.getLogger(WechatOrderShippingServiceImpl.class);

    @Autowired
    private WechatPayInfoService wechatPayInfoService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private RestTemplateUtil restTemplateUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private StoreOrderService storeOrderService;
    @Autowired
    private WechatNewService wechatNewService;
    @Autowired
    private StoreOrderInfoService storeOrderInfoService;
    @Autowired
    private UserRechargeService userRechargeService;

    /**
     * 录入发货信息
     */
    @Async
    @Override
    public void uploadShippingInfo(String orderNo) {
        StoreOrder order = storeOrderService.getByOderId(orderNo);
        List<StoreOrderInfo> orderInfoList = storeOrderInfoService.getListByOrderNo(orderNo);
        /**
         * 1.获取微信小程序accessToken
         * 2.构建录入发货信息对象
         * 3.调用微信小程序对应接口
         */
        String accessToken = wechatNewService.getMiniAccessToken();

        WechatUploadShippingDto wechatUploadShippingDto = new WechatUploadShippingDto();
        // todo 检查outTradeNo
        WechatPayInfo wechatPayInfo = wechatPayInfoService.getByNo(order.getOutTradeNo());
        if (ObjectUtil.isNull(wechatPayInfo)) {
            throw new CrmebException("未找到对应微信订单");
        }
        WechatOrderKeyDto orderKeyDto = new WechatOrderKeyDto();
        orderKeyDto.setOrderNumberType(2);
        orderKeyDto.setMchid(wechatPayInfo.getMchId());
        orderKeyDto.setTransactionId(wechatPayInfo.getTransactionId());
        orderKeyDto.setOutTradeNo(order.getOutTradeNo());
        wechatUploadShippingDto.setOrderKey(orderKeyDto);

        wechatUploadShippingDto.setLogisticsType(1);
        wechatUploadShippingDto.setDeliveryMode(1);
        wechatUploadShippingDto.setIsAllDelivered(true);
        wechatUploadShippingDto.setUploadTime(CrmebDateUtil.dateToStr(new Date(), Constants.DATE_FORMAT_RFC_3339));

        UserToken userToken = userTokenService.getTokenByUserId(order.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            throw new CrmebException("未找到用户对应的小程序openID");
        }
        WechatOrderPayerDto payerDto = new WechatOrderPayerDto();
        payerDto.setOpenid(userToken.getToken());
        wechatUploadShippingDto.setPayer(payerDto);

        WechatUploadShippingInfoDto shippingInfoDto = new WechatUploadShippingInfoDto();
        shippingInfoDto.setTrackingNo(order.getDeliveryId());
        String expressCompany = getWechatDeliveryIdByOrderExpressName(order.getDeliveryName());
        shippingInfoDto.setExpressCompany(expressCompany);
        String collect = orderInfoList.stream().map(d -> StrUtil.format("{}*{}件", d.getProductName(), d.getPayNum())).collect(Collectors.joining(","));
        if (collect.length() > 120) {
            collect = collect.substring(0, 119);
        }
        shippingInfoDto.setItemDesc(collect);
        if (expressCompany.equals("SF")) {
            WechatShippingContactDto contactDto = new WechatShippingContactDto();
            contactDto.setReceiverContact(maskMobile(order.getUserPhone()));
            shippingInfoDto.setContact(contactDto);
        }
        List<WechatUploadShippingInfoDto> shippingInfoDtoList = new ArrayList<>();
        shippingInfoDtoList.add(shippingInfoDto);
        wechatUploadShippingDto.setShippingList(shippingInfoDtoList);

        String resultData = restTemplateUtil.postStringData(StrUtil.format(WeChatConstants.WECHAT_MINI_UPLOAD_SHIPPING_URL, accessToken), JSONObject.toJSONString(wechatUploadShippingDto));
        JSONObject resultJsonObject = JSONObject.parseObject(resultData);
        try {
            checkWechatResult(resultJsonObject);
        } catch (Exception e) {
            logger.error("微信小程序上传发货管理，失败", e);
        }
        logger.info("微信小程序上传发货管理，result = {}", resultJsonObject);
    }

    private String maskMobile(String phone) {
        if (StrUtil.isBlank(phone)) {
            return "";
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 虚拟商品发货录入
     */
    @Async
    @Override
    public void uploadVirtualShippingInfo(String orderNo) {

        StoreOrder order = storeOrderService.getByOderId(orderNo);
        List<StoreOrderInfo> orderInfoList = storeOrderInfoService.getListByOrderNo(orderNo);
        String accessToken = wechatNewService.getMiniAccessToken();

        WechatUploadShippingDto wechatUploadShippingDto = new WechatUploadShippingDto();

        WechatPayInfo wechatPayInfo = wechatPayInfoService.getByNo(order.getOutTradeNo());
        if (ObjectUtil.isNull(wechatPayInfo)) {
            throw new CrmebException("未找到对应微信订单");
        }
        WechatOrderKeyDto orderKeyDto = new WechatOrderKeyDto();
        orderKeyDto.setOrderNumberType(2);
        orderKeyDto.setMchid(wechatPayInfo.getMchId());
        orderKeyDto.setTransactionId(wechatPayInfo.getTransactionId());
        orderKeyDto.setOutTradeNo(order.getOutTradeNo());
        wechatUploadShippingDto.setOrderKey(orderKeyDto);

        wechatUploadShippingDto.setLogisticsType(3);
        wechatUploadShippingDto.setDeliveryMode(1);
        wechatUploadShippingDto.setIsAllDelivered(true);
        wechatUploadShippingDto.setUploadTime(CrmebDateUtil.dateToStr(new Date(), Constants.DATE_FORMAT_RFC_3339));

        UserToken userToken = userTokenService.getTokenByUserId(order.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            throw new CrmebException("未找到用户对应的小程序openID");
        }
        WechatOrderPayerDto payerDto = new WechatOrderPayerDto();
        payerDto.setOpenid(userToken.getToken());
        wechatUploadShippingDto.setPayer(payerDto);

        WechatUploadShippingInfoDto shippingInfoDto = new WechatUploadShippingInfoDto();
        String collect = orderInfoList.stream().map(d -> StrUtil.format("{}*{}件", d.getProductName(), d.getPayNum())).collect(Collectors.joining(","));
        if (collect.length() > 120) {
            collect = collect.substring(0, 119);
        }
        shippingInfoDto.setItemDesc(collect);
        List<WechatUploadShippingInfoDto> shippingInfoDtoList = new ArrayList<>();
        shippingInfoDtoList.add(shippingInfoDto);
        wechatUploadShippingDto.setShippingList(shippingInfoDtoList);


        String resultData = restTemplateUtil.postStringData(StrUtil.format(WeChatConstants.WECHAT_MINI_UPLOAD_SHIPPING_URL, accessToken), JSONObject.toJSONString(wechatUploadShippingDto));
        JSONObject resultJsonObject = JSONObject.parseObject(resultData);
        try {
            checkWechatResult(resultJsonObject);
        } catch (Exception e) {
            logger.error("微信小程序上传发货管理，失败", e);
        }
        logger.info("微信小程序上传发货管理，result = {}", resultJsonObject);
    }

    /**
     * 核销订单录入
     */
    @Async
    @Override
    public void uploadVerifyShippingInfo(String orderNo) {
        StoreOrder order = storeOrderService.getByOderId(orderNo);
        List<StoreOrderInfo> orderInfoList = storeOrderInfoService.getListByOrderNo(orderNo);
        String accessToken = wechatNewService.getMiniAccessToken();

        WechatUploadShippingDto wechatUploadShippingDto = new WechatUploadShippingDto();

        WechatPayInfo wechatPayInfo = wechatPayInfoService.getByNo(order.getOutTradeNo());
        if (ObjectUtil.isNull(wechatPayInfo)) {
            throw new CrmebException("未找到对应微信订单");
        }
        WechatOrderKeyDto orderKeyDto = new WechatOrderKeyDto();
        orderKeyDto.setOrderNumberType(2);
        orderKeyDto.setMchid(wechatPayInfo.getMchId());
        orderKeyDto.setTransactionId(wechatPayInfo.getTransactionId());
        orderKeyDto.setOutTradeNo(order.getOutTradeNo());
        wechatUploadShippingDto.setOrderKey(orderKeyDto);

        wechatUploadShippingDto.setLogisticsType(4);
        wechatUploadShippingDto.setDeliveryMode(1);
        wechatUploadShippingDto.setIsAllDelivered(true);
        wechatUploadShippingDto.setUploadTime(CrmebDateUtil.dateToStr(new Date(), Constants.DATE_FORMAT_RFC_3339));

        UserToken userToken = userTokenService.getTokenByUserId(order.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            throw new CrmebException("未找到用户对应的小程序openID");
        }
        WechatOrderPayerDto payerDto = new WechatOrderPayerDto();
        payerDto.setOpenid(userToken.getToken());
        wechatUploadShippingDto.setPayer(payerDto);

        WechatUploadShippingInfoDto shippingInfoDto = new WechatUploadShippingInfoDto();
        String collect = orderInfoList.stream().map(d -> StrUtil.format("{}*{}件", d.getProductName(), d.getPayNum())).collect(Collectors.joining(","));
        if (collect.length() > 120) {
            collect = collect.substring(0, 119);
        }
        shippingInfoDto.setItemDesc(collect);
        List<WechatUploadShippingInfoDto> shippingInfoDtoList = new ArrayList<>();
        shippingInfoDtoList.add(shippingInfoDto);
        wechatUploadShippingDto.setShippingList(shippingInfoDtoList);


        String resultData = restTemplateUtil.postStringData(StrUtil.format(WeChatConstants.WECHAT_MINI_UPLOAD_SHIPPING_URL, accessToken), JSONObject.toJSONString(wechatUploadShippingDto));
        JSONObject resultJsonObject = JSONObject.parseObject(resultData);
        try {
            checkWechatResult(resultJsonObject);
        } catch (Exception e) {
            logger.error("微信小程序上传发货管理，失败", e);
        }
        logger.info("微信小程序上传发货管理，result = {}", resultJsonObject);
    }

    /**
     * 批量录入充值订单发货
     */
    @Override
    public void batchUploadRechargeOrderShipping() {
        String shippingSwitch = systemConfigService.getValueByKey(WeChatConstants.CONFIG_WECHAT_ROUTINE_SHIPPING_SWITCH);
        if (StrUtil.isBlank(shippingSwitch) || "0".equals(shippingSwitch)) {
            return;
        }
        List<UserRecharge> rechargeOrderList = userRechargeService.findAwaitUploadWechatList();
        if (CollUtil.isEmpty(rechargeOrderList)) {
            return;
        }
        String accessToken = wechatNewService.getMiniAccessToken();
        for (UserRecharge rechargeOrder : rechargeOrderList) {
            WechatUploadShippingDto wechatUploadShippingDto = new WechatUploadShippingDto();
            // todo 检查outTradeNo
            WechatPayInfo wechatPayInfo = wechatPayInfoService.getByNo(rechargeOrder.getOutTradeNo());
            if (ObjectUtil.isNull(wechatPayInfo)) {
                logger.error("充值订单微信小程序发货，未找到对应微信订单，充值单号={}", rechargeOrder.getOrderId());
                continue;
            }
            WechatOrderKeyDto orderKeyDto = new WechatOrderKeyDto();
            orderKeyDto.setOrderNumberType(2);
            orderKeyDto.setMchid(wechatPayInfo.getMchId());
            orderKeyDto.setTransactionId(wechatPayInfo.getTransactionId());
            orderKeyDto.setOutTradeNo(rechargeOrder.getOutTradeNo());
            wechatUploadShippingDto.setOrderKey(orderKeyDto);

            wechatUploadShippingDto.setLogisticsType(3);
            wechatUploadShippingDto.setDeliveryMode(1);
            wechatUploadShippingDto.setIsAllDelivered(true);
            wechatUploadShippingDto.setUploadTime(CrmebDateUtil.dateToStr(new Date(), Constants.DATE_FORMAT_RFC_3339));

            UserToken userToken = userTokenService.getTokenByUserId(rechargeOrder.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
            if (ObjectUtil.isNull(userToken)) {
                logger.error("充值订单微信小程序发货，未找到用户对应的小程序openID，充值单号={}", rechargeOrder.getOrderId());
                continue;
            }
            WechatOrderPayerDto payerDto = new WechatOrderPayerDto();
            payerDto.setOpenid(userToken.getToken());
            wechatUploadShippingDto.setPayer(payerDto);

            WechatUploadShippingInfoDto shippingInfoDto = new WechatUploadShippingInfoDto();
            shippingInfoDto.setItemDesc(StrUtil.format("用户充值{}余额,赠送{}", rechargeOrder.getPrice(), rechargeOrder.getGivePrice()));
            List<WechatUploadShippingInfoDto> shippingInfoDtoList = new ArrayList<>();
            shippingInfoDtoList.add(shippingInfoDto);
            wechatUploadShippingDto.setShippingList(shippingInfoDtoList);

            String resultData = restTemplateUtil.postStringData(StrUtil.format(WeChatConstants.WECHAT_MINI_UPLOAD_SHIPPING_URL, accessToken), JSONObject.toJSONString(wechatUploadShippingDto));
            JSONObject resultJsonObject = JSONObject.parseObject(resultData);
            try {
                checkWechatResult(resultJsonObject);
            } catch (Exception e) {
                logger.error("微信小程序上传发货管理，失败", e);
                continue;
            }
            rechargeOrder.setIsWechatShipping(true);
            userRechargeService.updateById(rechargeOrder);
            logger.info("微信小程序上传发货管理，result = {}", resultJsonObject);
        }
    }

    /**
     * 获取运力公司名称通过订单发货公司
     *
     * @param expressName 订单快递公司名称
     * @return 运力公司编码
     */
    private String getWechatDeliveryIdByOrderExpressName(String expressName) {
        if (!redisUtil.exists(RedisConstatns.WECHAT_MINI_DELIVERY_KEY)) {
            wechatGetDeliveryList();
        } else {
            Long hashSize = redisUtil.getHashSize(RedisConstatns.WECHAT_MINI_DELIVERY_KEY);
            if (hashSize > 0) {
                wechatGetDeliveryList();
            }
        }
        if (!redisUtil.hHasKey(RedisConstatns.WECHAT_MINI_DELIVERY_KEY, expressName)) {
            // 未从redis中找到对应物流公司，返回固定值,韵达物流
            return "YD";
        }
        Object object = redisUtil.hget(RedisConstatns.WECHAT_MINI_DELIVERY_KEY, expressName);
        return object.toString();
    }

    /**
     * 检查调用微信结果
     * @param resultJsonObject 微信返回结果
     */
    private void checkWechatResult(JSONObject resultJsonObject) {
        if (ObjectUtil.isNull(resultJsonObject)) {
            throw new CrmebException("微信小程序接口异常，无返回结果");
        }
        if (!resultJsonObject.containsKey("errcode")) {
            logger.error("微信小程序接口失败，无errcode对象, errmsg = {}", resultJsonObject.getString("errmsg"));
            throw new CrmebException(StrUtil.format("微信小程序接口失败，无errcode对象, errmsg = {}", resultJsonObject.getString("errmsg")));
        }
        if (!resultJsonObject.getInteger("errcode").equals(0)) {
            logger.error("微信小程序接口失败，errcode = {}, errmsg = {}", resultJsonObject.getInteger("errcode"), resultJsonObject.getString("errmsg"));
            throw new CrmebException(StrUtil.format("微信小程序接口失败，errcode = {}, errmsg = {}", resultJsonObject.getInteger("errcode"), resultJsonObject.getString("errmsg")));
        }
    }

    /**
     * 获取微信运力ID列表
     * 例：
     * {
     *     "errcode": 0,
     *     "delivery_list": [
     *       {
     *           "delivery_id": "(AU)",
     *           "delivery_name": "Interparcel"
     *       },
     *       {
     *           "delivery_id": "BDT",
     *           "delivery_name": "八达通"
     *       },
     *       {
     *           "delivery_id": "YD",
     *           "delivery_name": "韵达速递"
     *       },
     *       ...
     *     ],
     *     "count": 1379
     * }
     */
    private void wechatGetDeliveryList() {
        String accessToken = wechatNewService.getMiniAccessToken();
//        JSONObject jsonObject = restTemplateUtil.post(StrUtil.format(WeChatConstants.WECHAT_MINI_GET_DELIVERY_LIST_URL, accessToken));
        String result = HttpUtil.post(StrUtil.format(WeChatConstants.WECHAT_MINI_GET_DELIVERY_LIST_URL, accessToken), String.valueOf(new HashMap<String, Object>()));
        JSONObject jsonObject = JSONObject.parseObject(result);
        checkWechatResult(jsonObject);
        logger.info("获取微信小程序运力ID，共获取运力数量{}", jsonObject.getInteger("count"));
        JSONArray deliveryListArray = jsonObject.getJSONArray("delivery_list");
        for (int i = 0; i < deliveryListArray.size(); i++) {
            JSONObject deliveryJsonObject = deliveryListArray.getJSONObject(i);
            redisUtil.hset(RedisConstatns.WECHAT_MINI_DELIVERY_KEY, deliveryJsonObject.getString("delivery_name"), deliveryJsonObject.getString("delivery_id"));
        }
    }
}
