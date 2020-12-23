package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * 提现结果通知
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
