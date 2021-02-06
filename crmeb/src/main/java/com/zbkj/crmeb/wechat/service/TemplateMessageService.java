package com.zbkj.crmeb.wechat.service;

import com.common.PageParamRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.wechat.model.TemplateMessage;
import com.zbkj.crmeb.wechat.request.TemplateMessageSearchRequest;
import com.zbkj.crmeb.wechat.vo.TemplateMessageIndustryVo;

import java.util.HashMap;
import java.util.List;

/**
 *  TemplateMessageService 接口
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
public interface TemplateMessageService extends IService<TemplateMessage> {

    List<TemplateMessage> getList(TemplateMessageSearchRequest request, PageParamRequest pageParamRequest);

    void push(String tempKey, HashMap<String, String> map, Integer userId, String type);

    TemplateMessage infoException(Integer id);

    void consumePublic();

    void consumeProgram();

    TemplateMessageIndustryVo getIndustry();

    /**
     * 发送公众号模板消息
     * @param templateNo 模板消息编号
     * @param temMap 内容Map
     * @param openId 微信用户openId
     */
    void pushTemplateMessage(String templateNo, HashMap<String, String> temMap, String openId);

    /**
     * 发送小程序订阅消息
     * @param templateNo 模板消息编号
     * @param temMap 内容Map
     * @param openId 微信用户openId
     */
    void pushMiniTemplateMessage(String templateNo, HashMap<String, String> temMap, String openId);

    /**
     * 获取小程序订阅模板编号
     * @param type 场景类型(支付之前：beforePay|支付成功：afterPay|申请退款：refundApply|充值之前：beforeRecharge|创建砍价：createBargain|参与拼团：pink|取消拼团：cancelPink)
     * @return
     */
    List<TemplateMessage> getMiniTempList(String type);
}