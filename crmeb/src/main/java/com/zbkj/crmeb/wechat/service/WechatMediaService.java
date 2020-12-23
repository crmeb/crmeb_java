package com.zbkj.crmeb.wechat.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface WechatMediaService {
    Map<String, String> upload(MultipartFile file, String type) throws Exception;
}
