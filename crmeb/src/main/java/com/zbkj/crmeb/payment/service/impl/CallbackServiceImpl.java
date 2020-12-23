package com.zbkj.crmeb.payment.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.WxPayUtil;
import com.zbkj.crmeb.finance.model.UserRecharge;
import com.zbkj.crmeb.finance.service.UserRechargeService;
import com.zbkj.crmeb.payment.service.CallbackService;
import com.zbkj.crmeb.payment.service.OrderPayService;
import com.zbkj.crmeb.payment.service.RechargePayService;
import com.zbkj.crmeb.payment.vo.wechat.AttachVo;
import com.zbkj.crmeb.payment.vo.wechat.CallbackVo;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.user.service.UserTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;


/**
 * 订单支付回调 CallbackService 实现类
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
public class CallbackServiceImpl implements CallbackService {

    private static final Logger logger = LoggerFactory.getLogger(CallbackServiceImpl.class);

    @Autowired
    private RechargePayService rechargePayService;

    @Lazy
    @Autowired
    private OrderPayService orderPayService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRechargeService userRechargeService;

    /**
     * 微信支付回调
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @Override
    public String weChat(String xmlInfo) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        if(StrUtil.isBlank("xmlInfo")){
            sb.append("<return_code><![CDATA[FAIL]]></return_code>");
            sb.append("<return_msg><![CDATA[xmlInfo is blank]]></return_msg>");
            sb.append("</xml>");
            logger.error("wechat callback error : " + sb.toString());
            return sb.toString();
        }

        try{
//            HashMap<String, Object> map = XmlUtil.xmlToMap(xmlInfo);
            HashMap<String, Object> map = WxPayUtil.processResponseXml(xmlInfo);
            // 通信是否成功
            String returnCode = (String) map.get("return_code");
            if (!returnCode.equals(Constants.SUCCESS)) {
                sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
                sb.append("<return_msg><![CDATA[OK]]></return_msg>");
                sb.append("</xml>");
                logger.error("wechat callback error : wx pay return code is fail returnMsg : " + map.get("return_msg"));
                return sb.toString();
            }
            // 交易是否成功
            String resultCode = (String) map.get("result_code");
            if (!resultCode.equals(Constants.SUCCESS)) {
                sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
                sb.append("<return_msg><![CDATA[OK]]></return_msg>");
                sb.append("</xml>");
                logger.error("wechat callback error : wx pay result code is fail");
                return sb.toString();
            }

            //解析xml
            CallbackVo callbackVo = CrmebUtil.mapToObj(map, CallbackVo.class);
            AttachVo attachVo = JSONObject.toJavaObject(JSONObject.parseObject(callbackVo.getAttach()), AttachVo.class);

            //判断openid
            UserToken userToken = userTokenService.getByOpenid(callbackVo.getOpenid());
            if(null == userToken || !userToken.getUid().equals(attachVo.getUserId())){
                //用户信息错误
                throw new CrmebException("用户信息错误！");
            }
            User user = userService.getById(attachVo.getUserId());
            if (ObjectUtil.isNull(user)) {
                //用户信息错误
                throw new CrmebException("用户信息错误！");
            }

            //根据类型判断是订单或者充值
            if (!Constants.SERVICE_PAY_TYPE_ORDER.equals(attachVo.getType()) && !Constants.SERVICE_PAY_TYPE_RECHARGE.equals(attachVo.getType())) {
                logger.error("wechat pay err : 未知的支付类型==》" + callbackVo.getOutTradeNo());
                throw new CrmebException("未知的支付类型！");
            }
            // 订单
            if (Constants.SERVICE_PAY_TYPE_ORDER.equals(attachVo.getType())) {
                StoreOrder storeOrder = new StoreOrder();
                storeOrder.setOrderId(callbackVo.getOutTradeNo());
                storeOrder.setUid(attachVo.getUserId());

                storeOrder = storeOrderService.getInfoByEntity(storeOrder);
                if (ObjectUtil.isNull(storeOrder)) {
                    logger.error("wechat pay error : 订单信息不存在==》" + callbackVo.getOutTradeNo());
                    throw new CrmebException("wechat pay error : 订单信息不存在==》" + callbackVo.getOutTradeNo());
                }
                if (storeOrder.getPaid()) {
                    logger.error("wechat pay error : 订单已处理==》" + callbackVo.getOutTradeNo());
                    sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
                    sb.append("<return_msg><![CDATA[OK]]></return_msg>");
                    sb.append("</xml>");
                    return sb.toString();
                }
                // 支付成功处理
                Boolean payAfter = orderPayService.paySuccess(storeOrder, user, userToken);
                if (!payAfter) {
                    logger.error("wechat pay error : 数据保存失败==》" + callbackVo.getOutTradeNo());
                    throw new CrmebException("wechat pay error : 数据保存失败==》" + callbackVo.getOutTradeNo());
                }
            }
            // 充值
            if (Constants.SERVICE_PAY_TYPE_RECHARGE.equals(attachVo.getType())) {
                UserRecharge userRecharge = new UserRecharge();
                userRecharge.setOrderId(callbackVo.getOutTradeNo());
                userRecharge.setUid(attachVo.getUserId());
                userRecharge = userRechargeService.getInfoByEntity(userRecharge);
                if(ObjectUtil.isNull(userRecharge)){
                    throw new CrmebException("没有找到订单信息");
                }
                if(userRecharge.getPaid()){
                    sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
                    sb.append("<return_msg><![CDATA[OK]]></return_msg>");
                    sb.append("</xml>");
                    return sb.toString();
                }
                // 支付成功处理
                Boolean rechargePayAfter = rechargePayService.paySuccess(userRecharge, userToken);
                if (!rechargePayAfter) {
                    logger.error("wechat pay error : 数据保存失败==》" + callbackVo.getOutTradeNo());
                    throw new CrmebException("wechat pay error : 数据保存失败==》" + callbackVo.getOutTradeNo());
                }
            }
            sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
            sb.append("<return_msg><![CDATA[OK]]></return_msg>");
        }catch (Exception e){
            sb.append("<return_code><![CDATA[FAIL]]></return_code>");
            sb.append("<return_msg><![CDATA[").append(e.getMessage()).append("]]></return_msg>");
            logger.error("wechat pay error : 业务异常==》" + e.getMessage());
        }
        sb.append("</xml>");
        logger.error("wechat callback response : " + sb.toString());
        return sb.toString();
    }

    /**
     * 支付宝支付回调
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @Override
    public boolean aliPay(String request) {
        //根据类型判断是订单或者充值
        return false;
    }
}
