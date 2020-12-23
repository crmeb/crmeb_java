package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * 订单取消通知
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
@Data
public class WechatSendMessageForOrderCancel {
    public WechatSendMessageForOrderCancel(String quXiaoYuanYin, String quXiaoShiJian, String menDianZiTi, String beiZhu, String dingDanLeiXing, String shangPinMingCheng, String dingDanHao, String dingDanZhuangTai, String dingDanJinE, String xiaDanShiJian, String shangChangMingCheng, String keFuDianHua, String dingDanBianHao, String menDianMingCheng, String lianXiFangShi) {
        QuXiaoYuanYin = quXiaoYuanYin;
        QuXiaoShiJian = quXiaoShiJian;
        MenDianZiTi = menDianZiTi;
        BeiZhu = beiZhu;
        DingDanLeiXing = dingDanLeiXing;
        ShangPinMingCheng = shangPinMingCheng;
        DingDanHao = dingDanHao;
        DingDanZhuangTai = dingDanZhuangTai;
        DingDanJinE = dingDanJinE;
        XiaDanShiJian = xiaDanShiJian;
        ShangChangMingCheng = shangChangMingCheng;
        KeFuDianHua = keFuDianHua;
        DingDanBianHao = dingDanBianHao;
        MenDianMingCheng = menDianMingCheng;
        LianXiFangShi = lianXiFangShi;
    }

    private String QuXiaoYuanYin;
    private String QuXiaoShiJian;
    private String MenDianZiTi;
    private String BeiZhu;
    private String DingDanLeiXing;
    private String ShangPinMingCheng;
    private String DingDanHao;
    private String DingDanZhuangTai;
    private String DingDanJinE;
    private String XiaDanShiJian;
    private String ShangChangMingCheng;
    private String KeFuDianHua;
    private String DingDanBianHao;
    private String MenDianMingCheng;
    private String LianXiFangShi;
}
