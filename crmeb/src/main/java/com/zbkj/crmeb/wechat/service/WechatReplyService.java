package com.zbkj.crmeb.wechat.service;

import com.common.PageParamRequest;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.zbkj.crmeb.wechat.model.WechatReply;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.wechat.request.WechatReplySearchRequest;

import java.util.List;

/**
 *  WechatReplyService 接口
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
public interface WechatReplyService extends IService<WechatReply> {

    List<WechatReply> getList(WechatReplySearchRequest request, PageParamRequest pageParamRequest);

    Boolean create(WechatReply wechatReply);

    Boolean updateVo(WechatReply wechatReply);

    WechatReply getVoByKeywords(String keywords);

    WechatReply getInfoException(Integer id, boolean isTrue);

    WechatReply getInfo(Integer id);
}