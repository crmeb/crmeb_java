package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * @Classname weChat
 * @Description 提现结果通知
 * @Date 9/10/20 2:09 下午
 * @Created by stivepeim
 */
@Data
public class WechatSendMessageForCash {
    public WechatSendMessageForCash(String tiXianZhuangTai, String tiXianJinE, String tiXianZhangHao, String tiXianShiJian, String tiXianShuoMing, String xingMing, String shouXuFei, String daKuanFangShi, String daKuanYuanYin, String tiXianDanHao, String tiXianFangShi, String shiBaiYuanYin, String huiYuanMingCheng) {
        TiXianZhuangTai = tiXianZhuangTai;
        TiXianJinE = tiXianJinE;
        TiXianZhangHao = tiXianZhangHao;
        TiXianShiJian = tiXianShiJian;
        TiXianShuoMing = tiXianShuoMing;
        XingMing = xingMing;
        ShouXuFei = shouXuFei;
        DaKuanFangShi = daKuanFangShi;
        DaKuanYuanYin = daKuanYuanYin;
        TiXianDanHao = tiXianDanHao;
        TiXianFangShi = tiXianFangShi;
        ShiBaiYuanYin = shiBaiYuanYin;
        HuiYuanMingCheng = huiYuanMingCheng;
    }

    private String TiXianZhuangTai;
    private String TiXianJinE;
    private String TiXianZhangHao;
    private String TiXianShiJian;
    private String TiXianShuoMing;
    private String XingMing;
    private String ShouXuFei;
    private String DaKuanFangShi;
    private String DaKuanYuanYin;
    private String TiXianDanHao;
    private String TiXianFangShi;
    private String ShiBaiYuanYin;
    private String HuiYuanMingCheng;
}
