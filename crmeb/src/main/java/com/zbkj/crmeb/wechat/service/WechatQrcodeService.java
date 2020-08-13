package com.zbkj.crmeb.wechat.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.wechat.model.WechatQrcode;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.wechat.request.WechatQrcodeSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description WechatQrcodeService 接口
* @since 2020-04-18
*/
public interface WechatQrcodeService extends IService<WechatQrcode> {

    List<WechatQrcode> getList(WechatQrcodeSearchRequest request, PageParamRequest pageParamRequest);
}