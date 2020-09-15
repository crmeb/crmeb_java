package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * @Classname WechatSendMessageForCreateOrder
 * @Description 新订单提醒
 * @Date 9/10/20 12:24 下午
 * @Created by stivepeim
 */
@Data
public class WechatSendMessageForCreateOrder {
    public WechatSendMessageForCreateOrder(String dingDanLeiXing, String dingDanShangPin, String dingDanJinE, String dingDanBianHao, String maiJiaXinXi, String dingDanShiJian, String wenXinTiShi, String tiHuoDiZhi, String zhiFuFangShi, String shouHuoDiZHi) {
        DingDanLeiXing = dingDanLeiXing;
        DingDanShangPin = dingDanShangPin;
        DingDanJinE = dingDanJinE;
        DingDanBianHao = dingDanBianHao;
        MaiJiaXinXi = maiJiaXinXi;
        DingDanShiJian = dingDanShiJian;
        WenXinTiShi = wenXinTiShi;
        TiHuoDiZhi = tiHuoDiZhi;
        ZhiFuFangShi = zhiFuFangShi;
        ShouHuoDiZHi = shouHuoDiZHi;
    }

    private String DingDanLeiXing;
    private String DingDanShangPin;
    private String DingDanJinE;
    private String DingDanBianHao;
    private String MaiJiaXinXi;
    private String DingDanShiJian;
    private String WenXinTiShi;
    private String TiHuoDiZhi;
    private String ZhiFuFangShi;
    private String ShouHuoDiZHi;
}
