package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * 核销成功通知
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
