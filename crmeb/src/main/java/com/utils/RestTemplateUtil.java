package com.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 * httpClient 工具类
 */

@Component
public class RestTemplateUtil {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 发送GET请求
     * @param url
     * @param param
     * @return
     */

    public String getData(String url, Map<String, String> param) {
        // 请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return restTemplate.getForEntity(url, String.class, param).getBody();
    }

    /**
     * 发送GET请求
     * @param url
     * @return
     */

    public String getLink(String url) {
        return restTemplate.getForEntity(url, String.class).getBody();
    }


    /**
     * 发送GET请求
     * @param url
     * @return
     */

    public JSONObject getData(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity =
                new HttpEntity<>( headers);

        return restTemplate.exchange( url, HttpMethod.GET, requestEntity, JSONObject.class).getBody();
    }

    /**
     * 发送GET请求 支持header
     * @param url
     * @return
     */

    public JSONObject getData(String url, HashMap<String, String> hashMap) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            headers.add(entry.getKey(), entry.getValue());
        }

            HttpEntity<Map<String, Object>> requestEntity =
                new HttpEntity<>( headers);

        return restTemplate.exchange( url, HttpMethod.GET, requestEntity, JSONObject.class).getBody();
    }


    /**
     * 发送POST-JSON请求
     *
     * @param url
     * @param param
     * @return
     */

    public String postJsonData(String url, JSONObject param) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        HttpEntity<JSONObject> requestEntity = new HttpEntity<JSONObject>(param, headers);

        return restTemplate.postForEntity(url, param, String.class).getBody();

    }

    /**
     * 发送POST 表单请求
     *
     * @param url
     * @return
     */

    public String postFormData(String url, MultiValueMap<String, String> map) {
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> requests = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        String body = restTemplate.postForEntity(url, requests, String.class).getBody();
        return body;


    }




    /**
     * 发送POST-JSON请求
     *
     * @param url
     * @param param
     * @return
     */

    public JSONObject postJsonDataAndReturnJson(String url, JSONObject param) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        HttpEntity<JSONObject> requestEntity = new HttpEntity<JSONObject>(param, headers);

        JSONObject body = restTemplate.postForEntity(url, requestEntity, JSONObject.class).getBody();

        return body;

    }

    /**
     * 发送POST-JSON请求
     *
     * @param url
     * @return
     */

    public JSONObject post(String url) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        HttpEntity<String> requestEntity = new HttpEntity<>( headers);

        JSONObject body = restTemplate.postForEntity(url, requestEntity, JSONObject.class).getBody();

        return body;

    }

    /**
     * 发送POST-JSON请求
     *
     * @param url
     * @return
     */

    public String postXml(String url, String xml){
        HttpHeaders headers = new HttpHeaders();

        MediaType type = MediaType.parseMediaType("application/xml; charset=UTF-8");
        headers.setContentType(type);
        HttpEntity<String> requestEntity = new HttpEntity<>(xml, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        try{
            return new String(Objects.requireNonNull(responseEntity.getBody()).getBytes("ISO8859-1"), StandardCharsets.UTF_8);
        }catch (Exception e){
            return "";
        }
    }

    /**
     * 发送POST 表单请求
     *
     * @param url
     * @param params
     * @return
     *
     * 表单提交，返回数据分为两部分，一个代表成功，一个是返回的数据内容
     */

    public String postFormData(String url, LinkedMultiValueMap<String, Object> params ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class);

        String body = responseEntity.getBody();

        return body;

    }



    public String postMapData(String url, Map<String, Object> params ) {

        //cs
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        HttpEntity<Map<String, Object>> requestEntity =
                new HttpEntity<>(params, headers);

        String body1 = restTemplate.postForEntity( url, requestEntity, String.class).getBody();


        System.out.println(body1);

        return  body1;

    }

    public String postMapData(String url, Map<String, Object> params,  Map<String, String> header) {

        //cs
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        for (Map.Entry<String, String> entry : header.entrySet()) {
            headers.add(entry.getKey(), entry.getValue());
        }

        HttpEntity<Map<String, Object>> requestEntity =
                new HttpEntity<>(params, headers);

        String body1 = restTemplate.postForEntity( url, requestEntity, String.class).getBody();


        System.out.println(body1);

        return  body1;

    }


    public String postStringData(String url,String data ) {

        //cs
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        HttpEntity<String> requestEntity =
                new HttpEntity<>(data, headers);

        String body1 = restTemplate.postForEntity(url, requestEntity, String.class).getBody();


        System.out.println(body1);

        return body1;

    }

    public byte[] postJsonDataAndReturnBuffer(String url, JSONObject param) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        HttpEntity<JSONObject> requestEntity = new HttpEntity<JSONObject>(param, headers);

        return restTemplate.postForEntity(url, param, byte[].class).getBody();
    }


    public byte[] getBuffer(String url) {
        return restTemplate.getForEntity(url, byte[].class).getBody();
    }

    /**
     * post——from-urlencoded格式请求
     */
    public String postFromUrlencoded(String url, MultiValueMap<String, Object> params, Map<String, String> header) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        if (CollUtil.isNotEmpty(header)) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                headers.add(entry.getKey(), entry.getValue());
            }
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(params, headers);

        String body = restTemplate.postForEntity( url, requestEntity, String.class).getBody();
        return  body;
    }
}
