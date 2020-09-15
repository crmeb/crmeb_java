package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * @Classname WechatSendMessageFor
 * @Description 核销成功通知
 * @Date 9/10/20 12:29 下午
 * @Created by stivepeim
 */
@Data
public class WechatSendMessageForVerSuccess {
    public WechatSendMessageForVerSuccess(String huoDongMingCheng, String shangPinMingCheng, String dingDanHao, String heXiaoShiJian, String heXiaoZongE, String beiZhu, String menDian) {
        HuoDongMingCheng = huoDongMingCheng;
        ShangPinMingCheng = shangPinMingCheng;
        DingDanHao = dingDanHao;
        HeXiaoShiJian = heXiaoShiJian;
        HeXiaoZongE = heXiaoZongE;
        BeiZhu = beiZhu;
        MenDian = menDian;
    }

    private String HuoDongMingCheng;
    private String ShangPinMingCheng;
    private String DingDanHao;
    private String HeXiaoShiJian;
    private String HeXiaoZongE;
    private String BeiZhu;
    private String MenDian;
}
