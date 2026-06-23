package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.zbkj.common.constants.AlipayConfig;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.PayConstants;
import com.zbkj.common.constants.TaskConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.combination.StoreCombination;
import com.zbkj.common.model.combination.StorePink;
import com.zbkj.common.model.finance.UserRecharge;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.StoreOrderRefundRequest;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 支付宝支付 Service impl
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class AliPayServiceImpl implements AliPayService {

    private static final Logger logger = LoggerFactory.getLogger(AliPayServiceImpl.class);

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private StorePinkService storePinkService;

    @Autowired
    private UserRechargeService userRechargeService;

    @Autowired
    private RechargePayService rechargePayService;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 查询支付结果
     * @param orderNo 订单编号
     * @return Boolean
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
            if (!storeOrder.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY)) {
                throw new CrmebException("不是微信支付类型订单，请重新选择支付方式");
            }
            if (storeOrder.getPaid()) {
                return Boolean.TRUE;
            }

            User user = userService.getById(storeOrder.getUid());
            if (ObjectUtil.isNull(user)) throw new CrmebException("用户不存在");

            // 商户订单号，商户网站订单系统中唯一订单号，必填
            String out_trade_no = orderNo;
            //支付宝交易号
            // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
            String aliPayAppid = systemConfigService.getValueByKey(AlipayConfig.APPID);
            String aliPayPrivateKey = systemConfigService.getValueByKey(AlipayConfig.RSA_PRIVATE_KEY);
            String aliPayPublicKey = systemConfigService.getValueByKey(AlipayConfig.ALIPAY_PUBLIC_KEY_2);
            AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);
            AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();

            AlipayTradeQueryModel model=new AlipayTradeQueryModel();
            model.setOutTradeNo(out_trade_no);
//            model.setTradeNo(trade_no);
            alipay_request.setBizModel(model);
            logger.info("alipay_request = " + alipay_request);

            AlipayTradeQueryResponse alipay_response = null;
            try {
                alipay_response = client.execute(alipay_request);
            } catch (AlipayApiException e) {
                e.printStackTrace();
                logger.error("支付宝支付查询异常，" + e.getMessage());
                throw new CrmebException("支付宝支付查询异常");
            }
            logger.info("alipay_response = ");
            if (!alipay_response.getTradeStatus().equals("TRADE_SUCCESS")) {
                logger.error("支付宝支付结果异常，tradeStatus = " + alipay_response.getTradeStatus());
                throw new CrmebException("支付宝支付结果异常");
            }

            Boolean execute = transactionTemplate.execute(e -> {
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
                    storeOrder.setUpdateTime(DateUtil.date());
                    storeOrderService.updateById(storeOrder);
                }
                return Boolean.TRUE;
            });

            if (!execute) {
                throw new CrmebException("支付成功更新订单失败");
            }
            // 添加支付成功task
            redisUtil.lPush(TaskConstants.ORDER_TASK_PAY_SUCCESS_AFTER, orderNo);
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
        // 商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = userRecharge.getOutTradeNo();
        //支付宝交易号
//            String trade_no = new String("1111");
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        String aliPayAppid = systemConfigService.getValueByKey(AlipayConfig.APPID);
        String aliPayPrivateKey = systemConfigService.getValueByKey(AlipayConfig.RSA_PRIVATE_KEY);
        String aliPayPublicKey = systemConfigService.getValueByKey(AlipayConfig.ALIPAY_PUBLIC_KEY_2);
        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);
        AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();

        AlipayTradeQueryModel model=new AlipayTradeQueryModel();
        model.setOutTradeNo(out_trade_no);
//            model.setTradeNo(trade_no);
        alipay_request.setBizModel(model);
        logger.info("alipay_request = " + alipay_request);

        AlipayTradeQueryResponse alipay_response = null;
        try {
            alipay_response = client.execute(alipay_request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("支付宝支付查询异常，" + e.getMessage());
            throw new CrmebException("支付宝支付查询异常");
        }
        logger.info("alipay_response = ");
        if (!alipay_response.getTradeStatus().equals("TRADE_SUCCESS")) {
            logger.error("支付宝支付结果异常，tradeStatus = " + alipay_response.getTradeStatus());
            throw new CrmebException("支付宝支付结果异常");
        }
        // 支付成功处理
        Boolean rechargePayAfter = rechargePayService.paySuccess(userRecharge);
        if (!rechargePayAfter) {
            throw new CrmebException("wechat pay error : 数据保存失败==》" + orderNo);
        }
        return rechargePayAfter;
    }

    /**
     * 支付宝退款
     * @param request 请求参数
     * @param storeOrder 订单
     */
    @Override
    public void refund(StoreOrderRefundRequest request, StoreOrder storeOrder) {

        //商户订单号和支付宝交易号不能同时为空。 trade_no、  out_trade_no如果同时存在优先取trade_no
        //商户订单号，和支付宝交易号二选一
        String out_trade_no = storeOrder.getOrderId();
        //支付宝交易号，和商户订单号二选一
//        String trade_no = new String(request.getParameter("WIDtrade_no").getBytes("ISO-8859-1"),"UTF-8");
        //退款金额，不能大于订单总金额
        String refund_amount = request.getAmount().toString();
        //退款的原因说明
        String refund_reason = storeOrder.getRefundReasonWapExplain();
        //标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
//        String  out_request_no=new String("");
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        String aliPayAppid = systemConfigService.getValueByKey(AlipayConfig.APPID);
        String aliPayPrivateKey = systemConfigService.getValueByKey(AlipayConfig.RSA_PRIVATE_KEY);
        String aliPayPublicKey = systemConfigService.getValueByKey(AlipayConfig.ALIPAY_PUBLIC_KEY);
        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);
        AlipayTradeRefundRequest alipay_request = new AlipayTradeRefundRequest();

        AlipayTradeRefundModel model=new AlipayTradeRefundModel();
        model.setOutTradeNo(out_trade_no);
//        model.setTradeNo(trade_no);
        model.setRefundAmount(refund_amount);
        model.setRefundReason(refund_reason);
//        model.setOutRequestNo(out_request_no);
        alipay_request.setBizModel(model);
        logger.info("alipay_request = " + alipay_request);

        AlipayTradeRefundResponse alipay_response = null;
        try {
            alipay_response = client.execute(alipay_request);
        } catch (AlipayApiException e) {
            logger.error("支付宝退款申请异常，alipay_response = " + alipay_response);
            logger.error(e.getMessage());
            throw new CrmebException("支付宝支付结果异常");
        }
        logger.info("alipay_response = " + alipay_response.getBody());
    }

    /**
     * 查询退款
     * @param orderNo 订单编号
     */
    @Override
    public Boolean queryRefund(String orderNo) {
        StoreOrder storeOrder = storeOrderService.getByOderId(orderNo);
        if (ObjectUtil.isNull(storeOrder)) {
            throw new CrmebException("订单不存在");
        }
        if (storeOrder.getIsDel()) {
            throw new CrmebException("订单已被删除");
        }
        if (!storeOrder.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY)) {
            throw new CrmebException("不是支付宝支付类型订单");
        }
        if (!storeOrder.getPaid()) {
            throw new CrmebException("订单未支付");
        }
        if (!storeOrder.getRefundStatus().equals(3)) {
            throw new CrmebException("订单已不在退款中状态");
        }

        //商户订单号和支付宝交易号不能同时为空。 trade_no、  out_trade_no如果同时存在优先取trade_no
        //商户订单号，和支付宝交易号二选一
        String out_trade_no = orderNo;
        //支付宝交易号，和商户订单号二选一
        //        String trade_no = new String(request.getParameter("WIDtrade_no").getBytes("ISO-8859-1"),"UTF-8");
        //请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号
        //        String out_request_no = new String(request.getParameter("WIDout_request_no").getBytes("ISO-8859-1"),"UTF-8");
        //        String out_request_no = new String("WIDout_request_no");
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        String aliPayAppid = systemConfigService.getValueByKey(AlipayConfig.APPID);
        String aliPayPrivateKey = systemConfigService.getValueByKey(AlipayConfig.RSA_PRIVATE_KEY);
        String aliPayPublicKey = systemConfigService.getValueByKey(AlipayConfig.ALIPAY_PUBLIC_KEY);
        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, aliPayAppid, aliPayPrivateKey, AlipayConfig.FORMAT, AlipayConfig.CHARSET, aliPayPublicKey, AlipayConfig.SIGNTYPE);

        AlipayTradeFastpayRefundQueryRequest alipay_request = new AlipayTradeFastpayRefundQueryRequest();

        AlipayTradeFastpayRefundQueryModel model=new AlipayTradeFastpayRefundQueryModel();
        model.setOutTradeNo(out_trade_no);
//        model.setTradeNo(trade_no);
//        model.setOutRequestNo(out_request_no);
        alipay_request.setBizModel(model);
        logger.info("alipay_request = " + alipay_request);
        AlipayTradeFastpayRefundQueryResponse alipay_response= null;
        try {
            alipay_response = client.execute(alipay_request);
        } catch (AlipayApiException e) {
            logger.error("支付宝退款查询异常，alipay_response = " + alipay_response);
            throw new CrmebException("支付宝退款查询异常");
        }
        logger.info("alipay_response = " + alipay_response.getBody());

        if (alipay_response.getRefundStatus().equals("REFUND_PROCESSING")) {
            throw new CrmebException("退款处理中");
        }
        if (alipay_response.getRefundStatus().equals("REFUND_FAIL")) {
            throw new CrmebException("支付宝退款失败");
        }

        storeOrder.setRefundStatus(2);
        storeOrder.setUpdateTime(DateUtil.date());
        boolean update = storeOrderService.updateById(storeOrder);
        if (update) {
            // 退款task
            redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, storeOrder.getId());
        } else {
            logger.warn("支付宝退款订单更新失败==>" + alipay_response);
        }
        return update;
    }
}
