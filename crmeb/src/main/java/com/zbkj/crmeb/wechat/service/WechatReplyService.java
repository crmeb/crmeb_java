package com.zbkj.crmeb.wechat.service;

import com.common.PageParamRequest;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.zbkj.crmeb.wechat.model.WechatReply;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.wechat.request.WechatReplySearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description WechatReplyService 接口
* @since 2020-04-18
*/
public interface WechatReplyService extends IService<WechatReply> {

    List<WechatReply> getList(WechatReplySearchRequest request, PageParamRequest pageParamRequest);

    Boolean create(WechatReply wechatReply);

    Boolean updateVo(WechatReply wechatReply);

    WechatReply getVoByKeywords(String keywords);

    WechatReply getInfoException(Integer id, boolean isTrue);

    WechatReply getInfo(Integer id);
}