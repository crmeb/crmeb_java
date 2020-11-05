package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * 收货通知
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
