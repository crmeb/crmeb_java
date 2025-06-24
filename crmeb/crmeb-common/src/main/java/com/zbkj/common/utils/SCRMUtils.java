package com.zbkj.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @Auther: 大粽子
 * @Date: 2023/9/9 15:19
 * @Description: For SCRM
 */

@Component
public class SCRMUtils {

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    private String scrm_addUser_URL = "http://api.scrm.crmeb.com/api/contactBatchAdd";

    public  void addUserForSCRM(String phone, String nikename){
        String type = "java单商户";
        HashMap addUserPram = new HashMap<>();
        addUserPram.put("phone", phone);
        addUserPram.put("nikename", nikename);
        addUserPram.put("type", type);
        String result = restTemplateUtil.postMapData(scrm_addUser_URL, addUserPram);
        System.out.println("result:"+result);
    }



}
