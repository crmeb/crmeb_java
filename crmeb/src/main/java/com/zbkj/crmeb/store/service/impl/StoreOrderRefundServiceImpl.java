package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.constants.Constants;
import com.constants.WeChatConstants;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.RestTemplateUtil;
import com.utils.XmlUtil;
import com.zbkj.crmeb.payment.vo.wechat.WxRefundResponseVo;
import com.zbkj.crmeb.payment.vo.wechat.WxRefundVo;
import com.zbkj.crmeb.store.dao.StoreOrderDao;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.request.StoreOrderRefundRequest;
import com.zbkj.crmeb.store.service.StoreOrderRefundService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * StoreOrderServiceImpl 接口实现
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
public class StoreOrderRefundServiceImpl extends ServiceImpl<StoreOrderDao, StoreOrder> implements StoreOrderRefundService {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    /**
     * 退款 需要优化
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @Async
    @Override
    public void refund(StoreOrderRefundRequest request, StoreOrder storeOrder) {
        //开始处理
        if(storeOrder.getIsChannel() == Constants.ORDER_PAY_CHANNEL_PUBLIC){
            //公众号
            refundJSAPI(request, storeOrder);
        }else{
            //小程序
            refundMiniWx(request, storeOrder);
        }

    }

    /**
     * 小程序退款
     * @param request
     * @param storeOrder
     */
    private void refundMiniWx(StoreOrderRefundRequest request, StoreOrder storeOrder) {
        WxRefundVo wxRefundVo = new WxRefundVo();

        String appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
        String mchId = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
        wxRefundVo.setAppid(appId);
        wxRefundVo.setMch_id(mchId);
        wxRefundVo.setNonce_str(DigestUtils.md5Hex(CrmebUtil.getUuid() + CrmebUtil.randomCount(111111, 666666)));
        wxRefundVo.setOut_trade_no(storeOrder.getOrderId());
        wxRefundVo.setOut_refund_no(storeOrder.getOrderId());
        wxRefundVo.setTotal_fee(storeOrder.getPayPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        wxRefundVo.setRefund_fee(request.getAmount().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        String signKey = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
        String sign = CrmebUtil.getSign(CrmebUtil.objectToMap(wxRefundVo), signKey);
        wxRefundVo.setSign(sign);
        String path = systemConfigService.getValueByKeyException("pay_mini_client_p12");
        commonRefound(wxRefundVo, path);
    }

    /**
     * 公共号退款
     * @param request
     * @param storeOrder
     */
    private void refundJSAPI(StoreOrderRefundRequest request, StoreOrder storeOrder) {
        WxRefundVo wxRefundVo = new WxRefundVo();

        String appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
        String mchId = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
        wxRefundVo.setAppid(appId);
        wxRefundVo.setMch_id(mchId);
        wxRefundVo.setNonce_str(DigestUtils.md5Hex(CrmebUtil.getUuid() + CrmebUtil.randomCount(111111, 666666)));
        wxRefundVo.setOut_trade_no(storeOrder.getOrderId());
        wxRefundVo.setOut_refund_no(storeOrder.getOrderId());
        wxRefundVo.setTotal_fee(storeOrder.getPayPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        wxRefundVo.setRefund_fee(request.getAmount().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        String signKey = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
        String sign = CrmebUtil.getSign(CrmebUtil.objectToMap(wxRefundVo), signKey);
        wxRefundVo.setSign(sign);
        String path = systemConfigService.getValueByKeyException("pay_routine_client_p12");
        commonRefound(wxRefundVo, path);
    }

    /**
     * 公共退款部分
     * @param wxRefundVo
     * @return
     */
    private WxRefundResponseVo commonRefound(WxRefundVo wxRefundVo, String path) {
        String xmlStr = XmlUtil.objectToXml(wxRefundVo);
        String url = WeChatConstants.PAY_API_URL + WeChatConstants.PAY_REFUND_API_URI_WECHAT;
        HashMap<String, Object> map = CollUtil.newHashMap();
        String xml = "";
        System.out.println("微信申请退款xmlStr = " + xmlStr);
        try {
            xml = restTemplateUtil.postWXRefundXml(url, xmlStr, wxRefundVo.getMch_id(), path);
            map = XmlUtil.xmlToMap(xml);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrmebException("xmlToMap错误，xml = " + xml);
        }
        if(null == map){
            throw new CrmebException("微信退款失败！");
        }

        WxRefundResponseVo responseVo = CrmebUtil.mapToObj(map, WxRefundResponseVo.class);
        if(responseVo.getReturnCode().toUpperCase().equals("FAIL")){
            throw new CrmebException("微信退款失败1！" +  responseVo.getReturnMsg());
        }

        if(responseVo.getResultCode().toUpperCase().equals("FAIL")){
            throw new CrmebException("微信退款失败2！" + responseVo.getErrCodeDes());
        }
        System.out.println("================微信申请退款结束=========================");
        System.out.println("xml = " + xml);
        return responseVo;
    }
}

