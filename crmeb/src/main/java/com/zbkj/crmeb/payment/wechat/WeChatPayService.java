package com.zbkj.crmeb.payment.wechat;

import com.zbkj.crmeb.payment.vo.wechat.CreateOrderResponseVo;
import com.zbkj.crmeb.payment.vo.wechat.PayParamsVo;

/**
 * <p>
 * 微信支付
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-10
 */
public interface WeChatPayService {
    CreateOrderResponseVo create(PayParamsVo payParamsVo);
}
