package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * 付款成功通知
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
