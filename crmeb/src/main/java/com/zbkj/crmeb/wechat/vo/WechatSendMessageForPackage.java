package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * 发货通知
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
public class WechatSendMessageForPackage {
    public WechatSendMessageForPackage(String peiSongFangShi, String dingDanHao, String shouHuoDiZhi, String keHuDianHua, String shouHuoRen) {
        PeiSongFangShi = peiSongFangShi;
        DingDanHao = dingDanHao;
        ShouHuoDiZhi = shouHuoDiZhi;
        KeHuDianHua = keHuDianHua;
        ShouHuoRen = shouHuoRen;
    }

    private String PeiSongFangShi;
    private String DingDanHao;
    private String ShouHuoDiZhi;
    private String KeHuDianHua;
    private String ShouHuoRen;
}
