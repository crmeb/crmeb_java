package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * @Classname WechatSendMessageForPaySuccess
 * @Description 付款成功通知
 * @Date 9/10/20 12:09 下午
 * @Created by stivepeim
 */
@Data
public class WechatSendMessageForPaySuccess {
    public WechatSendMessageForPaySuccess(String fuKuanDanHao, String fuKuanJinE, String fuKuanShiJian, String wenXinTiShi, String dingDanJinE, String shangPinQingDan) {
        FuKuanDanHao = fuKuanDanHao;
        FuKuanJinE = fuKuanJinE;
        FuKuanShiJian = fuKuanShiJian;
        WenXinTiShi = wenXinTiShi;
        DingDanJinE = dingDanJinE;
        ShangPinQingDan = shangPinQingDan;
    }

    private String FuKuanDanHao;
    private String FuKuanJinE;
    private String FuKuanShiJian;
    private String WenXinTiShi;
    private String DingDanJinE;
    private String ShangPinQingDan;
}
