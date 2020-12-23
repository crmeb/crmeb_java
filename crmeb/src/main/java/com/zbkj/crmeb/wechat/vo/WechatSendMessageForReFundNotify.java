package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * 退款申请通知
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
public class WechatSendMessageForReFundNotify {
    public WechatSendMessageForReFundNotify(String dingDanShangPin, String dingDanJinE, String xiaDanShiJian, String dingDanBianHao, String shenQingShiJian, String shangJiaMingCheng, String tuiKuanZhuangTai, String beiZhu, String tuiKuanJinE, String tuiKuanYuanYin, String yongHuDianHua, String sheBeiMingCheng) {
        DingDanShangPin = dingDanShangPin;
        DingDanJinE = dingDanJinE;
        XiaDanShiJian = xiaDanShiJian;
        DingDanBianHao = dingDanBianHao;
        ShenQingShiJian = shenQingShiJian;
        ShangJiaMingCheng = shangJiaMingCheng;
        TuiKuanZhuangTai = tuiKuanZhuangTai;
        BeiZhu = beiZhu;
        TuiKuanJinE = tuiKuanJinE;
        TuiKuanYuanYin = tuiKuanYuanYin;
        YongHuDianHua = yongHuDianHua;
        SheBeiMingCheng = sheBeiMingCheng;
    }

    private String DingDanShangPin;
    private String DingDanJinE;
    private String XiaDanShiJian;
    private String DingDanBianHao;
    private String ShenQingShiJian;
    private String ShangJiaMingCheng;
    private String TuiKuanZhuangTai;
    private String BeiZhu;
    private String TuiKuanJinE;
    private String TuiKuanYuanYin;
    private String YongHuDianHua;
    private String SheBeiMingCheng;
}
