package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * 赠送积分消息通知
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
