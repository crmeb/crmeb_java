package com.zbkj.crmeb.wechat.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.wechat.model.WechatMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.wechat.request.WechatMessageSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description WechatMessageService 接口
* @since 2020-04-18
*/
public interface WechatMessageService extends IService<WechatMessage> {

    List<WechatMessage> getList(WechatMessageSearchRequest request, PageParamRequest pageParamRequest);
}