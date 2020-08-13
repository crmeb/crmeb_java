package com.zbkj.crmeb.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.XmlUtil;
import com.zbkj.crmeb.payment.service.CallbackService;
import com.zbkj.crmeb.payment.service.OrderPayService;
import com.zbkj.crmeb.payment.service.RechargePayService;
import com.zbkj.crmeb.payment.vo.wechat.AttachVo;
import com.zbkj.crmeb.payment.vo.wechat.CallbackVo;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.service.UserTokenService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;


/**
 * <p>
 * 支付类
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-10
 */
@EqualsAndHashCode()
@Data
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

    /**
     * 微信支付回调
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public boolean weChat(String request) {
        try{
            if(null == request){
                return false;
            }

            HashMap<String, Object> map = XmlUtil.xmlToMap(request);
            if(null == map){
                throw new CrmebException("微信下单失败！");
            }

            //解析xml
            CallbackVo callbackVo = CrmebUtil.mapToObj(map, CallbackVo.class);
            AttachVo attachVo = JSONObject.toJavaObject(JSONObject.parseObject(callbackVo.getAttach()), AttachVo.class);

            //判断openid
            UserToken userToken = userTokenService.getUserIdByOpenId(callbackVo.getOpenid());
            if(null == userToken || !userToken.getUid().equals(attachVo.getUserId())){
                //用户信息错误
                throw new CrmebException("用户信息错误！");
            }

            //根据类型判断是订单或者充值
            switch (attachVo.getType()){
                case Constants.SERVICE_PAY_TYPE_ORDER:
                    return orderPayService.success(callbackVo.getOutTradeNo(), attachVo.getUserId(), Constants.PAY_TYPE_WE_CHAT);
                case Constants.SERVICE_PAY_TYPE_RECHARGE:
                    return rechargePayService.success(callbackVo.getOutTradeNo(), attachVo.getUserId(), Constants.PAY_TYPE_WE_CHAT);
                default:
                    throw new CrmebException("充值模块错误！");
            }

        }catch (Exception e){
            return false;
        }
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
