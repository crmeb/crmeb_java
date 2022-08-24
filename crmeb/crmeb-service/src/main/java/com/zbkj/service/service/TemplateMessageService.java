package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.wechat.TemplateMessage;

import java.util.HashMap;
import java.util.List;

/**
 *  TemplateMessageService 接口
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface TemplateMessageService extends IService<TemplateMessage> {

    /**
     * 公众号消费队列消费
     */
    void consumePublic();

    /**
     * 小程序消费队列消费
     */
    void consumeProgram();

    /**
     * 发送公众号模板消息
     * @param templateId 模板消息编号
     * @param temMap 内容Map
     * @param openId 微信用户openId
     */
    void pushTemplateMessage(Integer templateId, HashMap<String, String> temMap, String openId);

    /**
     * 发送小程序订阅消息
     * @param templateId 模板消息编号
     * @param temMap 内容Map
     * @param openId 微信用户openId
     */
    void pushMiniTemplateMessage(Integer templateId, HashMap<String, String> temMap, String openId);

    /**
     * 修改模板状态
     * @param id 模板id
     * @param status 状态
     */
    Boolean updateStatus(Integer id, Integer status);

    /**
     * 公众号模板消息同步
     * @return Boolean
     */
    Boolean whcbqhnSync();

    /**
     * 小程序订阅消息同步
     * @return Boolean
     */
    Boolean routineSync();

    /**
     * 获取详情
     * @param id id
     */
    TemplateMessage info(Integer id);

    /**
     * 获取模板列表
     * @param tidList id数组
     * @return List
     */
    List<TemplateMessage> getByIdList(List<Integer> tidList);
}
