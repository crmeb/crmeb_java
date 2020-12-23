package com.zbkj.crmeb.payment.wechat.impl;

import com.alibaba.fastjson.JSONObject;
import com.constants.Constants;
import com.constants.WeChatConstants;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.RestTemplateUtil;
import com.utils.XmlUtil;
import com.zbkj.crmeb.payment.vo.wechat.*;
import com.zbkj.crmeb.payment.wechat.WeChatPayService;
import com.zbkj.crmeb.store.service.StoreOrderInfoService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.service.UserTokenService;
import com.zbkj.crmeb.wechat.service.WeChatService;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;


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
}
