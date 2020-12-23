package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * 配送通知
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
