package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * @Classname WechatSendMessageForDistrbution
 * @Description 配送通知
 * @Date 9/10/20 2:21 下午
 * @Created by stivepeim
 */
@Data
public class WechatSendMessageForDistrbution {
    public WechatSendMessageForDistrbution(String dingDanBianHao, String peiSongRenYuan, String lianXiDianHua, String dingDanZhuangTai, String yuJiSongDaShiJian, String shangPinMingCheng, String peiSongShiJian) {
        DingDanBianHao = dingDanBianHao;
        PeiSongRenYuan = peiSongRenYuan;
        LianXiDianHua = lianXiDianHua;
        DingDanZhuangTai = dingDanZhuangTai;
        YuJiSongDaShiJian = yuJiSongDaShiJian;
        ShangPinMingCheng = shangPinMingCheng;
        PeiSongShiJian = peiSongShiJian;
    }

    private String DingDanBianHao;
    private String PeiSongRenYuan;
    private String LianXiDianHua;
    private String DingDanZhuangTai;
    private String YuJiSongDaShiJian;
    private String ShangPinMingCheng;
    private String PeiSongShiJian;
}
