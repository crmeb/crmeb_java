package com.zbkj.crmeb.front.service;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
* @author Mr.Zhang
* @Description QrCodeService 接口
* @since 2020-04-13
*/
public interface QrCodeService {
    Map<String, Object> get(JSONObject data) throws IOException;

    Map<String, Object> base64(String url);
}