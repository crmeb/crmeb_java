package com.zbkj.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.zbkj.common.constants.AlipayConfig;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.PayConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.finance.UserRecharge;
import com.zbkj.common.utils.WxPayUtil;
import com.zbkj.common.vo.WxRefundVo;
import com.zbkj.service.service.RechargeRefundService;
import com.zbkj.service.service.SystemConfigService;
import com.zbkj.service.service.WechatNewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 充值支付渠道退款服务实现
 */
@Service
public class RechargeRefundServiceImpl implements RechargeRefundService {

    private static final Logger logger = LoggerFactory.getLogger(RechargeRefundServiceImpl.class);

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private WechatNewService wechatNewService;

    @Override
    public void refund(UserRecharge userRecharge) {
        String rechargeType = userRecharge.getRechargeType();
        if (isWechatChannel(rechargeType)) {
            refundWechat(userRecharge);
            return;
        }
        if (PayConstants.PAY_CHANNEL_ALI_PAY.equals(rechargeType)
                || PayConstants.PAY_CHANNEL_ALI_APP_PAY.equals(rechargeType)) {
            refundAlipay(userRecharge);
            return;
        }
        throw new CrmebException("当前充值类型不支持原路退款");
    }

    private void refundWechat(UserRecharge userRecharge) {
        String appId;
        String mchId;
        String signKey;
        String certificatePath;
        String rechargeType = userRecharge.getRechargeType();

        if (PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM.equals(rechargeType)) {
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY);
            certificatePath = systemConfigService.getValueByKeyException("pay_routine_certificate_path");
        } else if (PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS.equals(rechargeType)
                || PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID.equals(rechargeType)) {
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_APP_KEY);
            certificatePath = systemConfigService.getValueByKeyException("pay_weixin_app_certificate_path");
        } else {
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
            certificatePath = systemConfigService.getValueByKeyException("pay_weixin_certificate_path");
        }

        WxRefundVo refundVo = new WxRefundVo();
        refundVo.setAppid(appId);
        refundVo.setMch_id(mchId);
        refundVo.setNonce_str(WxPayUtil.getNonceStr());
        refundVo.setOut_trade_no(userRecharge.getOrderId());
        refundVo.setOut_refund_no(userRecharge.getOrderId());
        refundVo.setTotal_fee(toCents(userRecharge.getPrice()));
        refundVo.setRefund_fee(toCents(userRecharge.getPrice()));
        refundVo.setSign(WxPayUtil.getSign(refundVo, signKey));

        wechatNewService.payRefund(refundVo, certificatePath);
    }

    private void refundAlipay(UserRecharge userRecharge) {
        String appId = systemConfigService.getValueByKeyException(AlipayConfig.APPID);
        String privateKey = systemConfigService.getValueByKeyException(AlipayConfig.RSA_PRIVATE_KEY);
        String publicKey = systemConfigService.getValueByKeyException(AlipayConfig.ALIPAY_PUBLIC_KEY);
        AlipayClient client = new DefaultAlipayClient(
                AlipayConfig.URL,
                appId,
                privateKey,
                AlipayConfig.FORMAT,
                AlipayConfig.CHARSET,
                publicKey,
                AlipayConfig.SIGNTYPE
        );

        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(userRecharge.getOrderId());
        model.setRefundAmount(userRecharge.getPrice().toPlainString());
        model.setRefundReason("后台充值退款");
        model.setOutRequestNo(userRecharge.getOrderId());

        AlipayTradeRefundRequest refundRequest = new AlipayTradeRefundRequest();
        refundRequest.setBizModel(model);

        AlipayTradeRefundResponse response;
        try {
            response = client.execute(refundRequest);
        } catch (AlipayApiException e) {
            logger.error("支付宝充值退款申请异常，orderId={}", userRecharge.getOrderId(), e);
            throw new CrmebException("支付宝充值退款申请异常");
        }
        if (response == null || !response.isSuccess()) {
            String errorMessage = response == null ? "支付宝无返回信息" : response.getSubMsg();
            if (StrUtil.isBlank(errorMessage) && response != null) {
                errorMessage = response.getMsg();
            }
            if (StrUtil.isBlank(errorMessage)) {
                errorMessage = "未知错误";
            }
            logger.error("支付宝充值退款失败，orderId={}，response={}", userRecharge.getOrderId(),
                    response == null ? null : response.getBody());
            throw new CrmebException("支付宝充值退款失败：" + errorMessage);
        }
        logger.info("支付宝充值退款成功，orderId={}，response={}", userRecharge.getOrderId(), response.getBody());
    }

    private boolean isWechatChannel(String rechargeType) {
        return PayConstants.PAY_CHANNEL_WE_CHAT_PUBLIC.equals(rechargeType)
                || PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM.equals(rechargeType)
                || PayConstants.PAY_CHANNEL_WE_CHAT_H5.equals(rechargeType)
                || PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS.equals(rechargeType)
                || PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID.equals(rechargeType);
    }

    private int toCents(BigDecimal amount) {
        try {
            return amount.movePointRight(2).intValueExact();
        } catch (ArithmeticException e) {
            throw new CrmebException("退款金额格式不正确");
        }
    }
}
