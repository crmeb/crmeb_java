package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * 新订单提醒
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
