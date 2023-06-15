package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.PayConstants;
import com.zbkj.common.constants.WeChatConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.wechat.WechatExceptions;
import com.zbkj.common.model.wechat.WechatPayInfo;
import com.zbkj.common.response.WeChatJsSdkConfigResponse;
import com.zbkj.common.token.WeChatOauthToken;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.RestTemplateUtil;
import com.zbkj.common.utils.XmlUtil;
import com.zbkj.common.vo.*;
import com.zbkj.service.service.SystemConfigService;
import com.zbkj.service.service.WechatExceptionsService;
import com.zbkj.service.service.WechatNewService;
import com.zbkj.service.service.WechatPayInfoService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *  微信公用服务实现类
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Service
public class WechatNewServiceImpl implements WechatNewService {
    private static final Logger logger = LoggerFactory.getLogger(WechatNewServiceImpl.class);

    @Autowired
    private CrmebConfig crmebConfig;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private WechatExceptionsService wechatExceptionsService;

    @Autowired
    private WechatPayInfoService wechatPayInfoService;

    /**
     * 获取公众号accessToken
     */
    @Override
    public String getPublicAccessToken() {
        boolean exists = redisUtil.exists(WeChatConstants.REDIS_WECAHT_PUBLIC_ACCESS_TOKEN_KEY);
        if (exists) {
            Object accessToken = redisUtil.get(WeChatConstants.REDIS_WECAHT_PUBLIC_ACCESS_TOKEN_KEY);
            return accessToken.toString();
        }
        String appId = systemConfigService.getValueByKey(WeChatConstants.WECHAT_PUBLIC_APPID);
        if (StrUtil.isBlank(appId)) {
            throw new CrmebException("微信公众号appId未设置");
        }
        String secret = systemConfigService.getValueByKey(WeChatConstants.WECHAT_PUBLIC_APPSECRET);
        if (StrUtil.isBlank(secret)) {
            throw new CrmebException("微信公众号secret未设置");
        }
        WeChatAccessTokenVo accessTokenVo = getAccessToken(appId, secret, "public");
        // 缓存accessToken
        redisUtil.set(WeChatConstants.REDIS_WECAHT_PUBLIC_ACCESS_TOKEN_KEY, accessTokenVo.getAccessToken(),
                accessTokenVo.getExpiresIn().longValue() - 1800L, TimeUnit.SECONDS);
        return accessTokenVo.getAccessToken();
    }


    /**
     * 获取小程序accessToken
     * @return accessToken
     */
    @Override
    public String getMiniAccessToken() {
        boolean exists = redisUtil.exists(WeChatConstants.REDIS_WECAHT_MINI_ACCESS_TOKEN_KEY);
        if (exists) {
            Object accessToken = redisUtil.get(WeChatConstants.REDIS_WECAHT_MINI_ACCESS_TOKEN_KEY);
            return accessToken.toString();
        }
        String appId = systemConfigService.getValueByKey(WeChatConstants.WECHAT_MINI_APPID);
        if (StrUtil.isBlank(appId)) {
            throw new CrmebException("微信小程序appId未设置");
        }
        String secret = systemConfigService.getValueByKey(WeChatConstants.WECHAT_MINI_APPSECRET);
        if (StrUtil.isBlank(secret)) {
            throw new CrmebException("微信小程序secret未设置");
        }
        WeChatAccessTokenVo accessTokenVo = getAccessToken(appId, secret, "mini");
        // 缓存accessToken
        redisUtil.set(WeChatConstants.REDIS_WECAHT_MINI_ACCESS_TOKEN_KEY, accessTokenVo.getAccessToken(),
                accessTokenVo.getExpiresIn().longValue() - 1800L, TimeUnit.SECONDS);
        return accessTokenVo.getAccessToken();
    }

    /**
     * 获取开放平台access_token
     * 通过 code 获取
     * 公众号使用
     * @return 开放平台accessToken对象
     */
    @Override
    public WeChatOauthToken getOauth2AccessToken(String code) {
        String appId = systemConfigService.getValueByKey(WeChatConstants.WECHAT_PUBLIC_APPID);
        if (StrUtil.isBlank(appId)) {
            throw new CrmebException("微信公众号appId未设置");
        }
        String secret = systemConfigService.getValueByKey(WeChatConstants.WECHAT_PUBLIC_APPSECRET);
        if (StrUtil.isBlank(secret)) {
            throw new CrmebException("微信公众号secret未设置");
        }
        String url = StrUtil.format(WeChatConstants.WECHAT_OAUTH2_ACCESS_TOKEN_URL, appId, secret, code);
        JSONObject data = restTemplateUtil.getData(url);
        if (ObjectUtil.isNull(data)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (data.containsKey("errcode") && !"0".equals(data.getString("errcode"))) {
            if (data.containsKey("errmsg")) {
                // 保存到微信异常表
                wxExceptionDispose(data, "微信获取开放平台access_token异常");
                throw new CrmebException("微信接口调用失败：" + data.getString("errcode") + data.getString("errmsg"));
            }
        }
        return JSONObject.parseObject(data.toJSONString(), WeChatOauthToken.class);
    }

    /**
     * 获取开放平台用户信息
     * @param accessToken 调用凭证
     * @param openid 普通用户的标识，对当前开发者帐号唯一
     * 公众号使用
     * @return 开放平台用户信息对象
     */
    @Override
    public WeChatAuthorizeLoginUserInfoVo getSnsUserInfo(String accessToken, String openid) {
        String url = StrUtil.format(WeChatConstants.WECHAT_SNS_USERINFO_URL, accessToken, openid, "zh_CN");
        JSONObject data = restTemplateUtil.getData(url);
        if (ObjectUtil.isNull(data)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (data.containsKey("errcode") && !"0".equals(data.getString("errcode"))) {
            if (data.containsKey("errmsg")) {
                // 保存到微信异常表
                wxExceptionDispose(data, "微信获取开放平台用户信息异常");
                throw new CrmebException("微信接口调用失败：" + data.getString("errcode") + data.getString("errmsg"));
            }
        }
        return JSONObject.parseObject(data.toJSONString(), WeChatAuthorizeLoginUserInfoVo.class);
    }

    /**
     * 小程序登录凭证校验
     * @return 小程序登录校验对象
     */
    @Override
    public WeChatMiniAuthorizeVo miniAuthCode(String code) {
        String appId = systemConfigService.getValueByKey(WeChatConstants.WECHAT_MINI_APPID);
        if (StrUtil.isBlank(appId)) {
            throw new CrmebException("微信小程序appId未设置");
        }
        String secret = systemConfigService.getValueByKey(WeChatConstants.WECHAT_MINI_APPSECRET);
        if (StrUtil.isBlank(secret)) {
            throw new CrmebException("微信小程序secret未设置");
        }
        String url = StrUtil.format(WeChatConstants.WECHAT_MINI_SNS_AUTH_CODE2SESSION_URL, appId, secret, code);
        JSONObject data = restTemplateUtil.getData(url);
        if (ObjectUtil.isNull(data)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (data.containsKey("errcode") && !"0".equals(data.getString("errcode"))) {
            if (data.containsKey("errmsg")) {
                // 保存到微信异常表
                wxExceptionDispose(data, "微信小程序登录凭证校验异常");
                throw new CrmebException("微信接口调用失败：" + data.getString("errcode") + data.getString("errmsg"));
            }
        }
        return JSONObject.parseObject(data.toJSONString(), WeChatMiniAuthorizeVo.class);
    }

    /**
     * 获取微信公众号js配置参数
     * @return WeChatJsSdkConfigResponse
     */
    @Override
    public WeChatJsSdkConfigResponse getJsSdkConfig(String url) {
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new CrmebException("url无法解析！");
        }

        String appId = systemConfigService.getValueByKey(WeChatConstants.WECHAT_PUBLIC_APPID);
        if (StrUtil.isBlank(appId)) {
            throw new CrmebException("微信公众号appId未设置");
        }
        String ticket = getJsApiTicket();
        String nonceStr = CrmebUtil.getUuid();
        Long timestamp = DateUtil.currentSeconds();
        String signature = getJsSDKSignature(nonceStr, ticket, timestamp , url);

        WeChatJsSdkConfigResponse response = new WeChatJsSdkConfigResponse();
        response.setUrl(url);
        response.setAppId(appId);
        response.setNonceStr(nonceStr);
        response.setTimestamp(timestamp);
        response.setSignature(signature);
        response.setJsApiList(CrmebUtil.stringToArrayStr(WeChatConstants.PUBLIC_API_JS_API_SDK_LIST));
        response.setDebug(crmebConfig.isWechatJsApiDebug());
        return response;
    }

    /**
     * 生成小程序码
     * @param page 必须是已经发布的小程序存在的页面
     * @param scene 最大32个可见字符，只支持数字，大小写英文以及部分特殊字符：!#$&'()*+,/:;=?@-._~，其它字符请自行编码为合法字符
     * @return 小程序码
     */
    @Override
    public String createQrCode(String page, String scene) {
        String miniAccessToken = getMiniAccessToken();
        String url = StrUtil.format(WeChatConstants.WECHAT_MINI_QRCODE_UNLIMITED_URL, miniAccessToken);
        HashMap<String, Object> map = new HashMap<>();
        map.put("scene", scene);
        map.put("page", page);
        map.put("width", 200);
        byte[] bytes = restTemplateUtil.postJsonDataAndReturnBuffer(url, new JSONObject(map));
        String response = new String(bytes);
        if (StringUtils.contains(response,"errcode")) {
            logger.error("微信生成小程序码异常"+response);
            JSONObject data = JSONObject.parseObject(response);
            // 保存到微信异常表
            wxExceptionDispose(data, "微信小程序生成小程序码异常");
            if ("40001".equals(data.getString("errcode"))) {
                redisUtil.delete(WeChatConstants.REDIS_WECAHT_MINI_ACCESS_TOKEN_KEY);
                miniAccessToken = getMiniAccessToken();
                url = StrUtil.format(WeChatConstants.WECHAT_MINI_QRCODE_UNLIMITED_URL, miniAccessToken);
                bytes = restTemplateUtil.postJsonDataAndReturnBuffer(url, new JSONObject(map));
                response = new String(bytes);
                if (StringUtils.contains(response,"errcode")) {
                    logger.error("微信生成小程序码重试异常"+response);
                    JSONObject data2 = JSONObject.parseObject(response);
                    // 保存到微信异常表
                    wxExceptionDispose(data2, "微信小程序重试生成小程序码异常");
                } else {
                    try {
                        return CrmebUtil.getBase64Image(Base64.encodeBase64String(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new CrmebException("微信小程序码转换Base64异常");
                    }
                }
            }
            throw new CrmebException("微信生成二维码异常");
        }
        try {
            return CrmebUtil.getBase64Image(Base64.encodeBase64String(bytes));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrmebException("微信小程序码转换Base64异常");
        }
    }

    /**
     * 微信预下单接口(统一下单)
     * @param unifiedorderVo 预下单请求对象
     * @return 微信预下单返回对象
     */
    @Override
    public CreateOrderResponseVo payUnifiedorder(CreateOrderRequestVo unifiedorderVo) {
        try {
            String url = PayConstants.WX_PAY_API_URL + PayConstants.WX_PAY_API_URI;
            String request = XmlUtil.objectToXml(unifiedorderVo);
            String xml = restTemplateUtil.postXml(url, request);
            HashMap<String, Object> map = XmlUtil.xmlToMap(xml);
            if (null == map) {
                throw new CrmebException("微信下单失败！");
            }
            // 保存微信预下单
            WechatPayInfo wechatPayInfo = createWechatPayInfo(unifiedorderVo);

            CreateOrderResponseVo responseVo = CrmebUtil.mapToObj(map, CreateOrderResponseVo.class);
            if ("FAIL".equals(responseVo.getReturnCode().toUpperCase())) {
                // 保存到微信异常表
                wxPayExceptionDispose(map, "微信支付预下单异常");
                wechatPayInfo.setErrCode(map.get("return_code").toString());
                wechatPayInfoService.save(wechatPayInfo);
                throw new CrmebException("微信下单失败1！" +  responseVo.getReturnMsg());
            }

            if ("FAIL".equals(responseVo.getResultCode().toUpperCase())) {
                wxPayExceptionDispose(map, "微信支付预下单业务异常");
                wechatPayInfo.setErrCode(map.get("err_code").toString());
                wechatPayInfoService.save(wechatPayInfo);
                throw new CrmebException("微信下单失败2！" + responseVo.getErrCodeDes());
            }
            wechatPayInfo.setErrCode("200");
            wechatPayInfo.setPrepayId(responseVo.getPrepayId());
            wechatPayInfoService.save(wechatPayInfo);
            responseVo.setExtra(unifiedorderVo.getScene_info());
            return responseVo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrmebException(e.getMessage());
        }
    }

    /**
     * 生成微信订单表对象
     * @param vo 预下单数据
     * @return WechatPayInfo
     */
    private WechatPayInfo createWechatPayInfo(CreateOrderRequestVo vo) {
        WechatPayInfo payInfo = new WechatPayInfo();
        payInfo.setAppId(vo.getAppid());
        payInfo.setMchId(vo.getMch_id());
        payInfo.setDeviceInfo(vo.getDevice_info());
        payInfo.setOpenId(vo.getOpenid());
        payInfo.setNonceStr(vo.getNonce_str());
        payInfo.setSign(vo.getSign());
        payInfo.setSignType(vo.getSign_type());
        payInfo.setBody(vo.getBody());
        payInfo.setDetail(vo.getDetail());
        payInfo.setAttach(vo.getAttach());
        payInfo.setOutTradeNo(vo.getOut_trade_no());
        payInfo.setFeeType(vo.getFee_type());
        payInfo.setTotalFee(vo.getTotal_fee());
        payInfo.setSpbillCreateIp(vo.getSpbill_create_ip());
        payInfo.setTimeStart(vo.getTime_start());
        payInfo.setTimeExpire(vo.getTime_expire());
        payInfo.setNotifyUrl(vo.getNotify_url());
        payInfo.setTradeType(vo.getTrade_type());
        payInfo.setProductId(vo.getProduct_id());
        payInfo.setSceneInfo(vo.getScene_info());
        return payInfo;
    }

    /**
     * 微信支付查询订单
     * @return 支付订单查询结果
     */
    @Override
    public MyRecord payOrderQuery(Map<String, String> payVo) {
        String url = PayConstants.WX_PAY_API_URL + PayConstants.WX_PAY_ORDER_QUERY_API_URI;
        try {
            String request = XmlUtil.mapToXml(payVo);
            String xml = restTemplateUtil.postXml(url, request);
            HashMap<String, Object> map = XmlUtil.xmlToMap(xml);
            MyRecord record = new MyRecord();
            if (null == map) {
                throw new CrmebException("微信订单查询失败！");
            }
            record.setColums(map);
            if ("FAIL".equals(record.getStr("return_code").toUpperCase())) {
                wxPayQueryExceptionDispose(record, "微信支付查询订单通信异常");
                throw new CrmebException("微信订单查询失败1！" +  record.getStr("return_msg"));
            }

            if ("FAIL".equals(record.getStr("result_code").toUpperCase())) {
                wxPayQueryExceptionDispose(record, "微信支付查询订单结果异常");
                throw new CrmebException("微信订单查询失败2！" + record.getStr("err_code") + record.getStr("err_code_des"));
            }
            if (!"SUCCESS".equals(record.getStr("trade_state").toUpperCase())) {
                wxPayQueryExceptionDispose(record, "微信支付查询订单状态异常");
                throw new CrmebException("微信订单支付失败3！" + record.getStr("trade_state"));
            }

            return record;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrmebException("查询微信订单mapToXml异常===》" + e.getMessage());
        }
    }

    /**
     * 微信公众号发送模板消息
     * @param templateMessage 模板消息对象
     * @return 是否发送成功
     */
    @Override
    public Boolean sendPublicTemplateMessage(TemplateMessageVo templateMessage) {
        String accessToken = getPublicAccessToken();
        String url = StrUtil.format(WeChatConstants.WECHAT_PUBLIC_SEND_TEMPLATE_URL, accessToken);
        JSONObject jsonData = JSONObject.parseObject(JSONObject.toJSONString(templateMessage));
        String result = restTemplateUtil.postJsonData(url, jsonData);
        JSONObject data = JSONObject.parseObject(result);
        if (ObjectUtil.isNull(data)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (data.containsKey("errcode") && !"0".equals(data.getString("errcode"))) {
            if (data.containsKey("errmsg")) {
                wxExceptionDispose(data, "微信公众号发送模板消息异常");
                throw new CrmebException("微信接口调用失败：" + data.getString("errcode") + data.getString("errmsg"));
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 微信小程序发送订阅消息
     * @param templateMessage 消息对象
     * @return 是否发送成功
     */
    @Override
    public Boolean sendMiniSubscribeMessage(TemplateMessageVo templateMessage) {
        String accessToken = getMiniAccessToken();
        String url = StrUtil.format(WeChatConstants.WECHAT_MINI_SEND_SUBSCRIBE_URL, accessToken);
        JSONObject messAge = JSONObject.parseObject(JSONObject.toJSONString(templateMessage));
        String result = restTemplateUtil.postJsonData(url, messAge);
        JSONObject data = JSONObject.parseObject(result);
        if (ObjectUtil.isNull(data)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (data.containsKey("errcode") && !"0".equals(data.getString("errcode"))) {
            if ("40001".equals(data.getString("errcode"))) {
                wxExceptionDispose(data, "微信小程序发送订阅消息异常");
                redisUtil.delete(WeChatConstants.REDIS_WECAHT_MINI_ACCESS_TOKEN_KEY);
                accessToken = getMiniAccessToken();
                url = StrUtil.format(WeChatConstants.WECHAT_MINI_SEND_SUBSCRIBE_URL, accessToken);
                result = restTemplateUtil.postJsonData(url, messAge);
                JSONObject data2 = JSONObject.parseObject(result);
                if (data2.containsKey("errcode") && !"0".equals(data2.getString("errcode"))) {
                    if (data2.containsKey("errmsg")) {
                        wxExceptionDispose(data2, "微信小程序发送订阅消息重试异常");
                        throw new CrmebException("微信接口调用失败：" + data2.getString("errcode") + data2.getString("errmsg"));
                    }
                } else {
                    return Boolean.TRUE;
                }
            }
            if (data.containsKey("errmsg")) {
                wxExceptionDispose(data, "微信小程序发送订阅消息异常");
                throw new CrmebException("微信接口调用失败：" + data.getString("errcode") + data.getString("errmsg"));
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 获取微信公众号自定义菜单配置
     * （使用本自定义菜单查询接口可以获取默认菜单和全部个性化菜单信息）
     * @return 公众号自定义菜单
     */
    @Override
    public JSONObject getPublicCustomMenu() {
        String accessToken = getPublicAccessToken();
        String url = StrUtil.format(WeChatConstants.WECHAT_PUBLIC_MENU_GET_URL, accessToken);
        JSONObject result = restTemplateUtil.getData(url);
        if (ObjectUtil.isNull(result)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (result.containsKey("errcode") && "0".equals(result.getString("errcode"))) {
            return result;
        }
        if (result.containsKey("errmsg")) {
            wxExceptionDispose(result, "微信公众号获取自定义菜单配置异常");
            throw new CrmebException("微信接口调用失败：" + result.getString("errcode") + result.getString("errmsg"));
        }
        return result;
    }

    /**
     * 创建微信自定义菜单
     * @param data 菜单json字符串
     * @return 创建结果
     */
    @Override
    public Boolean createPublicCustomMenu(String data) {
        String accessToken = getPublicAccessToken();
        String url = StrUtil.format(WeChatConstants.WECHAT_PUBLIC_MENU_CREATE_URL, accessToken);
        String result = restTemplateUtil.postJsonData(url, JSONObject.parseObject(data));
        logger.info("微信消息发送结果:" + result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (ObjectUtil.isNull(jsonObject)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (jsonObject.containsKey("errcode") && "0".equals(jsonObject.getString("errcode"))) {
            return Boolean.TRUE;
        }
        if (jsonObject.containsKey("errmsg")) {
            wxExceptionDispose(jsonObject, "微信公众号创建自定义菜单异常");
            throw new CrmebException("微信接口调用失败：" + jsonObject.getString("errcode") + jsonObject.getString("errmsg"));
        }
        return Boolean.TRUE;
    }

    /**
     * 删除微信自定义菜单
     * @return 删除结果
     */
    @Override
    public Boolean deletePublicCustomMenu() {
        String accessToken = getPublicAccessToken();
        String url = StrUtil.format(WeChatConstants.WECHAT_PUBLIC_MENU_DELETE_URL, accessToken);
        JSONObject result = restTemplateUtil.getData(url);
        if (ObjectUtil.isNull(result)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (result.containsKey("errcode") && "0".equals(result.getString("errcode"))) {
            return Boolean.TRUE;
        }
        if (result.containsKey("errmsg")) {
            wxExceptionDispose(result, "微信公众号删除自定义菜单异常");
            throw new CrmebException("微信接口调用失败：" + result.getString("errcode") + result.getString("errmsg"));
        }
        return Boolean.TRUE;
    }

    /**
     * 企业号上传其他类型永久素材
     * 获取url
     * @param type 素材类型:图片（image）、语音（voice）、视频（video），普通文件(file)
     */
    @Override
    public String qyapiAddMaterialUrl(String type) {
        String accessToken = getPublicAccessToken();
        return StrUtil.format(WeChatConstants.WECHAT_PUBLIC_QYAPI_ADD_MATERIAL_URL, type, accessToken);
    }

    /**
     * 微信申请退款
     * @param wxRefundVo 微信申请退款对象
     * @param path 商户p12证书绝对路径
     * @return 申请退款结果对象
     */
    @Override
    public WxRefundResponseVo payRefund(WxRefundVo wxRefundVo, String path) {
        String xmlStr = XmlUtil.objectToXml(wxRefundVo);
        String url = WeChatConstants.PAY_API_URL + WeChatConstants.PAY_REFUND_API_URI_WECHAT;
        HashMap<String, Object> map = CollUtil.newHashMap();
        String xml = "";
        try {
            xml = restTemplateUtil.postWXRefundXml(url, xmlStr, wxRefundVo.getMch_id(), path);
            map = XmlUtil.xmlToMap(xml);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrmebException("xmlToMap错误，xml = " + xml);
        }
        if (null == map) {
            throw new CrmebException("微信无信息返回，微信申请退款失败！");
        }

        WxRefundResponseVo responseVo = CrmebUtil.mapToObj(map, WxRefundResponseVo.class);
        if ("FAIL".equals(responseVo.getReturnCode().toUpperCase())) {
            wxPayExceptionDispose(map, "微信申请退款异常1");
            throw new CrmebException("微信申请退款失败1！" +  responseVo.getReturnMsg());
        }

        if ("FAIL".equals(responseVo.getResultCode().toUpperCase())) {
            wxPayExceptionDispose(map, "微信申请退款业务异常");
            throw new CrmebException("微信申请退款失败2！" + responseVo.getErrCodeDes());
        }
        System.out.println("================微信申请退款结束=========================");
        return responseVo;
    }

    /**
     * 获取我的公众号模板消息列表
     * @return List
     */
    @Override
    public List<PublicMyTemplateVo> getPublicMyTemplateList() {
        String accessToken = getPublicAccessToken();
        String url = StrUtil.format(WeChatConstants.WECHAT_PUBLIC_GET_ALL_PRIVATE_TEMPLATE_URL, accessToken);
        JSONObject jsonObject = restTemplateUtil.getData(url);
        if (ObjectUtil.isNull(jsonObject)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (jsonObject.containsKey("errcode") && !"0".equals(jsonObject.getString("errcode"))) {
            if (jsonObject.containsKey("errmsg")) {
                // 保存到微信异常表
                wxExceptionDispose(jsonObject, StrUtil.format("获取我的公众号模板消息列表异常"));
                throw new CrmebException("微信接口调用失败：" + jsonObject.getString("errcode") + jsonObject.getString("errmsg"));
            }
        }
        JSONArray templateList = jsonObject.getJSONArray("template_list");
        List<PublicMyTemplateVo> voList = templateList.toJavaList(PublicMyTemplateVo.class);
        return voList;
    }

    /**
     * 删除微信公众号模板消息
     * @param templateId 模板编号
     * @return Boolean
     */
    @Override
    public Boolean delPublicMyTemplate(String templateId) {
        String accessToken = getPublicAccessToken();
        String url = StrUtil.format(WeChatConstants.WECHAT_PUBLIC_DEL_PRIVATE_TEMPLATE_URL, accessToken);
        HashMap<String, String> map = new HashMap<>();
        map.put("template_id", templateId);
        JSONObject jsonData = JSONObject.parseObject(JSONObject.toJSONString(map));
        String result = restTemplateUtil.postJsonData(url, jsonData);
        JSONObject data = JSONObject.parseObject(result);
        if (ObjectUtil.isNull(data)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (data.containsKey("errcode") && !"0".equals(data.getString("errcode"))) {
            if (data.containsKey("errmsg")) {
                wxExceptionDispose(data, "删除微信公众号模板消息异常");
                throw new CrmebException("微信接口调用失败：" + data.getString("errcode") + data.getString("errmsg"));
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 添加公众号模板消息
     * @param templateIdShort 模板库中模板的编号，有“TM**”和“OPENTMTM**”等形式
     * @return 公众号模板编号（自己的）
     */
    @Override
    public String apiAddPublicTemplate(String templateIdShort) {
        String accessToken = getPublicAccessToken();
        String url = StrUtil.format(WeChatConstants.WECHAT_PUBLIC_API_ADD_TEMPLATE_URL, accessToken);
        HashMap<String, String> map = new HashMap<>();
        map.put("template_id_short", templateIdShort);
        JSONObject jsonData = JSONObject.parseObject(JSONObject.toJSONString(map));
        String result = restTemplateUtil.postJsonData(url, jsonData);
        JSONObject data = JSONObject.parseObject(result);
        if (ObjectUtil.isNull(data)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (data.containsKey("errcode") && !"0".equals(data.getString("errcode"))) {
            if (data.containsKey("errmsg")) {
                wxExceptionDispose(data, "添加公众号模板消息异常");
                throw new CrmebException("微信接口调用失败：" + data.getString("errcode") + data.getString("errmsg"));
            }
        }
        return data.getString("template_id");
    }

    /**
     * 获取当前帐号下的个人模板列表
     * @return List
     */
    @Override
    public List<RoutineMyTemplateVo> getRoutineMyTemplateList() {
        String accessToken = getMiniAccessToken();
        String url = StrUtil.format(WeChatConstants.WECHAT_MINI_GET_ALL_PRIVATE_TEMPLATE_URL, accessToken);
        JSONObject jsonObject = restTemplateUtil.getData(url);
        if (ObjectUtil.isNull(jsonObject)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (jsonObject.containsKey("errcode") && !"0".equals(jsonObject.getString("errcode"))) {
            if (jsonObject.containsKey("errmsg")) {
                // 保存到微信异常表
                wxExceptionDispose(jsonObject, StrUtil.format("获取小程序当前帐号下的个人模板列表异常"));
                throw new CrmebException("微信接口调用失败：" + jsonObject.getString("errcode") + jsonObject.getString("errmsg"));
            }
        }
        JSONArray templateList = jsonObject.getJSONArray("data");
        List<RoutineMyTemplateVo> voList = templateList.toJavaList(RoutineMyTemplateVo.class);
        return voList;
    }

    /**
     * 删除微信小程序订阅消息
     * @return Boolean
     */
    @Override
    public Boolean delRoutineMyTemplate(String priTmplId) {
        String accessToken = getMiniAccessToken();
        String url = StrUtil.format(WeChatConstants.WECHAT_MINI_DEL_PRIVATE_TEMPLATE_URL, accessToken);
        HashMap<String, String> map = new HashMap<>();
        map.put("priTmplId", priTmplId);
        JSONObject jsonData = JSONObject.parseObject(JSONObject.toJSONString(map));
        String result = restTemplateUtil.postJsonData(url, jsonData);
        JSONObject data = JSONObject.parseObject(result);
        if (ObjectUtil.isNull(data)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (data.containsKey("errcode") && !"0".equals(data.getString("errcode"))) {
            if (data.containsKey("errmsg")) {
                wxExceptionDispose(data, "删除微信小程序订阅消息异常");
                throw new CrmebException("微信接口调用失败：" + data.getString("errcode") + data.getString("errmsg"));
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 获取小程序平台上的标准模板
     * @param tempKey 模板编号
     * @return List
     */
    @Override
    public List<RoutineTemplateKeyVo> getRoutineTemplateByWechat(String tempKey) {
        String accessToken = getMiniAccessToken();
        String url = StrUtil.format(WeChatConstants.WECHAT_MINI_GET_TEMPLATE_URL, accessToken, tempKey);
        JSONObject jsonObject = restTemplateUtil.getData(url);
        if (ObjectUtil.isNull(jsonObject)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (jsonObject.containsKey("errcode") && !"0".equals(jsonObject.getString("errcode"))) {
            if (jsonObject.containsKey("errmsg")) {
                // 保存到微信异常表
                wxExceptionDispose(jsonObject, "获取小程序平台上的标准模板异常");
                throw new CrmebException("微信接口调用失败：" + jsonObject.getString("errcode") + jsonObject.getString("errmsg"));
            }
        }
        JSONArray templateList = jsonObject.getJSONArray("data");
        List<RoutineTemplateKeyVo> voList = templateList.toJavaList(RoutineTemplateKeyVo.class);
        return voList;
    }

    /**
     * 添加小程序订阅消息
     * @param tempKey 模板编号
     * @param kidList 小程序订阅消息模板kid数组
     * @return 小程序订阅消息模板编号（自己的）
     */
    @Override
    public String apiAddRoutineTemplate(String tempKey, List<Integer> kidList) {
        String accessToken = getMiniAccessToken();
        String url = StrUtil.format(WeChatConstants.WECHAT_MINI_API_ADD_TEMPLATE_URL, accessToken);
        HashMap<String, Object> map = new HashMap<>();
        map.put("tid", tempKey);
        map.put("kidList", kidList);
        map.put("sceneDesc", "接口添加");
        JSONObject jsonData = JSONObject.parseObject(JSONObject.toJSONString(map));
        String result = restTemplateUtil.postJsonData(url, jsonData);
        JSONObject data = JSONObject.parseObject(result);
        if (ObjectUtil.isNull(data)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (data.containsKey("errcode") && !"0".equals(data.getString("errcode"))) {
            if (data.containsKey("errmsg")) {
                wxExceptionDispose(data, "添加小程序订阅消息异常");
                throw new CrmebException("微信接口调用失败：" + data.getString("errcode") + data.getString("errmsg"));
            }
        }
        return data.getString("priTmplId");
    }

    /**
     * 获取JS-SDK的签名
     * @param nonceStr 随机字符串
     * @param ticket ticket
     * @param timestamp 时间戳
     * @param url url
     * @return 签名
     */
    private String getJsSDKSignature(String nonceStr, String ticket, Long timestamp, String url) {
        //注意这里参数名必须全部小写，且必须有序
        String paramString = StrUtil.format("jsapi_ticket={}&noncestr={}&timestamp={}&url={}", ticket, nonceStr, timestamp, url);
        return SecureUtil.sha1(paramString);
    }

    /**
     * 获取JS-SDK的ticket
     * 用于计算签名
     * @return ticket
     */
    private String getJsApiTicket() {
        boolean exists = redisUtil.exists(WeChatConstants.REDIS_PUBLIC_JS_API_TICKET);
        if (exists) {
            Object ticket = redisUtil.get(WeChatConstants.REDIS_PUBLIC_JS_API_TICKET);
            return ticket.toString();
        }
        String accessToken = getPublicAccessToken();
        String url = StrUtil.format(WeChatConstants.WECHAT_PUBLIC_JS_TICKET_URL, accessToken);
        JSONObject data = restTemplateUtil.getData(url);
        if (ObjectUtil.isNull(data)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (data.containsKey("errcode") && !"0".equals(data.getString("errcode"))) {
            if (data.containsKey("errmsg")) {
                // 保存到微信异常表
                wxExceptionDispose(data, "微信获取JS-SDK的ticket异常");
                throw new CrmebException("微信接口调用失败：" + data.getString("errcode") + data.getString("errmsg"));
            }
        }
        String ticket = data.getString("ticket");
        redisUtil.set(WeChatConstants.REDIS_PUBLIC_JS_API_TICKET, ticket, WeChatConstants.REDIS_PUBLIC_JS_API_TICKET_EXPRESS, TimeUnit.SECONDS);
        return ticket;
    }

    /**
     * 获取微信accessToken
     * @param appId appId
     * @param secret secret
     * @param type mini-小程序，public-公众号，app-app
     * @return WeChatAccessTokenVo
     */
    private WeChatAccessTokenVo getAccessToken(String appId, String secret, String type) {
        String url = StrUtil.format(WeChatConstants.WECHAT_ACCESS_TOKEN_URL, appId, secret);
        JSONObject data = restTemplateUtil.getData(url);
        if (ObjectUtil.isNull(data)) {
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if (data.containsKey("errcode") && !"0".equals(data.getString("errcode"))) {
            if (data.containsKey("errmsg")) {
                // 保存到微信异常表
                wxExceptionDispose(data, StrUtil.format("微信获取accessToken异常，{}端", type));
                throw new CrmebException("微信接口调用失败：" + data.getString("errcode") + data.getString("errmsg"));
            }
        }
        return JSONObject.parseObject(data.toJSONString(), WeChatAccessTokenVo.class);
    }

    /**
     * 微信异常处理
     * @param jsonObject 微信返回数据
     * @param remark 备注
     */
    private void wxExceptionDispose(JSONObject jsonObject, String remark) {
        WechatExceptions wechatExceptions = new WechatExceptions();
        wechatExceptions.setErrcode(jsonObject.getString("errcode"));
        wechatExceptions.setErrmsg(StrUtil.isNotBlank(jsonObject.getString("errmsg")) ? jsonObject.getString("errmsg") : "");
        wechatExceptions.setData(jsonObject.toJSONString());
        wechatExceptions.setRemark(remark);
        wechatExceptions.setCreateTime(DateUtil.date());
        wechatExceptions.setUpdateTime(DateUtil.date());
        wechatExceptionsService.save(wechatExceptions);
    }

    /**
     * 微信支付异常处理
     * @param map 微信返回数据
     * @param remark 备注
     */
    private void wxPayExceptionDispose(HashMap<String, Object> map, String remark) {
        WechatExceptions wechatExceptions = new WechatExceptions();
        String returnCode = (String) map.get("return_code");
        if ("FAIL".equals(returnCode.toUpperCase())) {
            wechatExceptions.setErrcode("-100");
            wechatExceptions.setErrmsg(map.get("return_msg").toString());
        } else {
            wechatExceptions.setErrcode(map.get("err_code").toString());
            wechatExceptions.setErrmsg(map.get("err_code_des").toString());
        }
        wechatExceptions.setData(JSONObject.toJSONString(map));
        wechatExceptions.setRemark(remark);
        wechatExceptions.setCreateTime(DateUtil.date());
        wechatExceptions.setUpdateTime(DateUtil.date());
        wechatExceptionsService.save(wechatExceptions);
    }

    /**
     * 微信支付查询异常处理
     * @param record 微信返回数据
     * @param remark 备注
     */
    private void wxPayQueryExceptionDispose(MyRecord record, String remark) {
        WechatExceptions wechatExceptions = new WechatExceptions();
        if ("FAIL".equals(record.getStr("return_code").toUpperCase())) {
            wechatExceptions.setErrcode("-200");
            wechatExceptions.setErrmsg(record.getStr("return_msg"));
        } else if ("FAIL".equals(record.getStr("result_code").toUpperCase())) {
            wechatExceptions.setErrcode(record.getStr("err_code"));
            wechatExceptions.setErrmsg(record.getStr("err_code_des"));
        } else if (!"SUCCESS".equals(record.getStr("trade_state").toUpperCase())) {
            wechatExceptions.setErrcode("-201");
            wechatExceptions.setErrmsg(record.getStr("trade_state"));
        }
        wechatExceptions.setData(JSONObject.toJSONString(record.getColumns()));
        wechatExceptions.setRemark(remark);
        wechatExceptions.setCreateTime(DateUtil.date());
        wechatExceptions.setUpdateTime(DateUtil.date());
        wechatExceptionsService.save(wechatExceptions);
    }

}

