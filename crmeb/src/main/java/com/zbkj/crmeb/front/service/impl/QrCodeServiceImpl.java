package com.zbkj.crmeb.front.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.RestTemplateUtil;
import com.zbkj.crmeb.front.service.QrCodeService;
import com.zbkj.crmeb.wechat.service.WeChatService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
* @author Mr.Zhang
* @Description QrCodeServiceImpl 接口实现
* @since 2020-04-13
*/
@Service
public class QrCodeServiceImpl implements QrCodeService {
    @Autowired
    private WeChatService weChatService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    /**
     * 二维码
     * @author Mr.Zhang
     * @since 2020-05-25
     * @return Object
     */
    @Override
    public Map<String, Object> get(JSONObject data) {
        Map<String, Object> map = new HashMap<>();
        StringBuilder scene = new StringBuilder();
        String page = "";
        try{
            if(null != data){
                Map<Object, Object> dataMap = JSONObject.toJavaObject(data, Map.class);

                for (Map.Entry<Object, Object> m : dataMap.entrySet()) {
                    if(m.getKey().equals("path")){
                        //前端路由， 不需要拼参数
                        page = m.getValue().toString();
                        continue;
                    }
                    scene.append(m.getKey()).append("=").append(m.getValue()).append("&");
                }
            }
        }catch (Exception e){
            throw new CrmebException("url参数错误 " + e.getMessage());
        }
        String uri = "";
        if(StringUtils.isNotBlank(scene)){
            uri = scene.substring(0, scene.length() - 1);
        }

        map.put("code", weChatService.qrCode(page, uri));
        return map;
    }

    @Override
    public Map<String, Object> base64(String url) {
        byte[] bytes = restTemplateUtil.getBuffer(url);
        String base64Image = CrmebUtil.getBase64Image(Base64.encodeBase64String(bytes));
        Map<String, Object> map = new HashMap<>();
        map.put("code", base64Image);
        return map;
    }
}

