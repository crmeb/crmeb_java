package com.zbkj.crmeb.wechat.vo;

import lombok.Data;

/**
 * @Classname WechatSendMessageForPackage
 * @Description 发货通知
 * @Date 9/10/20 12:33 下午
 * @Created by stivepeim
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
