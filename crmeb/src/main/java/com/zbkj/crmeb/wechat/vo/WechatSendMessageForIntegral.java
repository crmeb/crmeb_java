package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * @Classname WechatSendMessageForIntegral
 * @Description 赠送积分消息通知
 * @Date 9/10/20 10:38 上午
 * @Created by stivepeim
 */
@Data
public class WechatSendMessageForIntegral {

    public WechatSendMessageForIntegral(String shuoMing, String dingDanBianHao, String shangPinMingCheng, String zhiFuJinE, String huoDeJiFen, String leiJiJiFen, String jiaoYiShiJian, String beiZhu, String menDian, String daoZhangYuanYin) {
        ShuoMing = shuoMing;
        DingDanBianHao = dingDanBianHao;
        ShangPinMingCheng = shangPinMingCheng;
        ZhiFuJinE = zhiFuJinE;
        HuoDeJiFen = huoDeJiFen;
        LeiJiJiFen = leiJiJiFen;
        JiaoYiShiJian = jiaoYiShiJian;
        BeiZhu = beiZhu;
        MenDian = menDian;
        DaoZhangYuanYin = daoZhangYuanYin;
    }

    private String ShuoMing;
    private String DingDanBianHao;
    private String ShangPinMingCheng;
    private String ZhiFuJinE;
    private String HuoDeJiFen;
    private String LeiJiJiFen;
    private String JiaoYiShiJian;
    private String BeiZhu;
    private String MenDian;
    private String DaoZhangYuanYin;
}
