package com.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.constants.OnePassConstants;
import com.exception.CrmebException;
import com.zbkj.crmeb.pass.vo.OnePassLoginVo;
import com.zbkj.crmeb.system.service.SystemConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 一号通工具类
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
@Component
public class OnePassUtil {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private RestTemplateUtil restTemplateUtil;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取一号通登录对象
     *
     * @return
     */
    public OnePassLoginVo getLoginVo() {
        String account = systemConfigService.getValueByKey("sms_account");// 获取配置账号
        if (StrUtil.isBlank(account)) {
            throw new CrmebException("请配置一号通账号！");
        }
        String token = systemConfigService.getValueByKey("sms_token"); //获取配置密码
        if (StrUtil.isBlank(token)) {
            throw new CrmebException("请配置一号通密码！");
        }
        String secret = SecureUtil.md5(account + SecureUtil.md5(token));
        OnePassLoginVo loginVo = new OnePassLoginVo();
        loginVo.setAccount(account);
        loginVo.setSecret(secret);
        return loginVo;
    }

    /**
     * 获取一号通token
     *
     * @return
     */
    public String getToken(OnePassLoginVo loginVo) {
        boolean exists = redisUtil.exists(StrUtil.format(OnePassConstants.ONE_PASS_TOKEN_KEY_PREFIX, loginVo.getSecret()));
        if (exists) {
            Object token = redisUtil.get(StrUtil.format(OnePassConstants.ONE_PASS_TOKEN_KEY_PREFIX, loginVo.getSecret()));
            return token.toString();
        }
        // 缓存中不存在token，重新获取，存入缓存
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("account", loginVo.getAccount());
        map.add("secret", loginVo.getSecret());
        JSONObject jsonObject = postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.USER_LOGIN_URI, map, null);
        String accessToken = "";
        Long expiresIn = 0L;
        accessToken = OnePassConstants.ONE_PASS_USER_TOKEN_PREFIX.concat(jsonObject.getJSONObject("data").getString("access_token"));
        expiresIn = jsonObject.getJSONObject("data").getLong("expires_in");
        expiresIn = expiresIn - DateUtil.getTime();
        redisUtil.set(StrUtil.format(OnePassConstants.ONE_PASS_TOKEN_KEY_PREFIX, loginVo.getSecret()), accessToken, expiresIn, TimeUnit.SECONDS);
        return accessToken;
    }

    /**
     * 获取一号通token
     *
     * @return
     */
    public String getToken() {
        OnePassLoginVo loginVo = getLoginVo();
        boolean exists = redisUtil.exists(StrUtil.format(OnePassConstants.ONE_PASS_TOKEN_KEY_PREFIX, loginVo.getSecret()));
        if (exists) {
            Object token = redisUtil.get(StrUtil.format(OnePassConstants.ONE_PASS_TOKEN_KEY_PREFIX, loginVo.getSecret()));
            return token.toString();
        }
        // 缓存中不存在token，重新获取，存入缓存
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("account", loginVo.getAccount());
        map.add("secret", loginVo.getSecret());
        JSONObject jsonObject = postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.USER_LOGIN_URI, map, null);
        String accessToken = "";
        Long expiresIn = 0L;
        accessToken = OnePassConstants.ONE_PASS_USER_TOKEN_PREFIX.concat(jsonObject.getJSONObject("data").getString("access_token"));
        expiresIn = jsonObject.getJSONObject("data").getLong("expires_in");
        expiresIn = expiresIn - DateUtil.getTime();
        redisUtil.set(StrUtil.format(OnePassConstants.ONE_PASS_TOKEN_KEY_PREFIX, loginVo.getSecret()), accessToken, expiresIn, TimeUnit.SECONDS);
        return accessToken;
    }

    /**
     * 清除token
     */
    public void removeToken(OnePassLoginVo loginVo) {
        boolean exists = redisUtil.exists(StrUtil.format(OnePassConstants.ONE_PASS_TOKEN_KEY_PREFIX, loginVo.getSecret()));
        if (exists) {
            redisUtil.remove(StrUtil.format(OnePassConstants.ONE_PASS_TOKEN_KEY_PREFIX, loginVo.getSecret()));
        }
    }

    /**
     * 清除token
     */
    public void removeToken() {
        OnePassLoginVo loginVo = getLoginVo();
        boolean exists = redisUtil.exists(StrUtil.format(OnePassConstants.ONE_PASS_TOKEN_KEY_PREFIX, loginVo.getSecret()));
        if (exists) {
            redisUtil.remove(StrUtil.format(OnePassConstants.ONE_PASS_TOKEN_KEY_PREFIX, loginVo.getSecret()));
        }
    }

    /**
     * post请求from表单模式提交
     */
    public JSONObject postFrom(String url, MultiValueMap<String, Object> param, Map<String, String> header) {
        String result = restTemplateUtil.postFromUrlencoded(url, param, header);
        return checkResult(result);
    }

    /**
     * 检测结构请求返回的数据
     *
     * @param result 接口返回的结果
     * @return JSONObject
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    private JSONObject checkResult(String result) {
        if (StringUtils.isBlank(result)) {
            throw new CrmebException("一号通平台接口异常，没任何数据返回！");
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(result);
        } catch (Exception e) {
            throw new CrmebException("一号通平台接口异常！");
        }
        if (OnePassConstants.ONE_PASS_ERROR_CODE.equals(jsonObject.getInteger("status"))) {
            throw new CrmebException("一号通平台接口" + jsonObject.getString("msg"));
        }
        return jsonObject;
    }

    /**
     * 获取请求的headerMap
     *
     * @param accessToken
     * @return
     */
    public HashMap<String, String> getCommonHeader(String accessToken) {
        HashMap<String, String> header = CollUtil.newHashMap();
        header.put("Authorization", accessToken);
        return header;
    }
}
