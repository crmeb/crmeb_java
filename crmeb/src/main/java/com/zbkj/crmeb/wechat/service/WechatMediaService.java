package com.zbkj.crmeb.wechat.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
* @author Mr.Zhang
* @Description WechatMediaService 接口
* @since 2020-04-18
*/
public interface WechatMediaService{
    Map<String, String> upload(MultipartFile file, String type) throws Exception;

    String getMedia(String mediaId);
}