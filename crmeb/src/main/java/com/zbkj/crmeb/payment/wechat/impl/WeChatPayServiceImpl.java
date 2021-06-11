package com.zbkj.crmeb.payment.wechat.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.common.MyRecord;
import com.constants.Constants;
import com.constants.PayConstants;
import com.constants.WeChatConstants;
import com.exception.CrmebException;
import com.utils.*;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.combination.model.StorePink;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.finance.model.UserRecharge;
import com.zbkj.crmeb.finance.service.UserRechargeService;
import com.zbkj.crmeb.payment.service.RechargePayService;
import com.zbkj.crmeb.payment.vo.wechat.*;
import com.zbkj.crmeb.payment.wechat.WeChatPayService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.service.StoreOrderInfoService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.user.service.UserTokenService;
import com.zbkj.crmeb.wechat.service.WeChatService;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * 微信支付
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
@Data
@Service
public class WeChatPayServiceImpl implements WeChatPayService {
    private static final Logger logger = LoggerFactory.getLogger(WeChatPayServiceImpl.class);

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private SystemConfigService systemConfigService;

    private String signKey;

    private PayParamsVo payParamsVo;

    private CreateOrderRequestVo createOrderRequestVo;

    private CreateOrderResponseVo createOrderResponseVo = null;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRechargeService userRechargeService;

    @Autowired
    private RechargePayService rechargePayService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private StorePinkService storePinkService;

    /**
     * 统一下单
     * @param payParamsVo PayParamsVo 支付参数
     * @author Mr.Zhang
     * @since 2020-06-22
     * @return PayResponseVo
     */
    @Override
    public CreateOrderResponseVo create(PayParamsVo payParamsVo){
        try {
            setPayParamsVo(payParamsVo);
            switch (payParamsVo.getFromType()){
                case Constants.PAY_TYPE_WE_CHAT_FROM_H5: //h5
                    setH5PayConfig();
                    break;
                case Constants.PAY_TYPE_WE_CHAT_FROM_PUBLIC: //公众号
                    setPublicPayConfig();
                    break;
                case Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM: //小程序
                    setProgramPayConfig();
                    break;
                default:
                    throw new CrmebException("系统不支持当前支付方式！");
            }
            createOrderWeChatData();
            response();
            return getCreateOrderResponseVo();
        }catch (Exception e){
            e.printStackTrace();
            throw new CrmebException(e.getMessage());
        }
    }

    /**
     * 处理下单之后返回的数据
     * @author Mr.Zhang
     * @since 2020-06-22
     */
    private void response(){
        try{
            String url = WeChatConstants.PAY_API_URL + WeChatConstants.PAY_API_URI;
            String request = XmlUtil.objectToXml(getCreateOrderRequestVo());
            String xml = restTemplateUtil.postXml(url, request);
            HashMap<String, Object> map = XmlUtil.xmlToMap(xml);
            if(null == map){
                throw new CrmebException("微信下单失败！");
            }
            CreateOrderResponseVo responseVo = CrmebUtil.mapToObj(map, CreateOrderResponseVo.class);
            if(responseVo.getReturnCode().toUpperCase().equals("FAIL")){
                throw new CrmebException("微信下单失败1！" +  responseVo.getReturnMsg());
            }

            if(responseVo.getResultCode().toUpperCase().equals("FAIL")){
                throw new CrmebException("微信下单失败2！" + responseVo.getErrCodeDes());
            }

            responseVo.setExtra(getCreateOrderRequestVo().getScene_info());
            setCreateOrderResponseVo(responseVo);
        }catch (Exception e){
            e.printStackTrace();
            throw new CrmebException(e.getMessage());
        }
    }


    /**
     * 设置H5支付配置
     * @author Mr.Zhang
     * @since 2020-06-22
     */
    private void setH5PayConfig() {
        setCreateOrderRequestVo(new CreateOrderRequestVo());
        String appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
        String mchId = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
        setSignKey(systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY));
        getCreateOrderRequestVo().setAppid(appId);
        getCreateOrderRequestVo().setMch_id(mchId);
    }

    /**
     * 设置H5支付配置
     * @author Mr.Zhang
     * @since 2020-06-22
     */
    private void setPublicPayConfig() {
        setCreateOrderRequestVo(new CreateOrderRequestVo());
        String appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
        String mchId = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
        setSignKey(systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY));

        getCreateOrderRequestVo().setAppid(appId);
        getCreateOrderRequestVo().setMch_id(mchId);
        getCreateOrderRequestVo().setTrade_type(WeChatConstants.PAY_TYPE_JS);
        getCreateOrderRequestVo().setOpenid(getOpenId(Constants.THIRD_LOGIN_TOKEN_TYPE_PUBLIC));
    }

    /**
     * 设置JS支付配置
     * @author Mr.Zhang
     * @since 2020-06-22
     */
    private void setProgramPayConfig() {
        setCreateOrderRequestVo(new CreateOrderRequestVo());
        String appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
        String mchId = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
        setSignKey(systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY));

        getCreateOrderRequestVo().setAppid(appId);
        getCreateOrderRequestVo().setMch_id(mchId);
        getCreateOrderRequestVo().setTrade_type(WeChatConstants.PAY_TYPE_JS);
        getCreateOrderRequestVo().setOpenid(getOpenId(Constants.THIRD_LOGIN_TOKEN_TYPE_PROGRAM));
    }

    /**
     * 构建支付下单对象
     * @author Mr.Zhang
     * @since 2020-06-22
     */
    private void createOrderWeChatData(){
        //获取域名
        String domain = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_SITE_URL);
        String apiDomain = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_API_URL);
        //统一下单数据
        getCreateOrderRequestVo().setNotify_url(apiDomain + WeChatConstants.PAY_NOTIFY_API_URI_WECHAT);
        getCreateOrderRequestVo().setNonce_str(DigestUtils.md5Hex(CrmebUtil.getUuid() + CrmebUtil.randomCount(111111, 666666)));
        getCreateOrderRequestVo().setBody(getPayParamsVo().getTitle());
        getCreateOrderRequestVo().setOut_trade_no(getPayParamsVo().getOrderNo());

        //附加数据
        getCreateOrderRequestVo().setAttach(JSONObject.toJSONString(getPayParamsVo().getAttach()));

        getCreateOrderRequestVo().setTotal_fee(getWeChatPrice());
        getCreateOrderRequestVo().setSpbill_create_ip(getPayParamsVo().getClientIp());
        CreateOrderH5SceneInfoVo createOrderH5SceneInfoVo = new CreateOrderH5SceneInfoVo(
                new CreateOrderH5SceneInfoDetailVo(
                        domain,
                        systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_SITE_NAME)
                )
        );
        getCreateOrderRequestVo().setScene_info(JSONObject.toJSONString(createOrderH5SceneInfoVo));
        getCreateOrderRequestVo().setSign(CrmebUtil.getSign(CrmebUtil.objectToMap(getCreateOrderRequestVo()), getSignKey()));
    }

    /**
     * 计算价格微信需要以分为单位，所以需要乘以100
     * @author Mr.Zhang
     * @since 2020-06-22
     * @return String
     */
    private Integer getWeChatPrice(){
        return getPayParamsVo().getPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue();
    }

    /**
     * 构建场景信息对象
     * @author Mr.Zhang
     * @since 2020-06-22
     * @return PayCreateOrderSceneInfoVo
     */
    private String getOpenId(int type) {
        UserToken userToken = userTokenService.getTokenByUserId(getPayParamsVo().getUserId(), type);
        return userToken.getToken();
    }

    /**
     * 微信预下单
     * @param storeOrder 订单
     * @param ip      ip
     * @return
     */
    @Override
    public Map<String, String> unifiedorder(StoreOrder storeOrder, String ip) {
        if (ObjectUtil.isNull(storeOrder)) {
            throw new CrmebException("订单不存在");
        }
        if (storeOrder.getIsDel()) {
            throw new CrmebException("订单已被删除");
        }
        if (storeOrder.getPaid()) {
            throw new CrmebException("订单已支付");
        }
        if (!storeOrder.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            throw new CrmebException("不是微信支付类型订单，请重新选择支付方式");
        }
        // 获取用户openId
        // 根据订单支付类型来判断获取公众号openId还是小程序openId
        UserToken userToken = new UserToken();
        if (storeOrder.getIsChannel() == 0) {// 公众号
            userToken = userTokenService.getTokenByUserId(storeOrder.getUid(), 1);
        }
        if (storeOrder.getIsChannel() == 1) {// 小程序
            userToken = userTokenService.getTokenByUserId(storeOrder.getUid(), 2);
        }
        if (storeOrder.getIsChannel() == 2) {// H5
            userToken.setToken("");
        }

        if (ObjectUtil.isNull(userToken)) {
            throw new CrmebException("该用户没有openId");
        }

        // 获取appid、mch_id
        // 微信签名key
        String appId = "";
        String mchId = "";
        String signKey = "";
        if (storeOrder.getIsChannel() == 0) {// 公众号
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
        }
        if (storeOrder.getIsChannel() == 1) {// 小程序
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY);
        }
        if (storeOrder.getIsChannel() == 2) {// H5,使用公众号的
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
        }

        // 获取微信预下单对象
        CreateOrderRequestVo unifiedorderVo = getUnifiedorderVo(storeOrder, userToken.getToken(), ip, appId, mchId, signKey);
        // 预下单
        CreateOrderResponseVo responseVo = unifiedOrder(unifiedorderVo);

        // 组装前端预下单参数
        Map<String, String> map = new HashMap<>();
        map.put("appId", unifiedorderVo.getAppid());
        map.put("nonceStr", unifiedorderVo.getAppid());
        map.put("package", "prepay_id=".concat(responseVo.getPrepayId()));
        map.put("signType", unifiedorderVo.getSign_type());
        Long currentTimestamp = WxPayUtil.getCurrentTimestamp();
        map.put("timeStamp", Long.toString(currentTimestamp));
        String paySign = WxPayUtil.getSign(map, signKey);
        map.put("paySign", paySign);
        map.put("prepayId", responseVo.getPrepayId());
        map.put("prepayTime", DateUtil.nowDateTimeStr());
        if (storeOrder.getIsChannel() == 2) {
            map.put("mweb_url", responseVo.getMWebUrl());
        }
        if (storeOrder.getIsChannel() == 4 || storeOrder.getIsChannel() == 5) {// App
            map.put("partnerid", mchId);
            map.put("package", responseVo.getPrepayId());
            Map<String, Object> appMap = new HashMap<>();
            appMap.put("appid", unifiedorderVo.getAppid());
            appMap.put("partnerid", mchId);
            appMap.put("prepayid", responseVo.getPrepayId());
            appMap.put("package", "Sign=WXPay");
            appMap.put("noncestr", unifiedorderVo.getAppid());
            appMap.put("timestamp", currentTimestamp);
            logger.info("================================================app支付签名，map = " + appMap);
            String sign = WxPayUtil.getSignObject(appMap, signKey);
            logger.info("================================================app支付签名，sign = " + sign);
            map.put("paySign", sign);
        }
        return map;
    }

    public static void main(String[] args) {
        String signKey = "cd94c0b5fe5ab2d9940bee9cae8391f0";
        Map<String, String> appMap = new HashMap<>();
        appMap.put("appid", "wxa83d6fab40cab13f");
        appMap.put("partnerid", "1519485721");
        appMap.put("prepayid", "wx23155011418859d4aa5802ca703bd80000");
        appMap.put("package", "Sign=WXPay");
        appMap.put("noncestr", "wxa83d6fab40cab13f");
        appMap.put("timestamp", "1616485811");
        logger.info("================================================app支付签名，map = " + appMap);
        String sign = WxPayUtil.getSign(appMap, signKey);
        logger.info("================================================app支付签名，sign = " + sign);
    }

    /**
     * 查询支付结果
     * @param orderNo 订单编号
     * @return
     */
    @Override
    public Boolean queryPayResult(String orderNo) {
        if (StrUtil.isBlank(orderNo)) {
            throw new CrmebException("订单编号不能为空");
        }
        // 切割字符串，判断是支付订单还是充值订单
        String pre = StrUtil.subPre(orderNo, 5);
        if (pre.equals("order")) {// 支付订单
            StoreOrder storeOrder = storeOrderService.getByOderId(orderNo);
            if (ObjectUtil.isNull(storeOrder)) {
                throw new CrmebException("订单不存在");
            }
            if (storeOrder.getIsDel()) {
                throw new CrmebException("订单已被删除");
            }
            if (!storeOrder.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
                throw new CrmebException("不是微信支付类型订单，请重新选择支付方式");
            }

            if (storeOrder.getPaid()) {
                return Boolean.TRUE;
            }

            User user = userService.getById(storeOrder.getUid());
            if (ObjectUtil.isNull(user)) throw new CrmebException("用户不存在");


            // 获取appid、mch_id
            // 微信签名key
            String appId = "";
            String mchId = "";
            String signKey = "";
            if (storeOrder.getIsChannel() == 0) {// 公众号
                appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
                mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
                signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
            }
            if (storeOrder.getIsChannel() == 1) {// 小程序
                appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
                mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
                signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY);
            }
            if (storeOrder.getIsChannel() == 2) {// H5
                appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
                mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
                signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY);
            }

            // 生成查询订单对象
            Map<String, String> payVo = getWxChantQueryPayVo(orderNo, appId, mchId, signKey);
            // 查询订单信息
            MyRecord record = orderPayQuery(payVo);

            Boolean updatePaid = transactionTemplate.execute(e -> {
                storeOrderService.updatePaid(orderNo);
                if (storeOrder.getUseIntegral() > 0) {
                    userService.updateIntegral(user, storeOrder.getUseIntegral(), "sub");
                }
                // 处理拼团
                if (storeOrder.getCombinationId() > 0) {
                    // 判断拼团团长是否存在
                    StorePink headPink = new StorePink();
                    Integer pinkId = storeOrder.getPinkId();
                    if (pinkId > 0) {
                        headPink = storePinkService.getById(pinkId);
                        if (ObjectUtil.isNull(headPink) || headPink.getIsRefund().equals(true) || headPink.getStatus() == 3) {
                            pinkId = 0;
                        }
                    }
                    StoreCombination storeCombination = storeCombinationService.getById(storeOrder.getCombinationId());
                    // 如果拼团人数已满，重新开团
                    if (pinkId > 0) {
                        Integer count = storePinkService.getCountByKid(pinkId);
                        if (count >= storeCombination.getPeople()) {
                            pinkId = 0;
                        }
                    }
                    // 生成拼团表数据
                    StorePink storePink = new StorePink();
                    storePink.setUid(user.getUid());
                    storePink.setAvatar(user.getAvatar());
                    storePink.setNickname(user.getNickname());
                    storePink.setOrderId(storeOrder.getOrderId());
                    storePink.setOrderIdKey(storeOrder.getId());
                    storePink.setTotalNum(storeOrder.getTotalNum());
                    storePink.setTotalPrice(storeOrder.getTotalPrice());
                    storePink.setCid(storeCombination.getId());
                    storePink.setPid(storeCombination.getProductId());
                    storePink.setPeople(storeCombination.getPeople());
                    storePink.setPrice(storeCombination.getPrice());
                    Integer effectiveTime = storeCombination.getEffectiveTime();// 有效小时数
                    DateTime dateTime = cn.hutool.core.date.DateUtil.date();
                    storePink.setAddTime(dateTime.getTime());
                    if (pinkId > 0) {
                        storePink.setStopTime(headPink.getStopTime());
                    } else {
                        DateTime hourTime = cn.hutool.core.date.DateUtil.offsetHour(dateTime, effectiveTime);
                        long stopTime =  hourTime.getTime();
                        if (stopTime > storeCombination.getStopTime()) {
                            stopTime = storeCombination.getStopTime();
                        }
                        storePink.setStopTime(stopTime);
                    }
                    storePink.setKId(pinkId);
                    storePink.setIsTpl(false);
                    storePink.setIsRefund(false);
                    storePink.setStatus(1);
                    storePinkService.save(storePink);
                    // 如果是开团，需要更新订单数据
                    storeOrder.setPinkId(storePink.getId());
                    storeOrderService.updateById(storeOrder);
                }
                return Boolean.TRUE;
            });
            if (!updatePaid) {
                throw new CrmebException("支付成功更新订单失败");
            }
            // 添加支付成功task
            redisUtil.lPush(Constants.ORDER_TASK_PAY_SUCCESS_AFTER, orderNo);
            return Boolean.TRUE;
        }
        // 充值订单
        UserRecharge userRecharge = new UserRecharge();
        userRecharge.setOrderId(orderNo);
        userRecharge = userRechargeService.getInfoByEntity(userRecharge);
        if(ObjectUtil.isNull(userRecharge)){
            throw new CrmebException("没有找到订单信息");
        }
        if(userRecharge.getPaid()){
            return Boolean.TRUE;
        }
        // 查询订单
        // 获取appid、mch_id
        // 微信签名key
        String appId = "";
        String mchId = "";
        String signKey = "";
        if (userRecharge.getRechargeType().equals("public")) {// 公众号
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
        }
        if (userRecharge.getRechargeType().equals("routine")) {// 小程序
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY);
        }
        // 生成查询订单对象
        Map<String, String> payVo = getWxChantQueryPayVo(orderNo, appId, mchId, signKey);
        // 查询订单信息
        MyRecord record = orderPayQuery(payVo);
        // 支付成功处理
        Boolean rechargePayAfter = rechargePayService.paySuccess(userRecharge);
        if (!rechargePayAfter) {
            throw new CrmebException("wechat pay error : 数据保存失败==》" + orderNo);
        }
        return rechargePayAfter;
    }

    /**
     * 微信充值预下单接口
     * @param userRecharge 充值订单
     * @param clientIp      ip
     * @return
     */
    @Override
    public Map<String, String> unifiedRecharge(UserRecharge userRecharge, String clientIp) {
        if (ObjectUtil.isNull(userRecharge)) {
            throw new CrmebException("订单不存在");
        }
        // 获取用户openId
        // 根据订单支付类型来判断获取公众号openId还是小程序openId
        UserToken userToken = new UserToken();
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_PUBLIC)) {// 公众号
            userToken = userTokenService.getTokenByUserId(userRecharge.getUid(), 1);
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM)) {// 小程序
            userToken = userTokenService.getTokenByUserId(userRecharge.getUid(), 2);
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_H5)) {// H5
            userToken.setToken("");
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS)) {// app ios
            userToken = userTokenService.getTokenByUserId(userRecharge.getUid(), 5);
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID)) {// app android
            userToken = userTokenService.getTokenByUserId(userRecharge.getUid(), 6);
        }

        if (ObjectUtil.isNull(userToken)) {
            throw new CrmebException("该用户没有openId");
        }

        // 获取appid、mch_id
        // 微信签名key
        String appId = "";
        String mchId = "";
        String signKey = "";
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_PUBLIC)) {// 公众号
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM)) {// 小程序
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY);
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_H5)) {// H5,使用公众号的
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS) || userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID)) {// H5,使用公众号的
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_APP_KEY);
        }

        // 获取微信预下单对象
        CreateOrderRequestVo unifiedorderVo = getUnifiedorderVo(userRecharge, userToken.getToken(), clientIp, appId, mchId, signKey);
        // 预下单
        CreateOrderResponseVo responseVo = unifiedOrder(unifiedorderVo);

        // 组装前端预下单参数
        Map<String, String> map = new HashMap<>();
        map.put("appId", unifiedorderVo.getAppid());
        map.put("nonceStr", unifiedorderVo.getNonce_str());
        map.put("package", "prepay_id=".concat(responseVo.getPrepayId()));
        map.put("signType", unifiedorderVo.getSign_type());
        Long currentTimestamp = WxPayUtil.getCurrentTimestamp();
        map.put("timeStamp", Long.toString(currentTimestamp));
        String paySign = WxPayUtil.getSign(map, signKey);
        map.put("paySign", paySign);
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_H5)) {
            map.put("mweb_url", responseVo.getMWebUrl());
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS) || userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID)) {// H5,使用公众号的
            map.put("partnerid", mchId);
            map.put("package", responseVo.getPrepayId());
            Map<String, Object> appMap = new HashMap<>();
            appMap.put("appid", unifiedorderVo.getAppid());
            appMap.put("partnerid", mchId);
            appMap.put("prepayid", responseVo.getPrepayId());
            appMap.put("package", "Sign=WXPay");
            appMap.put("noncestr", unifiedorderVo.getNonce_str());
            appMap.put("timestamp", currentTimestamp);
            logger.info("================================================app支付签名，map = " + appMap);
            String sign = WxPayUtil.getSignObject(appMap, signKey);
            logger.info("================================================app支付签名，sign = " + sign);
            map.put("paySign", sign);
        }
        return map;
    }

    private MyRecord orderPayQuery(Map<String, String> payVo) {
        String url = PayConstants.WX_PAY_API_URL + PayConstants.WX_PAY_ORDER_QUERY_API_URI;
        try {
            String request = XmlUtil.mapToXml(payVo);
            String xml = restTemplateUtil.postXml(url, request);
            HashMap<String, Object> map = XmlUtil.xmlToMap(xml);
            MyRecord record = new MyRecord();
            if(null == map){
                throw new CrmebException("微信订单查询失败！");
            }
            record.setColums(map);
            if (record.getStr("return_code").toUpperCase().equals("FAIL")){
                throw new CrmebException("微信订单查询失败1！" +  record.getStr("return_msg"));
            }

            if (record.getStr("result_code").toUpperCase().equals("FAIL")){
                throw new CrmebException("微信订单查询失败2！" + record.getStr("err_code") + record.getStr("err_code_des"));
            }
            if (!record.getStr("trade_state").toUpperCase().equals("SUCCESS")){
                throw new CrmebException("微信订单支付失败！" + record.getStr("trade_state"));
            }

            return record;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrmebException("查询微信订单mapToXml异常===》" + e.getMessage());
        }
    }

    /**
     * 生成微信查询订单对象
     * @return
     */
    private Map<String, String> getWxChantQueryPayVo(String orderNo, String appId, String mchId, String signKey) {
        Map<String, String> map = CollUtil.newHashMap();
        map.put("appid", appId);
        map.put("mch_id", mchId);
        map.put("out_trade_no", orderNo);
        map.put("nonce_str", WxPayUtil.getNonceStr());
        map.put("sign_type", PayConstants.WX_PAY_SIGN_TYPE_MD5);
        map.put("sign", WxPayUtil.getSign(map, signKey));
        return map;
    }

    /**
     * 获取微信预下单对象
     * @return
     */
    private CreateOrderRequestVo getUnifiedorderVo(StoreOrder storeOrder, String openid, String ip, String appId, String mchId, String signKey) {

        // 获取域名
        String domain = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_SITE_URL);
        String apiDomain = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_API_URL);

        AttachVo attachVo = new AttachVo(Constants.SERVICE_PAY_TYPE_ORDER, storeOrder.getUid());
        CreateOrderRequestVo vo = new CreateOrderRequestVo();

        vo.setAppid(appId);
        vo.setMch_id(mchId);
        vo.setNonce_str(WxPayUtil.getNonceStr());
        vo.setSign_type(PayConstants.WX_PAY_SIGN_TYPE_MD5);
        vo.setBody(PayConstants.PAY_BODY);
        vo.setAttach(JSONObject.toJSONString(attachVo));
        vo.setOut_trade_no(storeOrder.getOrderId());
        // 订单中使用的是BigDecimal,这里要转为Integer类型
        vo.setTotal_fee(storeOrder.getPayPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        vo.setSpbill_create_ip(ip);
        vo.setNotify_url(apiDomain + PayConstants.WX_PAY_NOTIFY_API_URI);
        vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_JS);
        vo.setOpenid(openid);
        if (storeOrder.getIsChannel() == 2){// H5
            vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_H5);
            vo.setOpenid(null);
        }
        CreateOrderH5SceneInfoVo createOrderH5SceneInfoVo = new CreateOrderH5SceneInfoVo(
                new CreateOrderH5SceneInfoDetailVo(
                        domain,
                        systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_SITE_NAME)
                )
        );
        vo.setScene_info(JSONObject.toJSONString(createOrderH5SceneInfoVo));
        String sign = WxPayUtil.getSign(vo, signKey);
        vo.setSign(sign);
        return vo;
    }

    /**
     * 获取微信预下单对象
     * @return
     */
    private CreateOrderRequestVo getUnifiedorderVo(UserRecharge userRecharge, String openid, String ip, String appId, String mchId, String signKey) {

        // 获取域名
        String domain = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_SITE_URL);
        String apiDomain = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_API_URL);

        AttachVo attachVo = new AttachVo(Constants.SERVICE_PAY_TYPE_RECHARGE, userRecharge.getUid());
        CreateOrderRequestVo vo = new CreateOrderRequestVo();

        vo.setAppid(appId);
        vo.setMch_id(mchId);
        vo.setNonce_str(WxPayUtil.getNonceStr());
        vo.setSign_type(PayConstants.WX_PAY_SIGN_TYPE_MD5);
        vo.setBody(PayConstants.PAY_BODY);
        vo.setAttach(JSONObject.toJSONString(attachVo));
        vo.setOut_trade_no(userRecharge.getOrderId());
        // 订单中使用的是BigDecimal,这里要转为Integer类型
        vo.setTotal_fee(userRecharge.getPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        vo.setSpbill_create_ip(ip);
        vo.setNotify_url(apiDomain + PayConstants.WX_PAY_NOTIFY_API_URI);
        vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_JS);
        vo.setOpenid(openid);
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_H5)){// H5
            vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_H5);
            vo.setOpenid(null);
        }
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS) || userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID)) {
            vo.setTrade_type("APP");
            vo.setOpenid(null);
        }
        CreateOrderH5SceneInfoVo createOrderH5SceneInfoVo = new CreateOrderH5SceneInfoVo(
                new CreateOrderH5SceneInfoDetailVo(
                        domain,
                        systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_SITE_NAME)
                )
        );
        vo.setScene_info(JSONObject.toJSONString(createOrderH5SceneInfoVo));
        String sign = WxPayUtil.getSign(vo, signKey);
        vo.setSign(sign);
        return vo;
    }

    /**
     * 作用：统一下单<br>
     * 场景：公共号支付、扫码支付、APP支付
     *
     * @param vo 向wxpay post的请求数据
     * @return API返回数据
     */
    private CreateOrderResponseVo unifiedOrder(CreateOrderRequestVo vo) {
        try {
            String url = PayConstants.WX_PAY_API_URL + PayConstants.WX_PAY_API_URI;
            String request = XmlUtil.objectToXml(vo);
            String xml = restTemplateUtil.postXml(url, request);
            HashMap<String, Object> map = XmlUtil.xmlToMap(xml);
            if(null == map){
                throw new CrmebException("微信下单失败！");
            }
            CreateOrderResponseVo responseVo = CrmebUtil.mapToObj(map, CreateOrderResponseVo.class);
            if(responseVo.getReturnCode().toUpperCase().equals("FAIL")){
                throw new CrmebException("微信下单失败1！" +  responseVo.getReturnMsg());
            }

            if(responseVo.getResultCode().toUpperCase().equals("FAIL")){
                throw new CrmebException("微信下单失败2！" + responseVo.getErrCodeDes());
            }

            responseVo.setExtra(vo.getScene_info());
            return responseVo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrmebException(e.getMessage());
        }
    }

}
