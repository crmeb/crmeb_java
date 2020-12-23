package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * 充值成功
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
public class WechatSendMessageForTopped {
    public WechatSendMessageForTopped(String jiaoYiDanHao, String chongZhiKaHao, String chongZhiJinE, String zhangHuYuE, String chongZhiShiJian, String zengSongJinE, String beiZhu, String shiFuJinE, String shangHuMingCheng, String zhiFuFangShi) {
        JiaoYiDanHao = jiaoYiDanHao;
        ChongZhiKaHao = chongZhiKaHao;
        ChongZhiJinE = chongZhiJinE;
        ZhangHuYuE = zhangHuYuE;
        ChongZhiShiJian = chongZhiShiJian;
        ZengSongJinE = zengSongJinE;
        BeiZhu = beiZhu;
        ShiFuJinE = shiFuJinE;
        ShangHuMingCheng = shangHuMingCheng;
        ZhiFuFangShi = zhiFuFangShi;
    }

    private String JiaoYiDanHao;
    private String ChongZhiKaHao;
    private String ChongZhiJinE;
    private String ZhangHuYuE;
    private String ChongZhiShiJian;
    private String ZengSongJinE;
    private String BeiZhu;
    private String ShiFuJinE;
    private String ShangHuMingCheng;
    private String ZhiFuFangShi;
}
