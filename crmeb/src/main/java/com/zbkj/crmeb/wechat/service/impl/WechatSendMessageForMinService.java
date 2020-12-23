package com.zbkj.crmeb.wechat.service.impl;

import com.constants.Constants;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import com.zbkj.crmeb.wechat.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * 根据已申请的模版发送小程序消息
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
@Service
public class WechatSendMessageForMinService implements com.zbkj.crmeb.wechat.service.WechatSendMessageForMinService {

    @Autowired
    private TemplateMessageService templateMessageService;

    /**
     * 发送积分到账消息
     * @param integral      待发送积分参数
     * @param userId        接收用户id
     */
    @Override
    public void sendIntegralMessage(WechatSendMessageForIntegral integral, Integer userId) {
        HashMap<String, String> mapPram = new HashMap<>();
        mapPram.put("shuoming", integral.getShangPinMingCheng());
        mapPram.put("dingdanbianhao", integral.getDingDanBianHao());
        mapPram.put("shangpinmingcheng", integral.getShangPinMingCheng());
        mapPram.put("zhifujine", integral.getZhiFuJinE());
        mapPram.put("huodejifen", integral.getHuoDeJiFen());
        mapPram.put("leijijifen", integral.getLeiJiJiFen());
        mapPram.put("jiaoyishijian", integral.getJiaoYiShiJian());
        mapPram.put("beizhu", integral.getBeiZhu());
        mapPram.put("mendian", integral.getMenDian());
        mapPram.put("daozhangyuanyin", integral.getDaoZhangYuanYin());
        templateMessageService.push(4+"", mapPram, userId, Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM);
    }

    /**
     * 退款申请通知
     *
     * @param reFundNotify 退款申请参数
     * @param userId       接收者用户Id
     */
    @Override
    public void sendReFundNotifyMessage(WechatSendMessageForReFundNotify reFundNotify, Integer userId) {
        HashMap<String, String> mapPram = new HashMap<>();
        mapPram.put("dingdanshangpin", reFundNotify.getDingDanShangPin());
        mapPram.put("dingdanjine", reFundNotify.getDingDanJinE());
        mapPram.put("xiadanshijian", reFundNotify.getXiaDanShiJian());
        mapPram.put("dingdanbianhao", reFundNotify.getDingDanBianHao());
        mapPram.put("shenqingshijian", reFundNotify.getShenQingShiJian());
        mapPram.put("shangjiamingcheng", reFundNotify.getShangJiaMingCheng());
        mapPram.put("tuikuanzhuangtai", reFundNotify.getTuiKuanZhuangTai());
        mapPram.put("beizhu", reFundNotify.getBeiZhu());
        mapPram.put("tuikuanjine", reFundNotify.getTuiKuanJinE());
        mapPram.put("tuikuanyuanyin", reFundNotify.getTuiKuanYuanYin());
        mapPram.put("yonghudianhua", reFundNotify.getYongHuDianHua());
        mapPram.put("sheibeimingcheng", reFundNotify.getSheBeiMingCheng());
        templateMessageService.push(5+"", mapPram, userId, Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM);
    }

    /**
     * 退款通知
     *
     * @param reFundEd 退款参数
     * @param userId   接收者用户Id
     */
    @Override
    public void sendReFundEdMessage(WechatSendMessageForReFundEd reFundEd, Integer userId) {
        HashMap<String, String> mapPram = new HashMap<>();
        mapPram.put("tuikuanzhuangtai", reFundEd.getTuiKuanZhuangTai());
        mapPram.put("tuikuanshangpin", reFundEd.getTuiKuanShangPin());
        mapPram.put("tuikuanjine", reFundEd.getTuiKuanJinE());
        mapPram.put("chulishijian", reFundEd.getChuLiShiJian());
        mapPram.put("tuikuanshuoming", reFundEd.getTuiKuanShuoMing());
        mapPram.put("tuikuandanhao", reFundEd.getTuiKuaDanHao());
        mapPram.put("dingdanbianhao", reFundEd.getDingDanBianHao());
        mapPram.put("xiadanshijian", reFundEd.getXiaDanShiJian());
        mapPram.put("zhifujine", reFundEd.getZhiFuJinE());
        mapPram.put("shangpinmingcheng", reFundEd.getShangPinMingChneg());
        mapPram.put("tuikuanyuanyin", reFundEd.getTuiKuanYuanYin());
        mapPram.put("mendianmingcheng", reFundEd.getMenDianMingCheng());
        mapPram.put("beizhu", reFundEd.getBeiZhu());
        mapPram.put("shebeimingcheng", reFundEd.getSheBeiMingCheng());
        templateMessageService.push(8+"", mapPram, userId, Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM);
    }

    /**
     * 付款成功通知
     *
     * @param paySuccess 付款成功参数
     * @param userId     接受者用户Id
     */
    @Override
    public void sendPaySuccessMessage(WechatSendMessageForPaySuccess paySuccess, Integer userId) {
        HashMap<String, String> mapPram = new HashMap<>();
        mapPram.put("fukuandanhao", paySuccess.getFuKuanDanHao());
        mapPram.put("fukuanjine", paySuccess.getFuKuanJinE());
        mapPram.put("fukuanshijian", paySuccess.getFuKuanShiJian());
        mapPram.put("wenxintishi", paySuccess.getWenXinTiShi());
        mapPram.put("dingdanjine", paySuccess.getDingDanJinE());
        mapPram.put("shangpinqingdan", paySuccess.getShangPinQingDan());
        templateMessageService.push(6+"", mapPram, userId, Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM);
    }

    /**
     * 充值成功过通知
     *
     * @param topped 充值成功通知
     * @param userId 接收者Id
     */
    @Override
    public void sendToppedMessage(WechatSendMessageForTopped topped, Integer userId) {
        HashMap<String, String> mapPram = new HashMap<>();
        mapPram.put("jiaoyidanhao", topped.getJiaoYiDanHao());
        mapPram.put("chongzhikahao", topped.getChongZhiKaHao());
        mapPram.put("chongzhijine", topped.getChongZhiJinE());
        mapPram.put("zhanghuyue", topped.getZhangHuYuE());
        mapPram.put("chongzhishijian", topped.getChongZhiShiJian());
        mapPram.put("zengsongjine", topped.getZengSongJinE());
        mapPram.put("beizhu", topped.getBeiZhu());
        mapPram.put("shifujine", topped.getShiFuJinE());
        mapPram.put("shanghumingcheng", topped.getShangHuMingCheng());
        mapPram.put("zhifufangshi", topped.getZhiFuFangShi());
        templateMessageService.push(7+"", mapPram, userId, Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM);
    }

    /**
     * 配送通知
     *
     * @param distrbution 配送参数
     * @param userId      接收者Id
     */
    @Override
    public void sendDistrbutionMessage(WechatSendMessageForDistrbution distrbution, Integer userId) {
        HashMap<String, String> mapPram = new HashMap<>();
        mapPram.put("dingdanbianhao", distrbution.getDingDanBianHao());
        mapPram.put("peisongrenyuan", distrbution.getPeiSongRenYuan());
        mapPram.put("lianxidianhua", distrbution.getLianXiDianHua());
        mapPram.put("dingdanzhuangtai", distrbution.getDingDanZhuangTai());
        mapPram.put("yujisongdashijian", distrbution.getYuJiSongDaShiJian());
        mapPram.put("shangpinmingcheng", distrbution.getShangPinMingCheng());
        mapPram.put("peisongshijian", distrbution.getPeiSongShiJian());
        templateMessageService.push(15+"", mapPram, userId, Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM);
    }

    /**
     * 提现通知
     *
     * @param cash   提现参数
     * @param userId 用户Id
     */
    @Override
    public void sendCashMessage(WechatSendMessageForCash cash, Integer userId) {
        HashMap<String, String> mapPram = new HashMap<>();
        mapPram.put("tixianzhuangtai", cash.getTiXianZhuangTai());
        mapPram.put("tixianjine", cash.getTiXianJinE());
        mapPram.put("tixianzhanghao", cash.getTiXianZhangHao());
        mapPram.put("tixianshijian", cash.getTiXianShiJian());
        mapPram.put("tixianshuoming", cash.getTiXianShuoMing());
        mapPram.put("xingming", cash.getXingMing());
        mapPram.put("shouxufei", cash.getShouXuFei());
        mapPram.put("dakuanfangshi", cash.getDaKuanFangShi());
        mapPram.put("dakuanyuanyin", cash.getDaKuanYuanYin());
        mapPram.put("tixiandanhao", cash.getTiXianDanHao());
        mapPram.put("tixianfangshi", cash.getTiXianFangShi());
        mapPram.put("shibaiyuanyin", cash.getShiBaiYuanYin());
        mapPram.put("huiyuanmingcheng", cash.getHuiYuanMingCheng());
        templateMessageService.push(14+"", mapPram, userId, Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM);
    }

    /**
     * 订单创建
     *
     * @param cateOrder 创建订单参数
     * @param userId    用户Id
     */
    @Override
    public void sendCreateOrderMessage(WechatSendMessageForCreateOrder cateOrder, Integer userId) {
        HashMap<String, String> mapPram = new HashMap<>();
        mapPram.put("dingdanleixing", cateOrder.getDingDanLeiXing());
        mapPram.put("dingdanshangpin", cateOrder.getDingDanShangPin());
        mapPram.put("dingdanjine", cateOrder.getDingDanJinE());
        mapPram.put("dingdanbianhao", cateOrder.getDingDanBianHao());
        mapPram.put("maijiaxinxi", cateOrder.getMaiJiaXinXi());
        mapPram.put("dingdanshijian", cateOrder.getDingDanShiJian());
        mapPram.put("wenxintishi", cateOrder.getWenXinTiShi());
        mapPram.put("tihuodizhi", cateOrder.getTiHuoDiZhi());
        mapPram.put("zhifufangshi", cateOrder.getZhiFuFangShi());
        mapPram.put("shouhuodizhi", cateOrder.getShouHuoDiZHi());
        templateMessageService.push(9+"", mapPram, userId, Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM);
    }

    /**
     * 收货通知
     *
     * @param getPackage 收货参数
     * @param userId     用户Id
     */
    @Override
    public void sendGetPackageMessage(WechatSendMessageForGetPackage getPackage, Integer userId) {
        HashMap<String, String> mapPram = new HashMap<>();
        mapPram.put("dingdanleixing", getPackage.getDingDanLeiXing());
        mapPram.put("dingdanshangpin", getPackage.getDingDanShangPin());
        mapPram.put("maijiaxinxi", getPackage.getMaiJiaXinXi());
        mapPram.put("shouhuodizhi", getPackage.getShouHuoDiZhi());
        mapPram.put("shouhuoshijian", getPackage.getShouHuoShiJian());
        mapPram.put("dingdanbianhao", getPackage.getDingDanBianHao());
        templateMessageService.push(13+"", mapPram, userId, Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM);
    }

    /**
     * 取消订单
     *
     * @param orderCancel 取消订单参数
     * @param userId      用户Id
     */
    @Override
    public void sendOrderCancelMessage(WechatSendMessageForOrderCancel orderCancel, Integer userId) {
        HashMap<String, String> mapPram = new HashMap<>();
        mapPram.put("quxiaoyuanyin", orderCancel.getQuXiaoYuanYin());
        mapPram.put("quxiaoshijian", orderCancel.getQuXiaoShiJian());
        mapPram.put("mendianziti", orderCancel.getMenDianZiTi());
        mapPram.put("beizhu", orderCancel.getBeiZhu());
        mapPram.put("dingdanleixing", orderCancel.getDingDanLeiXing());
        mapPram.put("shangpinmingcheng", orderCancel.getShangPinMingCheng());
        mapPram.put("dingdanhao", orderCancel.getDingDanHao());
        mapPram.put("dingdanzhuangtai", orderCancel.getDingDanZhuangTai());
        mapPram.put("dingdanjine", orderCancel.getDingDanJinE());
        mapPram.put("xiadanshijian", orderCancel.getXiaDanShiJian());
        mapPram.put("shangchangmingcheng", orderCancel.getShangPinMingCheng());
        mapPram.put("kefudianhua", orderCancel.getKeFuDianHua());
        mapPram.put("dingdanbianhao", orderCancel.getDingDanBianHao());
        mapPram.put("mendianmingcheng", orderCancel.getMenDianMingCheng());
        mapPram.put("lianxifangshi", orderCancel.getLianXiFangShi());
        templateMessageService.push(12+"", mapPram, userId, Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM);
    }

    /**
     * 发货通知
     *
     * @param toPackage 发货通知
     * @param userId    用户id
     */
    @Override
    public void sendRePackageMessage(WechatSendMessageForPackage toPackage, Integer userId) {
        HashMap<String, String> mapPram = new HashMap<>();
        mapPram.put("peisongfangshi", toPackage.getPeiSongFangShi());
        mapPram.put("dingdanhao", toPackage.getDingDanHao());
        mapPram.put("shouhuodizhi", toPackage.getShouHuoDiZhi());
        mapPram.put("kehudianhua", toPackage.getKeHuDianHua());
        mapPram.put("shouhuoren", toPackage.getShouHuoRen());
        templateMessageService.push(11+"", mapPram, userId, Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM);
    }

    /**
     * 核销通知
     *
     * @param ver 核销参数
     * @param userId    用户Id
     */
    @Override
    public void sendVerSuccessMessage(WechatSendMessageForVerSuccess ver, Integer userId) {
        HashMap<String, String> mapPram = new HashMap<>();
        mapPram.put("huodongmingcheng", ver.getHuoDongMingCheng());
        mapPram.put("shangpinmingcheng", ver.getShangPinMingCheng());
        mapPram.put("dingdanhao", ver.getDingDanHao());
        mapPram.put("hexiaoshijian", ver.getHeXiaoShiJian());
        mapPram.put("hexiaozonge", ver.getHeXiaoZongE());
        mapPram.put("beizhu", ver.getBeiZhu());
        mapPram.put("mendian", ver.getMenDian());
        templateMessageService.push(10+"", mapPram, userId, Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM);
    }
}
