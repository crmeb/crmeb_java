package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * @Classname WechatSendMessageForGetPackage
 * @Description 收货通知
 * @Date 9/10/20 2:07 下午
 * @Created by stivepeim
 */
@Data
public class WechatSendMessageForGetPackage {
    public WechatSendMessageForGetPackage(String dingDanLeiXing, String dingDanShangPin, String maiJiaXinXi, String shouHuoDiZhi, String shouHuoShiJian, String dingDanBianHao) {
        DingDanLeiXing = dingDanLeiXing;
        DingDanShangPin = dingDanShangPin;
        MaiJiaXinXi = maiJiaXinXi;
        ShouHuoDiZhi = shouHuoDiZhi;
        ShouHuoShiJian = shouHuoShiJian;
        DingDanBianHao = dingDanBianHao;
    }

    private String DingDanLeiXing;
    private String DingDanShangPin;
    private String MaiJiaXinXi;
    private String ShouHuoDiZhi;
    private String ShouHuoShiJian;
    private String DingDanBianHao;
}
